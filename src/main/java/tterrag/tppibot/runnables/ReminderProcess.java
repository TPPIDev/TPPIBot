package tterrag.tppibot.runnables;

import static tterrag.tppibot.util.Logging.log;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;

public class ReminderProcess implements Runnable
{
    private PircBotX bot;

    private HashMap<String, Boolean> reminderMap;

    private Queue<String> reminders;

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
        sleep(150000);
        while (true)
        {
            String reminder = reminders.poll();
            try
            {
                if (bot.isConnected())
                {
                    for (Channel channel : bot.getUserBot().getChannels())
                    {
                        if (isRemindEnabledFor(channel.getName()))
                        {
                             remind(channel, reminder);
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
                sleep(10000);
            }
            finally
            {
                reminders.add(reminder);
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
