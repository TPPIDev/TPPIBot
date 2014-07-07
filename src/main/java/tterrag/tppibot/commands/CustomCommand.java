package tterrag.tppibot.commands;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

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
    public void onCommand(PircBotX bot, User user, List<String> lines, String... args)
    {
        lines.add(IRCUtils.getMessageForUser(user, message, args));
    }

    @Override
    public Command editCommand(PircBotX bot, User user, Channel channel, List<String> lines, String... args)
    {
        AddCommand.commandsAdded.remove(this);
        if (args.length > 0 && args[0].startsWith("-permLevel="))
        {
            PermLevel userLevel = PermRegistry.instance().getPermLevelForUser(channel, user);
            String perm = args[0].substring(11);
            PermLevel level = PermLevel.INVALID;
            try
            {
                level = PermLevel.valueOf(perm.toUpperCase());
            }
            catch (Exception e)
            {
                lines.add("Invalid perm level \"" + perm + ".\" Valid perm levels: " + Arrays.deepToString(PermLevel.getSettablePermLevels()));
            }

            if (level != PermLevel.INVALID && IRCUtils.isPermLevelAboveOrEqualTo(userLevel, level))
            {
                this.setPermLevel(level);
            }
            else
            {
                lines.add("You do not have the required perm level to do this. You must be at least: " + level.toString() + ".");
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
