package tterrag.tppibot.runnables;

import static tterrag.tppibot.util.Logging.log;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;

import tterrag.tppibot.Main;

public class ReminderProcess implements Runnable
{
    private PircBotX bot;

    private HashMap<String, Boolean> reminderMap;

    private static Queue<String> reminders;

    public ReminderProcess(PircBotX bot, String... strings)
    {
        this.bot = bot;
        reminders = new LinkedList<String>();

        for (String s : strings)
        {
            reminders.add(s);
        }
        
        reminderMap = new HashMap<String, Boolean>();
    }

    @Override
    public void run()
    {
        while (true)
        {
            if (bot.isConnected())
            {
                String reminder = (String) reminders.poll();
                for (Channel channel : bot.getUserBot().getChannels())
                {
                    if (reminderMap.get(channel.getName()))
                    {
                        remind(channel, reminder);
                    }
                }
                reminders.add(reminder);
                log("Sleeping reminder thread...");
                sleep(300000);
            }
            else
            {
                log("Bot not connected, waiting...");
                sleep(10000);
            }
        }
    }

    private void remind(Channel channel, String reminder)
    {
        synchronized (reminders)
        {
            log("Sending reminder!");
            Main.getBot().sendIRC().message(channel.getName(), reminder);
        }
    }
    
    public static void addReminder(String reminder)
    {
        synchronized (reminders)
        {
            reminders.add(reminder);
        }
    }
    
    public void disableRemindersFor(String channel)
    {
        reminderMap.put(channel, false);
    }
    
    public void enableRemindersFor(String channel)
    {
        reminderMap.put(channel, true);
    }
    
    public boolean isRemindEnabledFor(String channel)
    {
        return channel == null ? false : reminderMap.get(channel.toLowerCase());
    }

    private void sleep(int millis)
    {
        try
        {
            Thread.sleep(millis);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
