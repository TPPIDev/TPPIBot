package tterrag.tppibot.interfaces;

import org.pircbotx.hooks.events.MessageEvent;

import tterrag.tppibot.commands.Command;

public interface ICommand
{
    public enum PermLevel
    {
        CONTROLLER, OP, TRUSTED, CHANOP, VOICE, ALL
    }

    /**
     * Does any base initialization for the command, should be called on
     * construction e.g. <code>new ICommand().create()</code>
     */
    public ICommand create();

    /**
     * The identity of this command, e.g. the word after the control character
     * that would activate this command
     */
    public String getIdent();

    /**
     * The level required to use this command
     * 
     * @return {@link PermLevel}
     */
    public PermLevel getPermLevel();

    /**
     * Called when this command is used
     * 
     * @param channel - channel it was called from
     * @param user - user that called it
     * @param args - any args after the command (split by space)
     * @return whether the command processing was successful
     */
    public boolean onCommand(MessageEvent<?> event, String... args);

    /**
     * Edits the command
     * 
     * @param params - Could be anything, needs to be strictly checked for each
     *            command impl
     * @return the command object
     */
    public Command editCommand(String... params);

    /**
     * The description of this command, for help purposes
     */
    public String getDesc();
}
