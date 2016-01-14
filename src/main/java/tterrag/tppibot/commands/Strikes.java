package tterrag.tppibot.commands;

import java.util.List;
import java.util.Optional;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

import tterrag.tppibot.Main;
import tterrag.tppibot.util.IRCUtils;

public class Strikes extends Command {

    public Strikes() {
        super("strikes");
    }

    @Override
    public void onCommand(PircBotX bot, User user, Channel channel, List<String> lines, String... args) {
        Optional<User> target = Optional.empty();
        if (args.length > 0) {
            target = IRCUtils.getUserByNick(channel, args[0]);
            if (!target.isPresent()) {
                lines.add(args[0] + " is not a valid nick in this channel!");
                return;
            }
        } else {
            lines.add("This command requires an argument!");
            return;
        }

        lines.add(target.get().getNick() + " has " + Main.spamFilter.getStrikes(target.get()) + " strikes.");
    }
}
