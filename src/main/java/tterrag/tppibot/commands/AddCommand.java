package tterrag.tppibot.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.pircbotx.hooks.events.MessageEvent;

import tterrag.tppibot.Main;
import tterrag.tppibot.annotations.ReceiveExitEvent;
import tterrag.tppibot.config.Config;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class AddCommand extends Command
{
    private Config config;
    
    private List<CustomCommand> added = new ArrayList<CustomCommand>();
    
    public AddCommand()
    {
        super("addcmd", PermLevel.OP);
        config = new Config("customCommands.json");
        
        added = new Gson().fromJson(config.getText(), new TypeToken<List<CustomCommand>>(){}.getType());
    }

    @Override
    public boolean onCommand(MessageEvent<?> event, String... args)
    {
        if (args.length < 2)
        {
            sendNotice(event.getUser(), "This requires at least two args, [command name] and [message]!");
            return false;
        }

        String cmdName = args[0];

        args = ArrayUtils.remove(args, 0);

        String toAdd = StringUtils.join(args, ' ');

        Main.getCommandRegistry().registerCommand(new CustomCommand(cmdName, PermLevel.ANY, toAdd));
        
        sendNotice(event.getUser(), "Registered command " + cmdName);

        return true;
    }
    
    @ReceiveExitEvent
    public void onExitEvent()
    {
        config.addJsonToFile(added);
    }
}
