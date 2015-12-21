package tterrag.tppibot.commands;

import java.util.List;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

import com.google.common.base.Joiner;

import tterrag.tppibot.Main;
import tterrag.tppibot.registry.PermRegistry;
import tterrag.tppibot.runnables.MessageSender;
import tterrag.tppibot.util.IRCUtils;

public class BanWord extends Command {

    public BanWord() {
        super("banned", PermLevel.TRUSTED);
    }

    @Override
    public void onCommand(PircBotX bot, User user, Channel channel, List<String> lines, String... args) {
        PermLevel level = PermRegistry.INSTANCE.getPermLevelForUser(channel, user);

        if (args.length < 2) {
            if (args.length > 0) {
                if (!args[0].equals("list")) {
                    lines.add("This command requires 2 args, [action] and [word]");
                    return;
                } else {
                    String list = Joiner.on(", ").join(Main.bannedWords.getBannedWords());
                    MessageSender.INSTANCE.enqueueNotice(bot, user.getNick(), list);
                    return;
                }
            } else {
                lines.add("This command requires 2 args, [action] and [word]");
                return;
            }
        }

        if (IRCUtils.isPermLevelAboveOrEqualTo(level, PermLevel.CONTROLLER)) {
            if ("add".equals(args[0])) {
                Main.bannedWords.addWord(args[1]);
                lines.add("Successfully banned word.");
            } else if ("remove".equals(args[0])) {
                Main.bannedWords.removeWord(args[1]);
                lines.add("Successfully removed word.");
            } else {
                lines.add(args[0] + " is not a valid action.");
            }
        } else {
            MessageSender.INSTANCE.enqueueNotice(bot, user.getNick(), "You are not a " + PermLevel.CONTROLLER.toString());
        }
    }
}
