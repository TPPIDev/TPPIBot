package tterrag.tppibot.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.events.DisconnectEvent;

import tterrag.tppibot.Main;
import tterrag.tppibot.annotations.Subscribe;
import tterrag.tppibot.config.Config;

import com.google.gson.reflect.TypeToken;

public class Mode extends Command
{
    public enum BotMode
    {
        MESSAGE, NOTICE, PM
    }

    private static Map<String, BotMode> modes;
    private Config modeConfig;

    public Mode()
    {
        super("mode", PermLevel.OP);

        modeConfig = new Config("modes.json");
        modes = Main.gson.fromJson(modeConfig.getText(), new TypeToken<Map<String, BotMode>>() {}.getType());
        if (modes == null)
            modes = new HashMap<String, BotMode>();
    }

    @Override
    public void onCommand(PircBotX bot, User user, Channel channel, List<String> lines, String... args)
    {
        if (args.length >= 1)
        {
            try
            {
                synchronized (modes)
                {
                    modes.put(channel.getName(), BotMode.valueOf(args[0].toUpperCase()));
                    lines.add("Mode set to " + modes.get(channel.getName()));
                }
            }
            catch (IllegalArgumentException e)
            {
                lines.add("No such mode " + args[0] + "! Valid modes are: " + Arrays.deepToString(BotMode.values()));
            }
        }
        else
        {
            lines.add("Current mode is: " + modes.get(channel.getName()));
        }
    }

    public static void initMode(String channel, BotMode mode)
    {
        if (modes.get(channel) == null)
        {
            synchronized (modes)
            {
                modes.put(channel, mode);  
            }
        }
    }
    
    public static BotMode getMode(String channel)
    {
        synchronized (modes)
        {
            return modes.get(channel);
        }
    }

    @Override
    public boolean executeWithoutChannel()
    {
        return false;
    }
    
    @Override
    public boolean shouldReceiveEvents()
    {
        return true;
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent<PircBotX> event)
    {
        modeConfig.writeJsonToFile(modes);
    }
}
