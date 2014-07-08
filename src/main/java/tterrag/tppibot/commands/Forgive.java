package tterrag.tppibot.commands;

import java.util.List;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

import tterrag.tppibot.Main;
import tterrag.tppibot.util.IRCUtils;

public class Forgive extends Command
{
    private enum Type 
    {
        strikes,
        timeouts
    }
    
    public Forgive()
    {
        super("forgive", PermLevel.TRUSTED);
    }
    
    @Override
    public void onCommand(PircBotX bot, User user, Channel channel, List<String> lines, String... args)
    {
        if (args.length < 3)
        {
            lines.add("This command requires three args: [nick], [type], and [amount].");
            return;
        }
        
        User toChange = IRCUtils.getUserByNick(channel, args[0]);
        
        Type type = Type.strikes;
        for (Type t : Type.values())
        {
            if (t.toString().equals(args[1]))
            {
                type = Type.valueOf(args[1]);
            }
        }
            
        int amnt = 0;
        try
        {
            amnt = Integer.parseInt(args[2]);
        }
        catch (NumberFormatException e)
        {
            
            lines.add("\"" + args[1] + "\" is not a valid number!");
            return;
        }
        
        switch(type)
        {
        case strikes:
            removeStrikes(toChange, amnt, lines);
            break;
        case timeouts:
            removeTimeouts(toChange, amnt, lines);
            break;
        }
        
        if (toChange != null)
        {
            lines.add("Set the amount of strikes on " + args[0] + " to " + Main.spamFilter.removeStrikes(toChange, amnt));
        }
        else
        {
            lines.add("No such user " + args[0] + " in this channel!");
        }
    }
    
    private boolean removeStrikes(User user, int amnt, List<String> lines)
    {
        if (user != null)
        {
            lines.add("Set the amount of strikes on " + user.getNick() + " to " + Main.spamFilter.removeStrikes(user, amnt));
            return true;
        }
        else
        {
            lines.add("No such user in this channel!");
            return false;
        }
    }

    private boolean removeTimeouts(User user, int amnt, List<String> lines)
    {
        if (user != null)
        {
            lines.add("Set the amount of past offenses on " + user.getNick() + " to " + Main.timeouts.removePastOffenses(user, amnt));
            return true;
        }
        else
        {
            lines.add("No such user in this channel!");
            return false;
        }
    }
    
    @Override
    public String getDesc()
    {
        return "Forgives a user the specified number of offenses. Can use strikes or timeouts as a switch before the number (default is strikes).";
    }
    
    @Override
    public boolean executeWithoutChannel()
    {
        return false;
    }
}
