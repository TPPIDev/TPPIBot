package tterrag.tppibot.commands;

import java.util.Arrays;
import java.util.List;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

import tterrag.tppibot.registry.PermRegistry;
import tterrag.tppibot.util.IRCUtils;

public class Perms extends Command
{
    public Perms()
    {
        super("perms", PermLevel.OP);
    }
    
    @Override
    public void onCommand(PircBotX bot, User user, Channel channel, List<String> lines, String... args)
    {
        if (args.length < 1)
        {
            lines.add("This command requires at least 1 args, either [nick] or [nick] [perm level (controller, op, or trusted)]");
            return;
        }
        
        String nick = args[0];
        User toChange = IRCUtils.getUserByNick(channel, nick);
        
        if (toChange == null)
        {
            lines.add("\"" + nick + "\" is not a valid user in this channel!");
            return;
        }

        if (args.length == 1)
        {
            lines.add("Perm level for " + nick + ": " + PermRegistry.instance().getPermLevelForUser(channel, toChange));
            return;
        }

        PermLevel level = null;
        for (PermLevel p : PermLevel.getSettablePermLevels())
        {
            if (p.toString().equalsIgnoreCase(args[1]))
                level = p;
        }
        
        if (level == null)
        {
            lines.add("\"" + args[1] + "\" is not a valid perm level. Valid levels include: " + Arrays.deepToString(PermLevel.getSettablePermLevels()));
            return;
        }
        
        if (level == PermLevel.CONTROLLER && PermRegistry.instance().getPermLevelForUser(channel, user) != PermLevel.CONTROLLER)
        {
            lines.add("You must be a controller to give someone controller permissions!");
            return;
        }
        
        if (PermRegistry.instance().registerUser(channel, toChange, level))
        {
        	lines.add("Successfully set " + nick + " to the " + level.toString() + " level.");
        	IRCUtils.modeSensitiveEnqueue(bot, toChange, channel, toChange.getNick() + ", " + (level == PermLevel.CONTROLLER ? "you are now a controller for TPPIBot!" : "you are now of the level " + level.toString() + " in channel " + channel.getName() + "!"));
        }
        else
        {
        	lines.add(args[0] + " is not logged in!");
        }
    }
    
    @Override
    public String getDesc()
    {
        return "Edits the permissions level of the specified user, valid entries are: " + Arrays.deepToString(PermLevel.getSettablePermLevels()) + "  (not case sensitive)";
    }

    @Override
    public boolean executeWithoutChannel()
    {
        return false;
    }
}
