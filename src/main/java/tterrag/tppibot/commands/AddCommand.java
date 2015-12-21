package tterrag.tppibot.commands;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.events.DisconnectEvent;

import tterrag.tppibot.config.Config;
import tterrag.tppibot.interfaces.ICommand;
import tterrag.tppibot.registry.CommandRegistry;
import tterrag.tppibot.registry.PermRegistry;

import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class AddCommand extends Command {

    private Config config;

    public static Set<CustomCommand> commandsAdded = Sets.newConcurrentHashSet();

    public AddCommand() {
        super("addcmd", PermLevel.TRUSTED);
        config = new Config("customCommands.json");

        commandsAdded = new Gson().fromJson(config.getText(), new TypeToken<Set<CustomCommand>>() {
        }.getType());

        if (commandsAdded == null)
            commandsAdded = Sets.newConcurrentHashSet();

        for (ICommand c : commandsAdded) {
            CommandRegistry.INSTANCE.registerCommand(c);
        }
    }

    @Override
    public void onCommand(PircBotX bot, User user, Channel channel, List<String> lines, String... args) {
        boolean global = false;
        boolean action = false;
        PermLevel level = PermLevel.DEFAULT;
        if (args.length > 0) {
            while (args[0].startsWith("-")) {
                global |= args[0].equalsIgnoreCase("-global");
                action |= args[0].equalsIgnoreCase("-action");

                if (args[0].toLowerCase().contains("-permlevel=")) {
                    String levelStr = args[0].split("=")[1];
                    try {
                        level = PermLevel.valueOf(levelStr.toUpperCase(Locale.ENGLISH));
                    } catch (Exception e) {
                        lines.add(levelStr + " is not a valid perm level. Using default.");
                    }
                }
                args = ArrayUtils.remove(args, 0);
            }
        }

        if (args.length < 2) {
            lines.add("This requires at least two args, [command name] and [message]!");
            return;
        }

        String cmdName = args[0];

        args = ArrayUtils.remove(args, 0);

        String toAdd = StringUtils.join(args, ' ');

        CustomCommand command = null;

        if (global && PermRegistry.INSTANCE.isController(user)) {
            command = new CustomCommand(cmdName, level, toAdd);
        } else if (global) {
            lines.add("You must be a controller to add global commands!");
            return;
        } else if (channel != null) {
            command = new CustomCommand(cmdName, level, toAdd, channel.getName());
        } else {
            lines.add("You cannot add non-global commands in private message!");
            return;
        }

        command.setIsAction(action);

        commandsAdded.add(command);

        lines.add("Registered " + (global ? "global " : "") + "command " + cmdName);
    }

    @Override
    public String getDesc() {
        return "Adds a command with the name of the first argument and the output of any following arguments to the command registry.";
    }

    @Override
    public boolean shouldReceiveEvents() {
        return true;
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent<PircBotX> event) {
        config.writeJsonToFile(commandsAdded);
    }
}
