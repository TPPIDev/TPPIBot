package tterrag.tppibot.registry;

import java.util.ArrayList;
import java.util.List;

import tterrag.tppibot.commands.AddCommand;
import tterrag.tppibot.commands.Command;

public class CommandRegistry
{
    private static ArrayList<Command> commands = new ArrayList<Command>();

    public static void registerCommand(Command c)
    {
        commands.add(c);
    }

    public static List<Command> getCommands()
    {
        return commands;
    }

    public static boolean isCommandRegistered(String s)
    {
        for (Command c : commands)
        {
            if (c.getName().equalsIgnoreCase(s))
                return true;
        }
        return false;
    }
    
    public static Command getCommand(String s)
    {
        for (Command c : commands)
        {
            if (c.getName().equalsIgnoreCase(s))
                return c;
        }
        return null;
    }

    public static void unregisterCommand(String s)
    {
        for (int i = 0; i < commands.size(); i++)
        {
            Command c = commands.get(i);
            if (c.getName().equalsIgnoreCase(s))
            {
                commands.remove(c);
                AddCommand.commandsAdded.remove(c);
            }
        }
    }
}
