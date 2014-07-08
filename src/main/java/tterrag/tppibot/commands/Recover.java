package tterrag.tppibot.commands;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

import tterrag.tppibot.registry.PermRegistry;
import tterrag.tppibot.util.IRCUtils;

public class Recover extends Command
{
    public Recover()
    {
        super("recover", PermLevel.DEFAULT);
    }

    @Override
    public void onCommand(PircBotX bot, User user, Channel channel, List<String> lines, String... args)
    {
        if (ArrayUtils.contains(PermRegistry.defaultControllers, IRCUtils.getAccount(user)))
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
