package tterrag.tppibot.commands;

import java.util.List;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

import tterrag.tppibot.registry.CommandRegistry;

public class RemoveCommand extends Command {

    public RemoveCommand() {
        super("delcmd", PermLevel.OP);
    }

    @Override
    public void onCommand(PircBotX bot, User user, Channel channel, List<String> lines, String... args) {
        if (args.length < 1) {
            lines.add("No command specified.");
            return;
        }

        for (String s : args) {
            if (CommandRegistry.INSTANCE.unregisterCommand(s, channel)) {
                lines.add("Successfully removed command: \"" + s + "\"");
            } else {
                lines.add("You cannot remove this command from here.");
            }
        }
    }

    @Override
    public String getDesc() {
        return "Deletes a command from the registry. Default command removals will not persist between loads.";
    }
}
