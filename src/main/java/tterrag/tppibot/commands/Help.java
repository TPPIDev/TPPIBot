package tterrag.tppibot.commands;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

import tterrag.tppibot.interfaces.ICommand;
import tterrag.tppibot.listeners.MessageListener;
import tterrag.tppibot.registry.CommandRegistry;
import tterrag.tppibot.registry.PermRegistry;
import tterrag.tppibot.util.IRCUtils;

public class Help extends Command {

    private String helpText = "%user%, try " + MessageListener.controlChar + "help <command name>. To see all commands, use " + MessageListener.controlChar + "commands";

    public Help() {
        super("help", PermLevel.DEFAULT);
    }

    @Override
    public void onCommand(PircBotX bot, User user, Channel channel, List<String> lines, String... args) {
        if (args.length < 1) {
            if (channel != null) {
                user.send().notice("Your current perm level is: " + PermRegistry.INSTANCE.getPermLevelForUser(channel, user) + ".");
            }

            lines.add(IRCUtils.getMessageWithArgs(user, "To get help on specific commands " + (channel == null ? helpText.replace(MessageListener.controlChar, "\"") + "\"" : helpText), args));
        } else {
            lines.add(IRCUtils.getMessageWithArgs(user, "%user% - Info on commands:", new String[] {}));

            for (String s : args) {
                if (CommandRegistry.INSTANCE.isCommandRegistered(s)) {
                    ICommand c = CommandRegistry.INSTANCE.getCommand(s);
                    lines.add(String.format("Info on %s: %s %s: %s", s, c.getDesc(), "Required perm level", c.getPermLevel().toString()));
                }
            }
        }
    }

    @Override
    public ICommand editCommand(PircBotX bot, User user, Channel channel, List<String> lines, String... args) {
        if (args.length < 1)
            return this;

        String newText = StringUtils.join(args, ' ');

        this.helpText = newText;
        return this;
    }

    @Override
    public String getDesc() {
        return "Don't Panic.";
    }
}
