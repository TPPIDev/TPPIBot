package tterrag.tppibot.commands;

import org.pircbotx.Channel;
import org.pircbotx.User;

import tterrag.tppibot.Main;

public class Kill extends Command
{
    public Kill()
    {
        super("kill", PermLevel.OP);
    }

    @Override
    public boolean onCommand(Channel channel, User user, String... args)
    {
        sendMessage(channel, "NOOOOooooo...");
        
        try
        {
            Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        
        Main.getBot().sendIRC().quitServer("x.x");
        System.exit(0);
        
        return true;
    }
}
