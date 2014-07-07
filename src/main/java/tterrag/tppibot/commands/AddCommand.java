package tterrag.tppibot.commands;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.events.DisconnectEvent;

import tterrag.tppibot.annotations.Subscribe;
import tterrag.tppibot.config.Config;
import tterrag.tppibot.interfaces.ICommand;
import tterrag.tppibot.registry.CommandRegistry;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class AddCommand extends Command
{
    private Config config;

    public static Set<CustomCommand> commandsAdded = Sets.newConcurrentHashSet();

    public AddCommand()
    {
        super("addcmd", PermLevel.TRUSTED);
        config = new Config("customCommands.json");

        commandsAdded = new Gson().fromJson(config.getText(), new TypeToken<Set<CustomCommand>>(){}.getType());

        if (commandsAdded == null)
            commandsAdded = Sets.newConcurrentHashSet();

        for (ICommand c : commandsAdded)
        {
            CommandRegistry.registerCommand(c);
        }
    }

    @Override
    public void onCommand(PircBotX bot, User user, List<String> lines, String... args)
    {
        if (args.length < 2)
        {
            lines.add("This requires at least two args, [command name] and [message]!");
            return;
        }

        String cmdName = args[0];

        args = ArrayUtils.remove(args, 0);

        String toAdd = StringUtils.join(args, ' ');

        if (commandAlreadyRegistered(cmdName))
        {
            lines.add(cmdName + " is already registered!");
            return ;
        }

        CustomCommand command = new CustomCommand(cmdName, PermLevel.DEFAULT, toAdd);
        commandsAdded.add(command);

        lines.add("Registered command " + cmdName);
    }

    private boolean commandAlreadyRegistered(String cmdName)
    {
        return CommandRegistry.getCommand(cmdName) != null;
    }

    @Override
    public String getDesc()
    {
        return "Adds a command with the name of the first argument and the output of any following arguments to the command registry.";
    }

    @Override
    public boolean shouldReceiveEvents()
    {
        return true;
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent<PircBotX> event)
    {
        config.writeJsonToFile(commandsAdded);
    }
}
