package tterrag.tppibot.commands;

import org.apache.commons.lang3.ArrayUtils;
import org.pircbotx.hooks.events.MessageEvent;

import tterrag.tppibot.registry.PermRegistry;
import tterrag.tppibot.util.IRCUtils;

public class Recover extends Command
{
    public Recover()
    {
        super("recover", PermLevel.DEFAULT);
    }

    @Override
    public boolean onCommand(MessageEvent<?> event, String... args)
    {
        if (ArrayUtils.contains(PermRegistry.defaultControllers, IRCUtils.getAccount(event.getUser())))
        {
            PermRegistry.instance().registerUser(event.getChannel(), event.getUser(), PermLevel.CONTROLLER);
            sendNotice(event.getUser(), "Welcome back, " + event.getUser().getNick() + ". You are once again controller.");
        }
        else
        {
            sendNotice(event.getUser(), "Nice try...");
        }
        return true;
    }
}
