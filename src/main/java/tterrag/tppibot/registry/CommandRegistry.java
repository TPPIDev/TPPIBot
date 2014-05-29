package tterrag.tppibot.registry;

import java.util.ArrayList;
import java.util.List;

import tterrag.tppibot.commands.AddCommand;
import tterrag.tppibot.interfaces.ICommand;

public class CommandRegistry
{
    private static ArrayList<ICommand> commands = new ArrayList<ICommand>();

    public static void registerCommand(ICommand c)
    {
        commands.add(c);
    }

    public static List<ICommand> getCommands()
    {
        return commands;
    }

    public static boolean isCommandRegistered(String s)
    {
        for (ICommand c : commands)
        {
            if (c.getIdent().equalsIgnoreCase(s))
                return true;
        }
        return false;
    }

    public static ICommand getCommand(String s)
    {
        for (ICommand c : commands)
        {
            if (c.getIdent().equalsIgnoreCase(s))
                return c;
        }
        return null;
    }

    public static void unregisterCommand(String s)
    {
        for (int i = 0; i < commands.size(); i++)
        {
            ICommand c = commands.get(i);
            if (c.getIdent().equalsIgnoreCase(s))
            {
                commands.remove(c);
                AddCommand.commandsAdded.remove(c);
            }
        }
    }
}
