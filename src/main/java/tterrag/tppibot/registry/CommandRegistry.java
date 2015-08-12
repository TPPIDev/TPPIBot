package tterrag.tppibot.registry;

import java.util.ArrayList;
import java.util.List;

import org.pircbotx.Channel;

import tterrag.tppibot.commands.AddCommand;
import tterrag.tppibot.commands.CustomCommand;
import tterrag.tppibot.interfaces.ICommand;

public enum CommandRegistry
{
    INSTANCE;
    
    private ArrayList<ICommand> commands = new ArrayList<ICommand>();

    public void registerCommand(ICommand c)
    {
        commands.add(c);
    }

    public List<ICommand> getCommands()
    {
        return commands;
    }

    public boolean isCommandRegistered(String s)
    {
        for (ICommand c : commands)
        {
            if (c.getIdent().equalsIgnoreCase(s))
                return true;
        }
        return false;
    }

    public ICommand getCommand(String s)
    {
        for (ICommand c : commands)
        {
            if (c.getIdent().equalsIgnoreCase(s))
                return c;
        }
        return null;
    }

    public boolean unregisterCommand(String s, Channel channel)
    {
        for (int i = 0; i < commands.size(); i++)
        {
            ICommand c = commands.get(i);
            if (c.getIdent().equalsIgnoreCase(s))
            {
                if (!(c instanceof CustomCommand) || ((CustomCommand) c).isFor(channel))
                {
                    commands.remove(c);
                    AddCommand.commandsAdded.remove(c);
                    return true;
                }
            }
        }
        return false;
    }
}
