package tterrag.tppibot.commands;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.pircbotx.hooks.events.MessageEvent;

import tterrag.tppibot.Main;

public class AddCommand extends Command
{
    public AddCommand()
    {
        super("addcmd", PermLevel.OP);
    }

    @Override
    public boolean onCommand(MessageEvent<?> event, String... args)
    {
        if (args.length < 2)
        {
            sendNotice(event.getUser(), "This requires at least two args, [command name] and [message]!");
            return false;
        }

        String cmdName = args[0];

        args = ArrayUtils.remove(args, 0);

        String toAdd = StringUtils.join(args, ' ');

        Main.getCommandRegistry().registerCommand(new CustomCommand(cmdName, PermLevel.ANY, toAdd));

        return true;
    }
}
