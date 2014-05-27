package tterrag.tppibot.util;

import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.UserLevel;

import tterrag.tppibot.Main;
import tterrag.tppibot.commands.Command.PermLevel;

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
    
    public static boolean userMatchesPerms(Channel channel, User user, PermLevel perm)
    {
        if (perm == PermLevel.ANY)
        {
            return true;
        }
        else if (perm == PermLevel.VOICE)
        {
            return userIsVoice(channel, user) || userIsOp(channel, user);
        }
        else if (perm == PermLevel.OP)
        {
            return userIsOp(channel, user);
        }
        else return false;
    }
    
    public static void sendMessageForUser(Channel channel, User user, String message, String... args)
    {
        Main.getBot().sendIRC().message(channel.getName(), message.replace("%user%", args.length >= 1 ? args[0] : user.getNick()));
    }
}
