package tterrag.tppibot.listeners;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

import tterrag.tppibot.interfaces.ICommand;
import tterrag.tppibot.interfaces.ICommand.PermLevel;
import tterrag.tppibot.interfaces.IReaction;
import tterrag.tppibot.registry.CommandRegistry;
import tterrag.tppibot.registry.PermRegistry;
import tterrag.tppibot.registry.ReactionRegistry;
import tterrag.tppibot.util.IRCUtils;

public class MessageListener extends ListenerAdapter<PircBotX>
{
    public static final String controlChar = "~";

    public static final MessageListener instance = new MessageListener();

    public int delayTime = 10000;
    private long lastFire = 0;

    @Override
    public void onMessage(MessageEvent<PircBotX> event) throws Exception
    {
        String message = event.getMessage();
        User sender = event.getUser();
        Channel channel = event.getChannel();
        List<ICommand> commands = CommandRegistry.getCommands();

        if (message.startsWith(controlChar))
        {
            PermLevel perm = PermRegistry.instance().getPermLevelForUser(event.getChannel(), event.getUser());

            if (lastFire + delayTime < System.currentTimeMillis() || IRCUtils.isPermLevelAboveOrEqualTo(perm, PermLevel.TRUSTED))
            {
                message = pruneMessage(message);
                String[] args = message.split(" ");

                if (args.length < 1)
                    return;

                for (int i = 0; i < commands.size(); i++)
                {
                    ICommand c = commands.get(i);
                    if (c.getIdent().equalsIgnoreCase(args[0]) && (IRCUtils.userIsOp(event.getChannel(), event.getBot().getUserBot()) || !c.needsOp()))
                    {
                        lastFire = System.currentTimeMillis();
                        List<String> toSend = new ArrayList<String>();
                        if (IRCUtils.userMatchesPerms(channel, sender, c.getPermLevel()))
                        {
                            c.onCommand(event.getBot(), event.getUser(), event.getChannel(), toSend, ArrayUtils.remove(args, 0));
                        }
                        else
                        {
                            toSend.add("You have no permission, you must be at least: " + c.getPermLevel().toString());
                        }

                        for (String s : toSend)
                        {
                            IRCUtils.modeSensitiveEnqueue(event.getBot(), event.getUser(), event.getChannel(), s);
                        }
                    }
                    
                    if (i < commands.size() && commands.get(i) != c)
                    {
                        i--;
                    }
                }
            }
            else
            {
                event.getUser().send().notice("Slow down there, partner.");
            }
        }

        for (IReaction r : ReactionRegistry.getReactions())
        {
            r.onMessage(event);
        }
    }

    private String pruneMessage(String message)
    {
        return message.substring(controlChar.length());
    }

}
