package tterrag.tppibot.commands;

import java.util.List;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

import tterrag.tppibot.interfaces.ICommand;
import tterrag.tppibot.registry.CommandRegistry;
import tterrag.tppibot.registry.PermRegistry;
import tterrag.tppibot.util.IRCUtils;

public class Commands extends Command
{
    public Commands()
    {
        super("commands", PermLevel.DEFAULT);
    }

    @Override
    public void onCommand(PircBotX bot, User user, Channel channel, List<String> lines, String... args)
    {
        String s = "";
        boolean custom = false;

        if (args.length >= 1 && "custom".equals(args[0]))
        {
            custom = true;
        }

        PermLevel perms = getPerm(channel, user);
        for (ICommand c : CommandRegistry.getCommands())
        {
            if (IRCUtils.isPermLevelAboveOrEqualTo(perms, c.getPermLevel()))
            {
                if (custom == (c instanceof CustomCommand))
                {
                    if (!(c instanceof CustomCommand) || ((CustomCommand) c).isFor(channel))
                    {
                        s += c.getIdent() + ", ";
                    }
                }
            }
        }

        s = s.length() <= 2 ? "None" : s.substring(0, s.length() - 2);

        if (custom)
        {
            lines.add("Custom Commands: " + s + ".");
        }
        else
        {
            lines.add("Commands: " + s + ".");
            lines.add("To show custom commands for this channel, try \"~commands custom\".");
        }
    }

    @Override
    public String getDesc()
    {
        return "Shows all possible commands for you, perm level sensitive.";
    }

    private PermLevel getPerm(Channel chan, User user)
    {
        if (chan == null)
        {
            return PermRegistry.instance().isController(user) ? PermLevel.CONTROLLER : PermLevel.DEFAULT;
        }
        else
        {
            return PermRegistry.instance().getPermLevelForUser(chan, user);
        }
    }
}
