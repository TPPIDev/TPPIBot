package tterrag.tppibot.commands;

import tterrag.tppibot.Main;

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
    
    public void sendMessage(String channel, String message)
    {
        Main.bot.sendMessage(channel, message);
    }
    
    public void sendNotice(String user, String message)
    {
        Main.bot.sendNotice(user, message);
    }

    /**
     * Called when this command is used
     * 
     * @param channel - channel it was called from
     * @param user - user that called it
     * @param args - any args after the command (split by space)
     * @return whether the command processing was successful
     */
    public abstract boolean onCommand(String channel, String user, String... args);

    /**
     * Edits the command
     * @param params - Could be anything, needs to be strictly checked for each command impl
     * @return the command object
     */
    public Command editCommand(Object... params)
    {
        return this;
    }
}
