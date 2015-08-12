package tterrag.tppibot.commands;

import java.util.List;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

import tterrag.tppibot.registry.PermRegistry;
import tterrag.tppibot.runnables.MessageSender;

public class Recover extends Command
{
    public Recover()
    {
        super("recover", PermLevel.NONE);
    }

    @Override
    public void onCommand(PircBotX bot, User user, Channel channel, List<String> lines, String... args)
    {
        if (PermRegistry.isDefaultController(user))
        {
            if (PermRegistry.instance().registerUser(null, user, PermLevel.CONTROLLER)) 
            {
            	MessageSender.instance.enqueueNotice(bot, user.getNick(), "Welcome back, " + user.getNick() + ". You are once again controller.");
            }
            else
            {
            	lines.add(args[0] + " is not logged in!");
            }
        }
        else
        {
            MessageSender.instance.enqueueNotice(bot, user.getNick(), "Nice try...");
        }
    }
}
