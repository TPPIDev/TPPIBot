package tterrag.tppibot.util;

import static tterrag.tppibot.interfaces.ICommand.PermLevel.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.ArrayUtils;
import org.pircbotx.Channel;
import org.pircbotx.Colors;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.UserLevel;

import tterrag.tppibot.commands.Command;
import tterrag.tppibot.commands.Mode;
import tterrag.tppibot.commands.Mode.BotMode;
import tterrag.tppibot.interfaces.ICommand.PermLevel;
import tterrag.tppibot.registry.CommandRegistry;
import tterrag.tppibot.registry.PermRegistry;
import tterrag.tppibot.registry.WhoisCache;
import tterrag.tppibot.runnables.MessageSender;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

public class IRCUtils {

    public static boolean userIsOp(Channel channel, User user) {
        return user.getUserLevels(channel).contains(UserLevel.OP);
    }

    public static boolean userIsVoice(Channel channel, User user) {
        return user.getUserLevels(channel).contains(UserLevel.VOICE);
    }

    /**
     * Can a user perform a command or action with the passed perm level
     * 
     * @param channel
     *            - Channel the action was performed in
     * @param user
     *            - User to check
     * @param perm
     *            - {@link PermLevel} to check against
     */
    public static boolean userMatchesPerms(Channel channel, User user, PermLevel perm) {
        perm = Objects.firstNonNull(perm, DEFAULT);
        PermLevel userPerm = PermRegistry.INSTANCE.getPermLevelForUser(channel, user);
        return isPermLevelAboveOrEqualTo(userPerm, perm);
    }

    /**
     * Can a user perform a command or action with the passed perm level, use this version if you need to repeat the check multiple times
     * 
     * @param channel
     *            - Channel the action was performed in
     * @param user
     *            - User to check
     * @param userPerm
     *            - {@link PermLevel} of the user (not checked)
     * @param perm
     *            - {@link PermLevel} to check against
     */
    public static boolean userMatchesPerms(Channel channel, User user, PermLevel userPerm, PermLevel toCheck) {
        toCheck = Objects.firstNonNull(toCheck, DEFAULT);
        return isPermLevelAboveOrEqualTo(userPerm, toCheck);
    }

    /**
     * Determines if a user is at or above this level, can only be used for the <code>{@link PermLevel}s</code> returned by {@link PermLevel.getSettablePermLevels()}
     * 
     * @throws IllegalArgumentException
     *             If the {@link PermLevel} is not returned by the aforementioned method.
     */
    public static boolean isUserAboveOrEqualTo(Channel chan, PermLevel perm, User user) {
        Preconditions.checkArgument(ArrayUtils.contains(PermLevel.getSettablePermLevels(), perm), "The perm level " + perm.toString() + " is not valid.");
        return isPermLevelAboveOrEqualTo(PermRegistry.INSTANCE.getPermLevelForUser(chan, user), perm);
    }

    /**
     * Performs a simple ordinal check on the perm levels, but also checks for null and converts to {@link PermLevel.DEFAULT}
     * 
     * @return <code>true</code> if <code>userLevel</code> is above or equal to <code>toCheck</code>
     */
    public static boolean isPermLevelAboveOrEqualTo(PermLevel userLevel, PermLevel toCheck) {
        userLevel = Objects.firstNonNull(userLevel, DEFAULT);
        toCheck = Objects.firstNonNull(toCheck, DEFAULT);
        return userLevel.ordinal() >= toCheck.ordinal();
    }

    public static String getMessageWithArgs(User user, String message, String... args) {
        String ret = "";
        for (String arg : args)
            ret += arg + " ";

        return message.replace("%user%", args.length >= 1 ? ret.substring(0, ret.length() - 1) : user.getNick());
    }

    @Deprecated
    public static void sendNoticeForUser(Channel channel, User user, String message, String... args) {
        if (args.length > 0) {
            Optional<User> to = getUserByNick(channel, args[0]);
            if (to.isPresent()) {
                to.get().send().notice(message.replace("%user%", to.get().getNick()));
                return;
            }
        }
        user.send().notice(message.replace("%user%", user.getNick()));
    }

    public static Optional<User> getUserByNick(Channel channel, String nick) {
        return channel.getUsers().stream().filter(u -> u.getNick().equalsIgnoreCase(nick)).findFirst();
    }

    public static Optional<Channel> getChannelByName(PircBotX bot, String channel) {
        return bot.getUserBot().getChannels().stream().filter(c -> c.getName().equalsIgnoreCase(channel)).findFirst();
    }

    public static String getAccount(User u) {
        return WhoisCache.INSTANCE.getAccount(u);
    }

    public static String fmtChan(String chan) {
        return chan.startsWith("#") ? chan : "#" + chan;
    }

    public static void modeSensitiveEnqueue(PircBotX bot, User user, Channel channel, String message) {
        BotMode mode = Mode.getMode(channel.getName());
        modeSensitiveEnqueue(bot, user, channel, message, mode);
    }

    public static void modeSensitiveEnqueue(PircBotX bot, User user, Channel channel, String message, BotMode mode) {
        switch (mode) {
        case MESSAGE:
            MessageSender.INSTANCE.enqueue(bot, channel.getName(), message);
            break;
        case NOTICE:
            MessageSender.INSTANCE.enqueueNotice(bot, user.getNick(), message);
            break;
        case PM:
            MessageSender.INSTANCE.enqueue(bot, user.getNick(), message);
            break;
        }
    }

    public static void timeout(PircBotX bot, User user, Channel channel, String amount) {
        Command quiet = (Command) CommandRegistry.INSTANCE.getCommand("timeout");
        List<String> toQueue = new ArrayList<String>();
        quiet.onCommand(bot, user, channel, toQueue, user.getNick(), "" + amount);
        toQueue.forEach(s -> IRCUtils.modeSensitiveEnqueue(bot, user, channel, s));
    }

    public static int getSecondsFromString(String s) throws NumberFormatException {
        String modifier = "none";

        char c = s.charAt(s.length() - 1);
        modifier = c >= '0' && c <= '9' ? "none" : Character.toString(c).toLowerCase();
        if (!modifier.equals("none")) {
            s = s.substring(0, s.length() - 1);
        }

        int mult = getMultiplierForModifier(modifier);

        return Integer.parseInt(s) * mult;
    }

    public static int getMultiplierForModifier(String modifier) {
        switch (modifier) {
        case "s":
            return 1;
        case "m":
            return 60;
        case "h":
            return 3600;
        case "d":
            return 86400;
        case "w":
            return 604800;
        default:
            return 60;
        }
    }

    /**
     * Removes "invisible" arguments
     */
    public static String[] trim(String[] args) {
        return Arrays.stream(args).map(s -> Colors.removeFormattingAndColors(s).trim()).filter(s -> !s.isEmpty()).toArray(String[]::new);
    }
}
