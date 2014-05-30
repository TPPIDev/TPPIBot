package tterrag.tppibot.commands;

import org.pircbotx.hooks.events.MessageEvent;

import tterrag.tppibot.interfaces.ICommand;
import tterrag.tppibot.registry.CommandRegistry;
import tterrag.tppibot.util.IRCUtils;

public class Commands extends Command
{
    public Commands()
    {
        super("commands", PermLevel.DEFAULT);
    }

    @Override
    public boolean onCommand(MessageEvent<?> event, String... args)
    {
        String s = "";
        for (ICommand c : CommandRegistry.getCommands())
        {
            if (IRCUtils.userMatchesPerms(event.getChannel(), event.getUser(), c.getPermLevel()))
            {
                s += c.getIdent() + ", ";
            }
        }
        s = s.substring(0, s.length() - 2);
        sendNotice(event.getUser(), "Commands: " + s);
        return true;
    }

    @Override
    public String getDesc()
    {
        return "Shows all possible commands for you, perm level sensitive.";
    }
}
