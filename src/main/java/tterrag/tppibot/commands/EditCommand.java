package tterrag.tppibot.commands;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

import tterrag.tppibot.interfaces.ICommand;
import tterrag.tppibot.registry.CommandRegistry;
import tterrag.tppibot.util.IRCUtils;

public class EditCommand extends Command
{
    public EditCommand()
    {
        super("editcmd", PermLevel.TRUSTED);
    }

    @Override
    public void onCommand(PircBotX bot, User user, Channel channel, List<String> lines, String... args)
    {
        if (args.length < 2)
        {
            lines.add("This requires 2 args: [command] and [edit args]");
            return;
        }

        for (ICommand c : CommandRegistry.INSTANCE.getCommands())
        {
            String cmdName = args[0];

            if (c.getIdent().equalsIgnoreCase(cmdName))
            {
                args = ArrayUtils.remove(args, 0);

                if (!IRCUtils.userMatchesPerms(channel, user, c.getPermLevel()))
                {
                    lines.add("You do not have high enough permissions to edit command \"" + c.getIdent() + ".\" You must be at least: " + c.getPermLevel());
                    return;
                }
                
                lines.add("Editing command " + c.getIdent() + "...");
                c.editCommand(bot, user, channel, lines, args);
            }
        }
    }

    @Override
    public String getDesc()
    {
        return "Edits the specified command with the parameters passed. Each command has its own way of handling these parameters.";
    }
    
    @Override
    public boolean executeWithoutChannel()
    {
        return false;
    }
}
