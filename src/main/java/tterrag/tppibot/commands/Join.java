package tterrag.tppibot.commands;

import org.pircbotx.Channel;
import org.pircbotx.User;

import tterrag.tppibot.Main;

public class Join extends Command
{
    public Join()
    {
        super("join", PermLevel.OP);
    }

    @Override
    public boolean onCommand(Channel channel, User user, String... args)
    {
        if (args.length > 0)
        {
            for (String s : args)
            {
                Main.getBot().sendIRC().joinChannel(s);
            }
            return true;
        }
        else 
        {
            sendNotice(user, "Must supply at least one channel!");
            return false;
        }
    }
}
