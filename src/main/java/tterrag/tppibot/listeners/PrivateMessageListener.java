package tterrag.tppibot.listeners;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.PrivateMessageEvent;

import tterrag.tppibot.interfaces.ICommand;
import tterrag.tppibot.interfaces.ICommand.PermLevel;
import tterrag.tppibot.registry.CommandRegistry;
import tterrag.tppibot.registry.PermRegistry;
import tterrag.tppibot.runnables.MessageSender;
import tterrag.tppibot.util.IRCUtils;

public class PrivateMessageListener extends ListenerAdapter<PircBotX>
{
    @Override
    public void onPrivateMessage(PrivateMessageEvent<PircBotX> event) throws Exception
    {
        String[] args = event.getMessage().split(" ");
        if (args.length <= 0)
        {
            return;
        }

        List<String> lines = new ArrayList<String>();

        for (ICommand c : CommandRegistry.getCommands())
        {
            if (c.getIdent().equals(args[0]))
            {
                if (c.executeWithoutChannel())
                {
                    if (c.getPermLevel().equals(PermLevel.DEFAULT) || PermRegistry.instance().isController(event.getUser()))
                    {
                    c.onCommand(event.getBot(), event.getUser(), null, lines, ArrayUtils.remove(args, 0));
                    }
                    else
                    {
                        lines.add("You may not execute " + c.getPermLevel().toString().toLowerCase() + " commands.");
                    }
                }
                else
                {
                    if (args.length >= 2)
                    {
                        Channel channel = IRCUtils.getChannelByName(event.getBot(), args[1]);
                        if (channel == null)
                        {
                            lines.add("Bot is not connected to channel " + args[1]);
                        }
                        else if (IRCUtils.isUserAboveOrEqualTo(channel, c.getPermLevel(), event.getUser()))
                        {
                            c.onCommand(event.getBot(), event.getUser(), channel, lines, ArrayUtils.remove(ArrayUtils.remove(args, 0), 0));
                        }
                        else
                        {
                            lines.add("You are not of the level " + c.getPermLevel() + " in channel " + args[1] + ".");
                        }
                    }
                    else
                    {
                        lines.add("This command must be sent to a specific channel, please specify this as the first arg.");
                    }
                }
            }
        }
        
        for (String s : lines)
        {
            MessageSender.INSTANCE.enqueue(event.getBot(), event.getUser().getNick(), s);
        }
    }
}
