package tterrag.tppibot.commands;

import java.util.List;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

import tterrag.tppibot.registry.PermRegistry;

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
            PermRegistry.instance().registerUser(null, user, PermLevel.CONTROLLER);
            user.send().notice("Welcome back, " + user.getNick() + ". You are once again controller.");
        }
        else
        {
            user.send().notice("Nice try...");
        }
    }
}
