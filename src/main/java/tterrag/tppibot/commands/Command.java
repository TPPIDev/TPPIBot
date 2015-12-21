package tterrag.tppibot.commands;

import java.util.List;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

import tterrag.tppibot.interfaces.IChannelCommand;
import tterrag.tppibot.interfaces.ICommand;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class Command implements ICommand {

    private PermLevel level;
    private final String ident;
    private int argsNeeded;

    protected static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    protected Command(String ident) {
        this(ident, PermLevel.DEFAULT, 0);
    }

    protected Command(String ident, PermLevel level) {
        this(ident, level, 0);
    }

    public Command(String ident, PermLevel level, int args) {
        this.ident = ident.toLowerCase();
        this.level = level;
        this.argsNeeded = args;

        create();
    }

    protected ICommand setArgsNeeded(int args) {
        argsNeeded = args;
        return this;
    }

    public ICommand setPermLevel(PermLevel level) {
        this.level = level;
        return this;
    }

    /* === ICommand === */

    @Override
    public String getIdent() {
        return ident;
    }

    @Override
    public PermLevel getPermLevel() {
        return level;
    }

    @Override
    public int argsNeeded() {
        return argsNeeded;
    }

    /**
     * Called when this command is used
     * 
     * @param channel
     *            - channel it was called from
     * @param user
     *            - user that called it
     * @param args
     *            - any args after the command (split by space)
     * @return whether the command processing was successful
     */
    @Override
    public void onCommand(PircBotX bot, User user, Channel channel, List<String> lines, String... args) {
        throw new UnsupportedOperationException(this instanceof IChannelCommand ? "Please use the alternate onCommand method from IChannelOnlyCommand." : "Please implement this method.");
    }
}
