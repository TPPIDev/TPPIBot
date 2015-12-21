package tterrag.tppibot.registry;

import java.util.ArrayList;
import java.util.List;

import org.pircbotx.Channel;

import tterrag.tppibot.commands.AddCommand;
import tterrag.tppibot.commands.CustomCommand;
import tterrag.tppibot.interfaces.ICommand;

public enum CommandRegistry {
    INSTANCE;

    private ArrayList<ICommand> commands = new ArrayList<ICommand>();

    public void registerCommand(ICommand c) {
        commands.add(c);
    }

    public List<ICommand> getCommands() {
        return commands;
    }

    public boolean isCommandRegistered(String s) {
        return commands.stream().anyMatch(c -> c.getIdent().equalsIgnoreCase(s));
    }

    public ICommand getCommand(String s) {
        return commands.stream().filter(c -> c.getIdent().equalsIgnoreCase(s)).findFirst().get();
    }

    public boolean unregisterCommand(String s, Channel channel) {
        for (int i = 0; i < commands.size(); i++) {
            ICommand c = commands.get(i);
            if (c.getIdent().equalsIgnoreCase(s)) {
                if (!(c instanceof CustomCommand) || ((CustomCommand) c).isFor(channel)) {
                    commands.remove(c);
                    AddCommand.commandsAdded.remove(c);
                    return true;
                }
            }
        }
        return false;
    }
}
