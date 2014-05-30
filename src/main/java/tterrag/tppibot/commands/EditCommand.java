package tterrag.tppibot.commands;

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;
import org.pircbotx.hooks.events.MessageEvent;

import tterrag.tppibot.interfaces.ICommand;
import tterrag.tppibot.registry.CommandRegistry;
import tterrag.tppibot.util.IRCUtils;

public class EditCommand extends Command
{
    public EditCommand()
    {
        super("editcmd", PermLevel.DEFAULT);
    }

    @Override
    public boolean onCommand(MessageEvent<?> event, String... args)
    {
        if (args.length < 2)
        {
            sendNotice(event.getUser(), "This requires 2 args: [command] and [edit args]");
            return false;
        }

        for (ICommand c : CommandRegistry.getCommands())
        {
            String cmdName = args[0];

            if (c.getIdent().equalsIgnoreCase(cmdName))
            {
                args = ArrayUtils.remove(args, 0);

                if (!IRCUtils.userMatchesPerms(event.getChannel(), event.getUser(), c.getPermLevel()))
                {
                    sendNotice(event.getUser(), "You do not have high enough permissions to edit command \"" + c.getIdent() + ".\" You must be at least: " + c.getPermLevel());
                }
                
                sendNotice(event.getUser(), "Editing command " + c.getIdent() + " with args " + Arrays.deepToString(args));
                c.editCommand(args);
            }
        }

        return true;
    }

    @Override
    public String getDesc()
    {
        return "Edits the specified command with the parameters passed. Each command has its own way of handling these parameters.";
    }
}
