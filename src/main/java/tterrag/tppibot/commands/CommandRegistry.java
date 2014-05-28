package tterrag.tppibot.commands;

import java.util.ArrayList;
import java.util.List;

public class CommandRegistry
{
    private ArrayList<Command> commands;

    public CommandRegistry()
    {
        commands = new ArrayList<Command>();
    }

    public void registerCommand(Command c)
    {
        commands.add(c);
    }

    public List<Command> getCommands()
    {
        return commands;
    }

    public boolean isCommandRegistered(String s)
    {
        for (Command c : commands)
        {
            if (c.getName().equalsIgnoreCase(s))
                return true;
        }
        return false;
    }
    
    public Command getCommand(String s)
    {
        for (Command c : commands)
        {
            if (c.getName().equalsIgnoreCase(s))
                return c;
        }
        return null;
    }
}
