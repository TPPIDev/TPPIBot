package tterrag.tppibot.commands;

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;
import org.pircbotx.hooks.events.MessageEvent;

import tterrag.tppibot.registry.CommandRegistry;

public class EditCommand extends Command
{
    public EditCommand()
    {
        super("editcmd", PermLevel.OP);
    }

    @Override
    public boolean onCommand(MessageEvent<?> event, String... args)
    {
        if (args.length < 2)
        {
            sendNotice(event.getUser(), "This requires 2 args: [command] and [edit args]");
            return false;
        }

        for (Command c : CommandRegistry.getCommands())
        {
            String cmdName = args[0];

            if (c.getName().equalsIgnoreCase(cmdName))
            {
                args = ArrayUtils.remove(args, 0);

                sendNotice(event.getUser(), "Editing command " + c.getName() + " with args " + Arrays.deepToString(args));
                c.editCommand(args);
            }
        }

        return true;
    }
}
