package tterrag.tppibot.util;

import org.jibble.pircbot.User;

import tterrag.tppibot.Main;
import tterrag.tppibot.commands.Command.PermLevel;

public class IRCUtils
{
    public static boolean userIsOp(String channel, String user)
    {
        for (User u : Main.bot.getUsers(channel))
        {
            if (u.equals(user) && u.isOp())
                return true;
        }
        return false;
    }
    
    public static boolean userIsVoice(String channel, String user)
    {
        for (User u : Main.bot.getUsers(channel))
        {
            if (u.equals(user) && u.hasVoice())
                return true;
        }
        return false;
    }
    
    public static boolean userMatchesPerms(String channel, String user, PermLevel perm)
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
}
