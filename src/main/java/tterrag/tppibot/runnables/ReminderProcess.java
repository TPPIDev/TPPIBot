package tterrag.tppibot.runnables;

import static tterrag.tppibot.util.Logging.log;
import static tterrag.tppibot.util.ThreadUtils.sleep;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.DisconnectEvent;

import tterrag.tppibot.annotations.Subscribe;
import tterrag.tppibot.config.Config;
import tterrag.tppibot.util.Logging;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ReminderProcess implements Runnable
{
    private PircBotX bot;

    private HashMap<String, Boolean> reminderMap;

    private Queue<String> reminders;
    
    private Config mapConfig, remindersConfig;

    private String inFlux = null;
    
    public ReminderProcess(PircBotX bot, String... defaults)
    {
        this.bot = bot;
        
        mapConfig = new Config("reminderMap.json");
        remindersConfig = new Config("reminders.json");
        
        reminderMap = new Gson().fromJson(mapConfig.getText(), new TypeToken<HashMap<String, Boolean>>(){}.getType());

        if (reminderMap == null)
            reminderMap = new HashMap<String, Boolean>();
        
        reminders = new Gson().fromJson(remindersConfig.getText(), new TypeToken<Queue<String>>(){}.getType());

        if (reminders == null)
        {
            reminders = new LinkedList<String>();
            for (String s : defaults)
            {
                reminders.add(s);
            }
        }
    }

    @Override
    public void run()
    {
        sleep(150000);
        while (true)
        {
            inFlux = reminders.poll();
            try
            {
                if (bot.isConnected())
                {
                    for (Channel channel : bot.getUserBot().getChannels())
                    {
                        if (isRemindEnabledFor(channel.getName()))
                        {
                             remind(channel, inFlux);
                        }
                    }
                    log("Sleeping reminder thread...");
                    sleep(600000);
                }
                else
                {
                    log("Bot not connected, waiting...");
                    sleep(10000);
                }
            }
            catch (Throwable t)
            {
                t.printStackTrace();
                Logging.error("An error occured with the Reminder Process, continuing...");
                sleep(10000);
            }
            finally
            {
                reminders.add(inFlux);
                inFlux = null;
            }
        }
    }

    private void remind(Channel channel, String reminder)
    {
        synchronized (reminders)
        {
            log("Sending reminder!");
            channel.getBot().sendIRC().message(channel.getName(), reminder);
        }
    }

    public void addReminder(String reminder)
    {
        synchronized (reminders)
        {
            reminders.add(reminder);
        }
    }

    public void disableRemindersFor(String channel)
    {
        reminderMap.put(channel.toLowerCase(), false);
    }

    public void enableRemindersFor(String channel)
    {
        reminderMap.put(channel.toLowerCase(), true);
    }

    public boolean isRemindEnabledFor(String channel)
    {
        if (!channel.startsWith("#"))
            channel = "#" + channel;

        return channel == null ? false : reminderMap.get(channel.toLowerCase());
    }
    
    public boolean isInReminderMap(String channel)
    {
        if (!channel.startsWith("#"))
            channel = "#" + channel;
        
        return reminderMap.containsKey(channel.toLowerCase());
    }
    
    @Subscribe
    public void onDisconnect(DisconnectEvent<PircBotX> event)
    {
        mapConfig.writeJsonToFile(reminderMap);
        
        if (inFlux != null)
        {
            reminders.add(inFlux);
        }
        
        remindersConfig.writeJsonToFile(reminders);
    }
}
