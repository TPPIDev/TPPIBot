package tterrag.tppibot.commands;

import java.util.Arrays;

import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

import tterrag.tppibot.registry.PermRegistry;
import tterrag.tppibot.util.IRCUtils;

public class EditPerms extends Command
{
    public EditPerms()
    {
        super("perms", PermLevel.OP);
    }
    
    @Override
    public boolean onCommand(MessageEvent<?> event, String... args)
    {
        if (args.length < 2)
        {
            sendNotice(event.getUser(), "This command requires at least 2 args, [user nick] and [perm level (controller, op, or trusted)]");
            return false;
        }
        
        String nick = args[0];
        User user = IRCUtils.getUserByNick(event.getChannel(), nick);
        
        if (user == null)
        {
            sendNotice(event.getUser(), "\"" + nick + "\" is not a valid user in this channel!");
            return false;
        }
        
        PermLevel level = null;
        for (PermLevel p : PermLevel.getSettablePermLevels())
        {
            if (p.toString().equalsIgnoreCase(args[1]))
                level = p;
        }
        
        if (level == null)
        {
            sendNotice(event.getUser(), "\"" + args[1] + "\" is not a valid perm level. Valid levels include: " + Arrays.deepToString(PermLevel.getSettablePermLevels()));
            return false;
        }
        
        if (level == PermLevel.CONTROLLER && PermRegistry.instance().getPermLevelForUser(event.getChannel(), event.getUser()) != PermLevel.CONTROLLER)
        {
            sendNotice(event.getUser(), "You must be a controller to give someone controller permissions!");
            return false;
        }
        
        PermRegistry.instance().registerUser(event.getChannel(), user, level);
        sendNotice(event.getUser(), "Successfully set " + nick + " to the " + level.toString() + " level.");
        sendNotice(user, level == PermLevel.CONTROLLER ? "You are now a controller for TPPIBot!" : "You are now of the level " + level.toString() + " in channel " + event.getChannel().getName() + "!");
        
        return true;
    }
    
    @Override
    public String getDesc()
    {
        return "Edits the permissions level of the specified user, valid entries are: " + Arrays.deepToString(PermLevel.getSettablePermLevels()) + "  (not case sensitive)";
    }
}
