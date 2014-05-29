package tterrag.tppibot.commands;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.DisconnectEvent;
import org.pircbotx.hooks.events.MessageEvent;

import tterrag.tppibot.annotations.Subscribe;
import tterrag.tppibot.config.Config;
import tterrag.tppibot.util.IRCUtils;

import com.google.gson.Gson;

public class Victim extends Command
{
    private int n;
    private Config config;
    
    public Victim()
    {
        super("victim", PermLevel.ALL);
        
        config = new Config("victim.json");
        
        n = new Gson().fromJson(config.getText(), Integer.class);
    }
    
    @Override
    public boolean onCommand(MessageEvent<?> event, String... args)
    {
        IRCUtils.sendMessageForUser(event.getChannel(), event.getUser(), "%user% has fallen fictim to the slash bug! That's " + (++n) + " so far!", args);
        return true;
    }
    
    @Override
    public boolean shouldReceiveEvents()
    {
        return true;
    }
    
    @Subscribe
    public void onDisconnect(DisconnectEvent<PircBotX> event)
    {
        config.writeJsonToFile(n);
    }
}
