package tterrag.tppibot.commands;

import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

import tterrag.tppibot.Main;
import tterrag.tppibot.util.IRCUtils;

public class Forgive extends Command
{
    private enum Type
    {
        strikes, timeouts
    }

    public Forgive()
    {
        super("forgive", PermLevel.TRUSTED);
    }

    @Override
    public boolean onCommand(MessageEvent<?> event, String... args)
    {
        if (args.length < 3)
        {
            sendNotice(event.getUser(), "This command requires three args: [nick], [type], and [amount].");
            return false;
        }

        User user = IRCUtils.getUserByNick(event.getChannel(), args[0]);

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
            sendNotice(event.getUser(), "\"" + args[2] + "\" is not a valid number!");
            return false;
        }

        switch (type)
        {
        case strikes:
            return removeStrikes(event, user, amnt);
        case timeouts:
            return removeTimeouts(event, user, amnt);
        default:
            return false;
        }
    }

    private boolean removeStrikes(MessageEvent<?> event, User user, int amnt)
    {
        if (user != null)
        {
            sendNotice(event.getUser(), "Set the amount of strikes on " + user.getNick() + " to " + Main.spamFilter.removeStrikes(user, amnt));
            return true;
        }
        else
        {
            sendNotice(event.getUser(), "No such user in this channel!");
            return false;
        }
    }

    private boolean removeTimeouts(MessageEvent<?> event, User user, int amnt)
    {
        if (user != null)
        {
            sendNotice(event.getUser(), "Set the amount of past offenses on " + user.getNick() + " to " + Main.timeouts.removePastOffenses(user, amnt));
            return true;
        }
        else
        {
            sendNotice(event.getUser(), "No such user in this channel!");
            return false;
        }
    }

    @Override
    public String getDesc()
    {
        return "Forgives a user the specified number of offenses. Can use strikes or timeouts as a switch before the number (default is strikes).";
    }
}
