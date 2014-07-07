package tterrag.tppibot.listeners;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

import tterrag.tppibot.commands.Mode;
import tterrag.tppibot.interfaces.IChannelCommand;
import tterrag.tppibot.interfaces.ICommand;
import tterrag.tppibot.interfaces.ICommand.PermLevel;
import tterrag.tppibot.interfaces.IReaction;
import tterrag.tppibot.registry.CommandRegistry;
import tterrag.tppibot.registry.PermRegistry;
import tterrag.tppibot.registry.ReactionRegistry;
import tterrag.tppibot.runnables.MessageSender;
import tterrag.tppibot.util.IRCUtils;

public class MessageListener extends ListenerAdapter<PircBotX>
{
    public static final String controlChar = "~";

    public static final MessageListener instance = new MessageListener();

    public int delayTime = 5000;
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

            if (IRCUtils.isPermLevelAboveOrEqualTo(perm, PermLevel.TRUSTED) || lastFire + delayTime < System.currentTimeMillis())
            {
                lastFire = System.currentTimeMillis();
                message = pruneMessage(message);
                String[] args = message.split(" ");

                if (args.length < 1)
                    return;

                for (int i = 0; i < commands.size(); i++)
                {
                    ICommand c = commands.get(i);
                    if (c.getIdent().equalsIgnoreCase(args[0]) && (IRCUtils.userIsOp(event.getChannel(), event.getBot().getUserBot()) || !c.needsOp()))
                    {
                        List<String> toSend = new ArrayList<String>();
                        if (IRCUtils.userMatchesPerms(channel, sender, c.getPermLevel()))
                        {
                            if (c instanceof IChannelCommand)
                            {
                                ((IChannelCommand) c).onCommand(event.getBot(), event.getUser(), event.getChannel(), toSend, ArrayUtils.remove(args, 0));
                            }
                            else
                            {
                                c.onCommand(event.getBot(), event.getUser(), toSend, ArrayUtils.remove(args, 0));
                            }
                        }
                        else
                        {
                            this.sendNotice(event, "You have no permission, you must be at least: " + c.getPermLevel().toString());
                        }

                        for (String s : toSend)
                        {
                            switch (Mode.getMode(event.getChannel().getName()))
                            {
                            case MESSAGE:
                                MessageSender.INSTANCE.enqueue(event.getBot(), event.getChannel().getName(), s);
                                break;
                            case NOTICE:
                                MessageSender.INSTANCE.enqueueNotice(event.getBot(), event.getUser().getNick(), s);
                                break;
                            case PM:
                                MessageSender.INSTANCE.enqueue(event.getBot(), event.getUser().getNick(), s);
                                break;
                            default:
                                break;
                            }
                        }
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

    private void sendNotice(org.pircbotx.hooks.events.MessageEvent<PircBotX> event, String message)
    {
        event.getUser().send().notice(message);
    }

    private String pruneMessage(String message)
    {
        return message.substring(controlChar.length());
    }

}
