package tterrag.tppibot.commands;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

import tterrag.tppibot.interfaces.IChannelCommand;
import tterrag.tppibot.interfaces.ICommand;
import tterrag.tppibot.listeners.MessageListener;
import tterrag.tppibot.registry.CommandRegistry;
import tterrag.tppibot.registry.PermRegistry;
import tterrag.tppibot.util.IRCUtils;

public class Help extends Command implements IChannelCommand
{
    private String helpText = "%user%, try " + MessageListener.controlChar + "help <command name>";

    public Help()
    {
        super("help", PermLevel.DEFAULT);
    }

    @Override
    public void onCommand(PircBotX bot, User user, Channel channel, List<String> lines, String... args)
    {
        if (args.length < 1)
        {
            user.send().notice("Your current perm level is: " + PermRegistry.instance().getPermLevelForUser(channel, user) + ".");
        }

        onCommand(bot, user, lines, args);
    }

    @Override
    public void onCommand(PircBotX bot, User user, List<String> lines, String... args)
    {
        if (args.length < 1)
        {
            lines.add(IRCUtils.getMessageForUser(user, "To get help on specific commands " + helpText, args));
        }
        else
        {
            lines.add(IRCUtils.getMessageForUser(user, "%user% - Info on commands:", new String[] {}));

            for (String s : args)
            {
                if (CommandRegistry.isCommandRegistered(s))
                {
                    ICommand c = CommandRegistry.getCommand(s);
                    lines.add(String.format("Info on %s: %s %s: %s", s, c.getDesc(), "Required perm level", c.getPermLevel().toString()));
                }
            }
        }
    }

    @Override
    public ICommand editCommand(PircBotX bot, User user, Channel channel, List<String> lines, String... args)
    {
        if (args.length < 1)
            return this;

        String newText = StringUtils.join(args, ' ');

        this.helpText = newText;
        return this;
    }

    @Override
    public String getDesc()
    {
        return "Don't Panic.";
    }

    @Override
    public boolean canChannelBeNull()
    {
        return true;
    }
}
