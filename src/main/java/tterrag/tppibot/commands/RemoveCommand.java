package tterrag.tppibot.commands;

import org.pircbotx.hooks.events.MessageEvent;

import tterrag.tppibot.registry.CommandRegistry;

public class RemoveCommand extends Command
{
    public RemoveCommand()
    {
        super("delcmd", PermLevel.OP);
    }

    @Override
    public boolean onCommand(MessageEvent<?> event, String... args)
    {
        if (args.length < 1)
        {
            sendNotice(event.getUser(), "No command specified.");
            return false;
        }

        for (String s : args)
        {
            CommandRegistry.unregisterCommand(s);
            sendNotice(event.getUser(), "Successfully removed command: \"" + s + "\"");
        }
        return true;
    }

    @Override
    public String getDesc()
    {
        return "Deletes a command from the registry. Default command removals will not persist between loads.";
    }
}
