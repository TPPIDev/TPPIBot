package tterrag.tppibot.commands;

import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

public abstract class Command
{
    public enum PermLevel
    {
        OP, VOICE, ANY
    }

    private PermLevel level;
    private final String ident;

    protected Command(String ident, PermLevel level)
    {
        this.ident = ident.toLowerCase();
        this.level = level;
    }

    public String getName()
    {
        return ident;
    }

    public PermLevel getPermLevel()
    {
        return level;
    }

    public Command setPermLevel(PermLevel level)
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
    public abstract boolean onCommand(MessageEvent<?> event, String... args);

    /**
     * Edits the command
     * 
     * @param params - Could be anything, needs to be strictly checked for each
     *            command impl
     * @return the command object
     */
    public Command editCommand(String... params)
    {
        return this;
    }
    
    public String getDesc()
    {
        return "";
    }
}
