package tterrag.tppibot.runnables;

import java.util.HashMap;

import org.jibble.pircbot.Queue;

import tterrag.tppibot.Main;
import tterrag.tppibot.TPPIBot;
import static tterrag.tppibot.util.Logging.*;

public class ReminderProcess implements Runnable
{
    private TPPIBot bot;

    private HashMap<String, Boolean> reminderMap;

    private static Queue reminders;

    public ReminderProcess(TPPIBot bot, String... strings)
    {
        this.bot = bot;
        reminders = new Queue();

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
                for (String channel : bot.getChannels())
                {
                    if (channel != null && reminderMap.get(channel))
                    {
                        remind(channel);
                    }
                }
                log("Sleeping for 5 minutes...");
                sleep(300000);
            }
            else
            {
                log("Bot not connected, waiting five seconds...");
                sleep(10000);
            }
        }
    }

    private void remind(String channel)
    {
        synchronized (reminders)
        {
            String remind = (String) reminders.next();
            log("Sending reminder!");
            Main.getBot().sendMessage(channel, remind);
            reminders.add(remind);
        }
    }
    
    public static void addReminder(String reminder)
    {
        synchronized (reminders)
        {
            reminders.addFront(reminder);
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
        return channel == null ? false : reminderMap.get(channel);
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
