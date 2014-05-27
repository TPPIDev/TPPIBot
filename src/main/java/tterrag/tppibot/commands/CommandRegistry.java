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
}
