package tterrag.tppibot.interfaces;

import java.util.List;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

public interface ICommand
{
    public enum PermLevel
    {
        /**
         * Not allowed to use any commands
         */
        NONE,
        
        /**
         * Lowest value, all users have this even if not assigned
         */
        DEFAULT,

        /*
         * Custom levels, these are assignable and in order of power
         */
        TRUSTED, OP, CONTROLLER,

        /**
         * Used for error handling
         */
        INVALID;

        public static PermLevel[] getSettablePermLevels()
        {
            return new PermLevel[] { NONE, CONTROLLER, OP, TRUSTED, DEFAULT };
        }
    }

    /**
     * Does any base initialization for the command, should be called on construction e.g. <code>new ICommand().create()</code>
     */
    public ICommand create();

    /**
     * The identity of this command, e.g. the word after the control character that would activate this command
     */
    public String getIdent();

    /**
     * The level required to use this command
     * 
     * @return {@link PermLevel}
     */
    public PermLevel getPermLevel();

    /**
     * Does this command need operator status to do anything useful
     */
    public boolean needsOp();

    /**
     * Amount of arguments this command requires
     */
    public int argsNeeded();
    
    /**
     * Called when this command is used
     * 
     * @param channel - channel it was called from
     * @param user - user that called it
     * @param args - any args after the command (split by space)
     * @return whether the command processing was successful
     */
    public void onCommand(PircBotX bot, User user, Channel channel, List<String> lines, String... args);

    /**
     * Edits the command
     * 
     * @param args - Could be anything, needs to be strictly checked for each command impl
     * @return the command object
     * 
     */
    public ICommand editCommand(PircBotX bot, User user, Channel channel, List<String> lines, String... args);

    /**
     * The description of this command, for help purposes
     */
    public String getDesc();

    /**
     * Does something without a channel or user reference, used via the command line
     */
    boolean handleConsoleCommand(String... args);
    
    /**
     * Can this command be called without a channel reference (i.e. in PM)
     */
    public boolean executeWithoutChannel();
}
