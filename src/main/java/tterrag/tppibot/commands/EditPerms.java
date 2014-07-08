package tterrag.tppibot.commands;

import java.util.Arrays;
import java.util.List;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

import tterrag.tppibot.registry.PermRegistry;
import tterrag.tppibot.util.IRCUtils;

public class EditPerms extends Command
{
    public EditPerms()
    {
        super("perms", PermLevel.OP);
    }
    
    @Override
    public void onCommand(PircBotX bot, User user, Channel channel, List<String> lines, String... args)
    {
        if (args.length < 2)
        {
            lines.add("This command requires at least 2 args, [user nick] and [perm level (controller, op, or trusted)]");
            return;
        }
        
        String nick = args[0];
        User toChange = IRCUtils.getUserByNick(channel, nick);
        
        if (user == null)
        {
            lines.add("\"" + nick + "\" is not a valid user in this channel!");
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
        
        PermRegistry.instance().registerUser(channel, user, level);
        lines.add("Successfully set " + nick + " to the " + level.toString() + " level.");
        lines.add(toChange.getNick() + ", " + (level == PermLevel.CONTROLLER ? "you are now a controller for TPPIBot!" : "you are now of the level " + level.toString() + " in channel " + channel.getName() + "!"));
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
