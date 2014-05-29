package tterrag.tppibot.commands;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

import tterrag.tppibot.listeners.MessageListener;
import tterrag.tppibot.registry.CommandRegistry;
import tterrag.tppibot.util.IRCUtils;

public class Help extends Command
{
    private String helpText = "%user%, try " + MessageListener.controlChar + "help <command name>";

    public Help()
    {
        super("help", PermLevel.ANY);
    }

    @Override
    public boolean onCommand(MessageEvent<?> event, String... args)
    {
        User sendTo = event.getUser();

        if (args.length < 1)
        {
            IRCUtils.sendNoticeForUser(event.getChannel(), event.getUser(), helpText, args);
        }
        else
        {
            if (IRCUtils.getUserByNick(event.getChannel(), args[0]) != null)
            {
                IRCUtils.sendNoticeForUser(event.getChannel(), event.getUser(), args.length > 1 ? "%user% - Info on commands:" : helpText, args);
                sendTo = IRCUtils.getUserByNick(event.getChannel(), args[0]);
                args = ArrayUtils.remove(args, 0);
            }
            else
            {
                IRCUtils.sendNoticeForUser(event.getChannel(), event.getUser(), "%user% - Info on commands:", new String[] {});
            }

            for (String s : args)
            {
                if (CommandRegistry.isCommandRegistered(s))
                {
                    sendNotice(sendTo, String.format("Info on %s: %s", s, CommandRegistry.getCommand(s).getDesc()));
                }
            }
        }
        return true;
    }

    @Override
    public Command editCommand(String... params)
    {
        if (params.length < 1)
            return this;

        String newText = StringUtils.join(params, ' ');

        this.helpText = newText;
        return this;
    }

    @Override
    public String getDesc()
    {
        return "Don't Panic.";
    }
}
