package tterrag.tppibot.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

import tterrag.tppibot.interfaces.ICommand;
import tterrag.tppibot.interfaces.ICommand.PermLevel;
import tterrag.tppibot.registry.CommandRegistry;
import tterrag.tppibot.registry.PermRegistry;
import tterrag.tppibot.registry.ReactionRegistry;
import tterrag.tppibot.runnables.MessageSender;
import tterrag.tppibot.util.IRCUtils;

public class MessageListener extends ListenerAdapter<PircBotX> {

    public static final String controlChar = "~";

    public static final MessageListener instance = new MessageListener();

    public int delayTime = 10000;

    private Map<String, Long> delayMap = new HashMap<String, Long>();

    @Override
    public void onMessage(MessageEvent<PircBotX> event) throws Exception {
        String message = event.getMessage();
        User sender = event.getUser();
        Channel channel = event.getChannel();
        List<ICommand> commands = CommandRegistry.INSTANCE.getCommands();

        Long lastFire = delayMap.get(event.getChannel().getName());
        if (lastFire == null) {
            lastFire = 0L;
        }

        ReactionRegistry.INSTANCE.getReactions().forEach(r -> r.onMessage(event));
        String[] args = IRCUtils.trim(message.split(" "));

        if (args.length < 1) {
            return;
        }

        // Eira Bot
        if (args[0].startsWith("<") && args[0].endsWith(">")) {
            args = ArrayUtils.remove(args, 0);
        }

        if (args.length > 0 && args[0].startsWith(controlChar)) {
            PermLevel perm = PermRegistry.INSTANCE.getPermLevelForUser(event.getChannel(), event.getUser());

            if (perm == PermLevel.NONE && !PermRegistry.INSTANCE.isDefaultController(sender)) {
                MessageSender.INSTANCE.enqueueNotice(event.getBot(), sender.getNick(), "You may not execute commands in " + channel.getName());
                return;
            }

            if (lastFire + delayTime < System.currentTimeMillis() || IRCUtils.isPermLevelAboveOrEqualTo(perm, PermLevel.TRUSTED)) {
                args[0] = pruneCommand(args[0]);
                for (int i = 0; i < commands.size(); i++) {
                    ICommand c = commands.get(i);
                    if (c.getIdent().equalsIgnoreCase(args[0]) && (IRCUtils.userIsOp(event.getChannel(), event.getBot().getUserBot()) || !c.needsOp())) {
                        delayMap.put(channel.getName(), System.currentTimeMillis());
                        List<String> toSend = new ArrayList<String>();
                        if (IRCUtils.userMatchesPerms(channel, sender, c.getPermLevel())) {
                            c.onCommand(event.getBot(), event.getUser(), event.getChannel(), toSend, ArrayUtils.remove(args, 0));
                        } else {
                            MessageSender.INSTANCE.enqueueNotice(event.getBot(), event.getUser().getNick(), "You have no permission, you must be at least: " + c.getPermLevel().toString());
                        }

                        for (String s : toSend) {
                            IRCUtils.modeSensitiveEnqueue(event.getBot(), event.getUser(), event.getChannel(), s);
                        }
                    }

                    if (i < commands.size() && commands.get(i) != c) {
                        i--;
                    }
                }
            } else {
                event.getUser().send().notice("Slow down there, partner.");
            }
        }
    }

    private String pruneCommand(String message) {
        return message.substring(controlChar.length());
    }

}
