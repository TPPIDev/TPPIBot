package tterrag.tppibot.runnables;

import org.jibble.pircbot.Queue;

import tterrag.tppibot.Main;
import tterrag.tppibot.TPPIBot;
import static tterrag.tppibot.util.Logging.*;

public class ReminderProcess implements Runnable
{
    private TPPIBot bot;
    
    private Queue reminders;

    public ReminderProcess(TPPIBot bot, String... reminders)
    {
        this.bot = bot;
        this.reminders = new Queue();
        
        for (String s : reminders)
        {
            this.reminders.add(s);
        }
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
                    if (channel != null) {
                        remind(channel);
                    }
                }
                log("Sleeping for 5 minutes...");
                sleep(300000);
            }
            else
            {
                log("Bot not connected, waiting five seconds...");
                sleep(5000);
            }
        }
    }
    

    private void remind(String channel)
    {
        String remind = (String) reminders.next();
        log("Sending reminder!");
        Main.bot.sendMessage(channel, remind);
        reminders.add(remind);
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
