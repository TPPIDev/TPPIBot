package tterrag.tppibot.commands;

import java.util.List;
import java.util.Optional;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

import tterrag.tppibot.Main;
import tterrag.tppibot.util.IRCUtils;

public class Forgive extends Command {

    private enum Type {
        STRIKES,
        TIMEOUTS
    }

    public Forgive() {
        super("forgive", PermLevel.TRUSTED);
    }

    @Override
    public void onCommand(PircBotX bot, User user, Channel channel, List<String> lines, String... args) {
        if (args.length < 2) {
            lines.add("This command requires three args: [nick], [type], and [amount].");
            return;
        }

        Optional<User> toChange = IRCUtils.getUserByNick(channel, args[0]);
        
        if (!toChange.isPresent()) {
            lines.add(args[0] + " is not a valid user in this channel!");
            return;
        }

        boolean foundType = false;
        Type type = Type.STRIKES;
        for (Type t : Type.values()) {
            if (t.toString().equals(args[1])) {
                type = Type.valueOf(args[1]);
                foundType = true;
            }
        }

        int amnt = 0;
        try {
            amnt = Integer.parseInt(args[foundType ? 2 : 1]);
        } catch (NumberFormatException e) {
            lines.add("\"" + args[foundType ? 2 : 1] + "\" is not a valid number!");
            return;
        }

        switch (type) {
        case STRIKES:
            removeStrikes(toChange.get(), amnt, lines);
            break;
        case TIMEOUTS:
            removeTimeouts(toChange.get(), amnt, lines);
            break;
        }
    }

    private boolean removeStrikes(User user, int amnt, List<String> lines) {
        if (user != null) {
            lines.add("Set the amount of strikes on " + user.getNick() + " to " + Main.spamFilter.removeStrikes(user, amnt));
            return true;
        } else {
            lines.add("No such user in this channel!");
            return false;
        }
    }

    private boolean removeTimeouts(User user, int amnt, List<String> lines) {
        if (user != null) {
            lines.add("Set the amount of past offenses on " + user.getNick() + " to " + Main.timeouts.removePastOffenses(user, amnt));
            return true;
        } else {
            lines.add("No such user in this channel!");
            return false;
        }
    }

    @Override
    public String getDesc() {
        return "Forgives a user the specified number of offenses. Can use strikes or timeouts as a switch before the number (default is strikes).";
    }

    @Override
    public boolean executeWithoutChannel() {
        return false;
    }
}
