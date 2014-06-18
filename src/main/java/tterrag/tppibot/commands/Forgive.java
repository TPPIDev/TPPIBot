package tterrag.tppibot.commands;

import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

import tterrag.tppibot.Main;
import tterrag.tppibot.util.IRCUtils;

public class Forgive extends Command
{
    public Forgive()
    {
        super("forgive", PermLevel.TRUSTED);
    }
    
    @Override
    public boolean onCommand(MessageEvent<?> event, String... args)
    {
        if (args.length < 2)
        {
            sendNotice(event.getUser(), "This command requires two args, [nick] and [amount].");
            return false;
        }
        
        User user = IRCUtils.getUserByNick(event.getChannel(), args[0]);
        int amnt = 0;
        
        try
        {
            amnt = Integer.parseInt(args[1]);
        }
        catch (NumberFormatException e)
        {
            sendNotice(event.getUser(), "\"" + args[1] + "\" is not a valid number!");
            return false;
        }
        
        if (user != null)
        {
            sendNotice(event.getUser(), "Set the amount of strikes on " + args[0] + " to " + Main.spamFilter.removeStrikes(user, amnt));
            return true;
        }
        else
        {
            sendNotice(event.getUser(), "No such user " + args[0] + " in this channel!");
            return false;
        }
    }
}
