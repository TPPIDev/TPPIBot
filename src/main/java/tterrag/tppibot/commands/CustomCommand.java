package tterrag.tppibot.commands;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.pircbotx.hooks.events.MessageEvent;

import tterrag.tppibot.registry.PermRegistry;
import tterrag.tppibot.util.IRCUtils;

public class CustomCommand extends Command
{
    private String message;

    public CustomCommand(String ident, PermLevel perms, String message)
    {
        super(ident, perms);
        this.message = message;
    }

    @Override
    public boolean onCommand(MessageEvent<?> event, String... args)
    {
        IRCUtils.sendMessageForUser(event.getChannel(), event.getUser(), message, args);
        return true;
    }

    @Override
    public Command editCommand(MessageEvent<?> event, String... args)
    {
        AddCommand.commandsAdded.remove(this);
        PermLevel userLevel = PermRegistry.instance().getPermLevelForUser(event.getChannel(), event.getUser());
        if (args.length > 0 && args[0].startsWith("-permLevel="))
        {
            String perm = args[0].substring(11);
            PermLevel level = PermLevel.INVALID;
            try
            {
                level = PermLevel.valueOf(perm.toUpperCase());
            }
            catch (Exception e)
            {
                sendNotice(event.getUser(), "Invalid perm level \"" + perm + ".\" Valid perm levels: " + Arrays.deepToString(PermLevel.getSettablePermLevels()));
            }

            if (level != PermLevel.INVALID && IRCUtils.isPermLevelAboveOrEqualTo(userLevel, level))
            {
                this.setPermLevel(level);
            }
            else
            {
                sendNotice(event.getUser(), "You do not have the required perm level to do this. You must be at least: " + level.toString() + ".");
            }
        }
        else
        {
            this.message = StringUtils.join(args, " ");
        }

        AddCommand.commandsAdded.add(this);
        return this;
    }

    @Override
    public String getDesc()
    {
        return "A custom command that was added by 'addcmd'. Output text is: \"" + this.message + ".\"";
    }
}
