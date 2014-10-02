package tterrag.tppibot.runnables;

import static tterrag.tppibot.util.Logging.*;
import static tterrag.tppibot.util.ThreadUtils.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.DisconnectEvent;

import tterrag.tppibot.Main;
import tterrag.tppibot.annotations.Subscribe;
import tterrag.tppibot.config.Config;
import tterrag.tppibot.util.IRCUtils;
import tterrag.tppibot.util.Logging;

import com.google.gson.reflect.TypeToken;

public class ReminderProcess implements Runnable
{
    private static class RemindTime
    {
        private long init;
        private long time;
        private int index;

        private RemindTime(long init, long time, int index)
        {
            this.init = init;
            this.time = time;
            this.index = index;
        }
    }

    private PircBotX bot;

    private Map<String, Boolean> reminderMap;

    private List<String> reminders;

    private Map<String, RemindTime> delayMap;

    private Config mapConfig, remindersConfig, delayConfig;

    public ReminderProcess(PircBotX bot, String... defaults)
    {
        this.bot = bot;

        mapConfig = new Config("reminderMap.json");
        remindersConfig = new Config("reminders.json");
        delayConfig = new Config("reminderDelays.json");

        reminderMap = Config.gson.fromJson(mapConfig.getText(), new TypeToken<Map<String, Boolean>>() {}.getType());

        if (reminderMap == null)
        {
            reminderMap = new HashMap<String, Boolean>();
        }

        reminders = Config.gson.fromJson(remindersConfig.getText(), new TypeToken<List<String>>() {}.getType());

        if (reminders == null)
        {
            reminders = new LinkedList<String>();
            for (String s : defaults)
            {
                reminders.add(s);
            }
        }

        delayMap = Config.gson.fromJson(delayConfig.getText(), new TypeToken<Map<String, RemindTime>>() {}.getType());

        if (delayMap == null)
        {
            delayMap = new HashMap<String, RemindTime>();
        }
    }

    @Override
    public void run()
    {
        while (true)
        {
            try
            {
                sleep(1000);

                if (bot.isConnected())
                {
                    long time = System.currentTimeMillis();
                    for (Channel channel : bot.getUserBot().getChannels())
                    {
                        RemindTime remind = delayMap.get(channel.getName());
                        if (Main.reminderCommand.delayMap.get(channel.getName()) == null)
                        {
                            Main.reminderCommand.delayMap.put(channel.getName(), 900000L);
                        }

                        if (remind == null)
                        {
                            delayMap.put(channel.getName(), new RemindTime(time, Main.reminderCommand.delayMap.get(channel.getName()), 0));
                        }

                        String reminder = reminders.get(remind.index % reminders.size()); // make sure removal hasn't shifted indeces

                        if (time - remind.init > remind.time && isRemindEnabledFor(channel.getName()))
                        {
                            remind(channel, reminder);
                            delayMap.put(channel.getName(), new RemindTime(time, Main.reminderCommand.delayMap.get(channel.getName()), nextIndex(remind.index)));
                        }
                    }
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
        }
    }
    
    private int nextIndex(int index)
    {
        return (index + 1) % reminders.size();
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
    
    public void removeReminder(int index)
    {
        reminders.remove(index);
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
        channel = IRCUtils.fmtChan(channel);

        return channel == null ? false : reminderMap.get(channel.toLowerCase());
    }

    public boolean isInReminderMap(String channel)
    {
        channel = IRCUtils.fmtChan(channel);

        return reminderMap.containsKey(channel.toLowerCase());
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent<PircBotX> event)
    {
        mapConfig.writeJsonToFile(reminderMap);
        remindersConfig.writeJsonToFile(reminders);
        delayConfig.writeJsonToFile(delayMap);
    }

    public void setDelay(String name, long millis)
    {
        if (delayMap.get(name) == null)
        {
            delayMap.put(name, new RemindTime(System.currentTimeMillis(), millis, 0));
        }
        else
        {
            delayMap.get(name).time = millis;
        }
    }

    public String[] getReminders()
    {
        return (String[]) reminders.toArray();
    }
}
