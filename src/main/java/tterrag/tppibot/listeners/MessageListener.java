package tterrag.tppibot.listeners;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

import tterrag.tppibot.Main;
import tterrag.tppibot.commands.Command;
import tterrag.tppibot.interfaces.IReaction;
import tterrag.tppibot.util.IRCUtils;

public class MessageListener extends ListenerAdapter<PircBotX>
{
    public static final String controlChar = "`";

    @Override
    public void onMessage(MessageEvent<PircBotX> event) throws Exception
    {
        String message = event.getMessage();
        User sender = event.getUser();
        Channel channel = event.getChannel();
        List<Command> commands = Main.getCommandRegistry().getCommands();

        if (message.startsWith(controlChar))
        {
            message = pruneMessage(message);
            String[] args = message.split(" ");

            if (args.length < 1)
                return;

            for (int i = 0; i < commands.size(); i++)
            {
                Command c = commands.get(i);
                if (c.getName().equalsIgnoreCase(args[0]))
                {
                    if (IRCUtils.userMatchesPerms(channel, sender, c.getPermLevel()))
                    {
                        c.onCommand(event, ArrayUtils.remove(args, 0));
                    }
                    else
                    {
                        this.sendNotice(event, "You have no permission, you must be at least: " + c.getPermLevel().toString());
                    }
                }
            }
        }
        
        for (IReaction r : Main.getReactionRegistry().getReactions())
        {
            r.onMessage(event);
        }
    }

    private void sendNotice(org.pircbotx.hooks.events.MessageEvent<PircBotX> event, String message)
    {
        event.getUser().send().notice(message);
    }

    private String pruneMessage(String message)
    {
        return message.substring(controlChar.length());
    }

}
