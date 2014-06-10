package tterrag.tppibot.commands;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;

import tterrag.tppibot.Main;

public class Kill extends Command
{
    public Kill()
    {
        super("kill", PermLevel.CONTROLLER);
    }

    @Override
    public boolean onCommand(MessageEvent<?> event, String... args)
    {
        sendMessage(event.getChannel(), "NOOOOooooo...");
        
        killBot(event.getBot());
        
        return true;
    }
    
    public static boolean killBot(PircBotX bot)
    {
        try
        {
            Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        if (bot.isConnected())
        {
            bot.stopBotReconnect();
            bot.sendIRC().quitServer("x.x");
        }
        
        try
        {
            Thread.sleep(2000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        System.exit(0);

        return true;
    }

    @Override
    public String getDesc()
    {
        return "I am kill. No.";
    }
    
    @Override
    public boolean handleConsoleCommand(String... args)
    {
        killBot(Main.bot);
        return true;
    }
}
