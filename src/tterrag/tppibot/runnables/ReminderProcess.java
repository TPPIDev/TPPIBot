package tterrag.tppibot.runnables;

import tterrag.tppibot.TPPIBot;

public class ReminderProcess implements Runnable
{
    private TPPIBot bot;

    public ReminderProcess(TPPIBot bot)
    {
        this.bot = bot;
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
                        bot.remind(channel);
                    }
                }
                sleep(300000);
            }
            else
            {
                sleep(1000);
            }
        }
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
