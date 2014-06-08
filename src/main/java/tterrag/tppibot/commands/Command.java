package tterrag.tppibot.commands;

import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import tterrag.tppibot.interfaces.ICommand;
import tterrag.tppibot.registry.CommandRegistry;
import tterrag.tppibot.registry.EventHandler;

public abstract class Command implements ICommand
{
    private PermLevel level;
    private String ident;

    protected static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    protected Command(String ident, PermLevel level)
    {
        this.ident = ident.toLowerCase();
        this.level = level;
    }

    @Override
    public ICommand create()
    {
        CommandRegistry.registerCommand(this);

        if (shouldReceiveEvents())
            EventHandler.registerReceiver(this);

        return this;
    }

    public boolean shouldReceiveEvents()
    {
        return false;
    }

    @Override
    public String getIdent()
    {
        return ident;
    }

    @Override
    public PermLevel getPermLevel()
    {
        return level;
    }

    public ICommand setPermLevel(PermLevel level)
    {
        this.level = level;
        return this;
    }

    public void sendMessage(Channel channel, String message)
    {
        channel.send().message(message);
    }

    public void sendNotice(User user, String message)
    {
        user.send().notice(message);
    }

    /**
     * Called when this command is used
     * 
     * @param channel - channel it was called from
     * @param user - user that called it
     * @param args - any args after the command (split by space)
     * @return whether the command processing was successful
     */
    @Override
    public abstract boolean onCommand(MessageEvent<?> event, String... args);

    /**
     * Edits the command
     * 
     * @param params - Could be anything, needs to be strictly checked for each
     *            command impl
     * @return the command object
     */
    @Override
    public Command editCommand(String... params)
    {
        return this;
    }
    
    @Override
    public boolean handleConsoleCommand(String... args)
    {
        return false;
    }

    @Override
    public String getDesc()
    {
        return "";
    }
}
