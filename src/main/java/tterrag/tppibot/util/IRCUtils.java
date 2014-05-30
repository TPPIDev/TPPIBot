package tterrag.tppibot.util;

import org.apache.commons.lang3.ArrayUtils;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.UserLevel;
import org.pircbotx.hooks.WaitForQueue;
import org.pircbotx.hooks.events.WhoisEvent;

import tterrag.tppibot.Main;
import tterrag.tppibot.interfaces.ICommand.PermLevel;

import static tterrag.tppibot.interfaces.ICommand.PermLevel.*;

import tterrag.tppibot.registry.PermRegistry;

public class IRCUtils
{
    public static boolean userIsOp(Channel channel, User user)
    {
        return user.getUserLevels(channel).contains(UserLevel.OP);
    }

    public static boolean userIsVoice(Channel channel, User user)
    {
        return user.getUserLevels(channel).contains(UserLevel.VOICE);
    }

    /**
     * Can a user perform a command or action with the passed perm level
     * 
     * @param channel - Channel the action was performed in
     * @param user - User to check
     * @param perm - {@link PermLevel} to check against
     */
    public static boolean userMatchesPerms(Channel channel, User user, PermLevel perm)
    {
        if (perm == null)
            perm = PermLevel.DEFAULT;

        PermLevel userPerm = PermRegistry.instance().getPermLevelForUser(channel, user);

        return isUserAboveOrEqualTo(userPerm, perm);

    }

    /**
     * Can a user perform a command or action with the passed perm level, use this version if you need to repeat the check multiple times
     * 
     * @param channel - Channel the action was performed in
     * @param user - User to check
     * @param userPerm - {@link PermLevel} of the user (not checked)
     * @param perm - {@link PermLevel} to check against
     */
    public static boolean userMatchesPerms(Channel channel, User user, PermLevel userPerm, PermLevel toCheck)
    {
        toCheck = toCheck == null ? DEFAULT : toCheck;

        return isUserAboveOrEqualTo(userPerm, toCheck);
    }

    /**
     * Determines if a user is at or above this level, can only be used for the <code>{@link PermLevel}s</code> returned by {@link PermLevel.getSettablePermLevels()}
     * 
     * @throws IllegalArgumentException If the {@link PermLevel} is not returned by the aforementioned method.
     */
    public static boolean isUserAboveOrEqualTo(Channel chan, PermLevel perm, User user)
    {
        if (!ArrayUtils.contains(PermLevel.getSettablePermLevels(), perm)) { throw new IllegalArgumentException("The perm level " + perm.toString() + " is not valid."); }

        return isUserAboveOrEqualTo(PermRegistry.instance().getPermLevelForUser(chan, user), perm);
    }

    private static boolean isUserAboveOrEqualTo(PermLevel userLevel, PermLevel toCheck)
    {
        return userLevel.ordinal() >= toCheck.ordinal();
    }

    public static void sendMessageForUser(Channel channel, User user, String message, String... args)
    {
        channel.send().message(message.replace("%user%", args.length >= 1 ? args[0] : user.getNick()));
    }

    public static void sendNoticeForUser(Channel channel, User user, String message, String... args)
    {
        if (args.length > 0)
        {
            User to = getUserByNick(channel, args[0]);
            if (to != null)
            {
                to.send().notice(message.replace("%user%", to.getNick()));
                return;
            }
        }
        user.send().notice(message.replace("%user%", user.getNick()));
    }

    public static User getUserByNick(Channel channel, String nick)
    {
        for (User u : channel.getUsers())
        {
            if (u.getNick().equalsIgnoreCase(nick))
                return u;
        }
        return null;
    }

    public static Channel getChannelByName(PircBotX bot, String channel)
    {
        for (Channel c : bot.getUserBot().getChannels())
        {
            if (c.getName().equalsIgnoreCase(channel))
                return c;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static String getAccount(User u)
    {
        String user = "";
        Main.bot.sendRaw().rawLineNow("WHOIS " + u.getNick());
        WaitForQueue waitForQueue = new WaitForQueue(Main.bot);
        WhoisEvent<PircBotX> test = null;
        try
        {
            test = waitForQueue.waitFor(WhoisEvent.class);
            waitForQueue.close();
            user = test.getRegisteredAs();
        }
        catch (InterruptedException ex)
        {
            ex.printStackTrace();
            user = null;
        }

        return user;
    }
}
