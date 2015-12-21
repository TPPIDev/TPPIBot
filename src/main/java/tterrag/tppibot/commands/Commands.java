package tterrag.tppibot.commands;

import java.util.ArrayList;
import java.util.List;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.events.DisconnectEvent;

import tterrag.tppibot.commands.Mode.BotMode;
import tterrag.tppibot.config.Config;
import tterrag.tppibot.interfaces.ICommand;
import tterrag.tppibot.registry.CommandRegistry;
import tterrag.tppibot.registry.PermRegistry;
import tterrag.tppibot.runnables.MessageSender;
import tterrag.tppibot.util.IRCUtils;

import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;

public class Commands extends Command {

    private Config config;

    public Commands() {
        super("commands", PermLevel.DEFAULT);

        config = new Config("cmdsLength.txt");

        try {
            length = Integer.parseInt(config.getText());
        } catch (NumberFormatException e) {
            config.writeInt(length);
        }
    }

    private int length = 250;

    @Override
    public void onCommand(PircBotX bot, User user, Channel channel, List<String> lines, String... args) {
        String s = "";
        boolean custom = false;
        List<String> listLines = new ArrayList<>();

        if (args.length >= 1) {
            if ("custom".equals(args[0])) {
                custom = true;
            } else if ("length".equals(args[0]) && PermRegistry.INSTANCE.isController(user)) {
                if (args.length > 1) {
                    length = Integer.parseInt(args[1]);
                    MessageSender.INSTANCE.enqueueNotice(bot, user.getNick(), "Length set.");
                    return;
                }
            }
        }

        boolean isTooLong = false;

        PermLevel perms = getPerm(channel, user);
        for (ICommand c : CommandRegistry.INSTANCE.getCommands()) {
            if (IRCUtils.isPermLevelAboveOrEqualTo(perms, c.getPermLevel())) {
                if (custom == (c instanceof CustomCommand)) {
                    if (!(c instanceof CustomCommand) || ((CustomCommand) c).isFor(channel)) {
                        s += c.getIdent() + ", ";
                        if (s.length() > length) {
                            listLines.add(s);
                            s = "";
                            isTooLong = true;
                        }
                    }
                }
            }
        }

        if (listLines.size() == 0 && s.isEmpty()) {
            listLines.add("None");
        }

        if (!s.isEmpty()) {
            listLines.add(s);
        }

        String last = listLines.remove(listLines.size() - 1);
        listLines.add(last.substring(0, last.length() - 2));

        String[] cmds = listLines.toArray(new String[] {});
        BotMode mode = Mode.getMode(channel.getName());
        if (isTooLong && mode != BotMode.PM) {
            lines.add("List too long, replying privately...");
            if (mode != BotMode.NOTICE) {
                mode = BotMode.PM;
            }
        }

        if (custom) {
            addLines(cmds, "Custom Commands: " + cmds[0], bot, user, channel, mode);
        } else {
            addLines(cmds, "Commands: " + cmds[0], bot, user, channel, mode);
            lines.add("To show custom commands for this channel, try \"~commands custom\".");
        }
    }

    private void addLines(String[] cmds, String firstLine, PircBotX bot, User user, Channel channel, BotMode mode) {
        List<String> lines = Lists.newArrayList(firstLine);
        for (int i = 1; i < cmds.length; i++) {
            String s = cmds[i];
            if (i == cmds.length - 1) {
                s += ".";
            }
            lines.add(s);
        }

        if (cmds.length == 1) {
            lines.set(0, lines.get(0) + ".");
        }

        for (String s : lines) {
            IRCUtils.modeSensitiveEnqueue(bot, user, channel, s, mode);
        }
    }

    @Override
    public String getDesc() {
        return "Shows all possible commands for you, perm level sensitive.";
    }

    private PermLevel getPerm(Channel chan, User user) {
        if (chan == null) {
            return PermRegistry.INSTANCE.isController(user) ? PermLevel.CONTROLLER : PermLevel.DEFAULT;
        } else {
            return PermRegistry.INSTANCE.getPermLevelForUser(chan, user);
        }
    }

    @Override
    public boolean shouldReceiveEvents() {
        return true;
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent<PircBotX> event) {
        config.writeInt(length);
    }
}
