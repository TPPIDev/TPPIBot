package tterrag.tppibot.commands;

import org.pircbotx.hooks.events.MessageEvent;

public class Kill extends Command
{
    public Kill()
    {
        super("kill", PermLevel.OP);
    }

    @Override
    public boolean onCommand(MessageEvent<?> event, String... args)
    {
        sendMessage(event.getChannel(), "NOOOOooooo...");
        
        try
        {
            Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        event.getBot().stopBotReconnect();
        event.getBot().sendIRC().quitServer("x.x");
        
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
}
