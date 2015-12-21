package tterrag.tppibot.commands;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

import tterrag.tppibot.registry.PermRegistry;
import tterrag.tppibot.runnables.MessageSender;
import tterrag.tppibot.util.IRCUtils;

public class CustomCommand extends Command {

    private String message;
    private String channel;
    private boolean isAction = false;

    public CustomCommand(String ident, PermLevel perms, String message) {
        this(ident, perms, message, null);
    }

    public CustomCommand(String ident, PermLevel perms, String message, String channel) {
        super(ident, perms);
        this.message = message;
        this.channel = channel;
    }

    public CustomCommand setIsAction(boolean set) {
        isAction = set;
        return this;
    }

    @Override
    public void onCommand(PircBotX bot, User user, Channel channel, List<String> lines, String... args) {
        if ((channel != null && channel.getName().equals(this.channel)) || this.channel == null) {
            String to = channel == null ? user.getNick() : channel.getName();
            String msg = IRCUtils.getMessageWithArgs(user, message, args);

            if (this.isAction) {
                MessageSender.INSTANCE.enqueueAction(bot, to, msg);
            } else {
                MessageSender.INSTANCE.enqueue(bot, channel == null ? user.getNick() : channel.getName(), IRCUtils.getMessageWithArgs(user, message, args));
            }
        }
    }

    @Override
    public Command editCommand(PircBotX bot, User user, Channel channel, List<String> lines, String... args) {
        AddCommand.commandsAdded.remove(this);
        PermLevel level = PermLevel.INVALID;
        PermLevel userLevel = PermRegistry.INSTANCE.getPermLevelForUser(channel, user);

        if (args.length > 0 && args[0].startsWith("-permLevel=")) {
            String perm = args[0].substring(11);
            try {
                level = PermLevel.valueOf(perm.toUpperCase());
            } catch (Exception e) {
                lines.add("Invalid perm level \"" + perm + ".\" Valid perm levels: " + Arrays.deepToString(PermLevel.getSettablePermLevels()));
            }

            if (level != PermLevel.INVALID && IRCUtils.isPermLevelAboveOrEqualTo(userLevel, level)) {
                this.setPermLevel(level);
            } else {
                lines.add("You do not have the required perm level to do this. You must be at least: " + level.toString() + ".");
            }
        } else if ((this.channel != null && this.channel.equals(channel.getName())) || (this.channel == null && userLevel == PermLevel.CONTROLLER)) {
            this.message = StringUtils.join(args, " ");
        } else if (this.channel == null) {
            lines.add("You cannot edit global commands.");
        } else {
            lines.add("No such command in this channel!");
        }

        AddCommand.commandsAdded.add(this);
        return this;
    }

    @Override
    public String getDesc() {
        return "A custom command that was added by 'addcmd'. Output text is: \"" + this.message + ".\" Registered to : " + (this.channel == null ? "GLOBAL" : this.channel) + ".";
    }

    public boolean isFor(Channel channel) {
        return channel == null ? this.channel == null : this.channel == null || channel.getName().equals(this.channel);
    }
}
