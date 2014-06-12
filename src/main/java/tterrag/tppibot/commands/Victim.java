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

    private long lastUsed = 0;

    public Victim()
    {
        super("victim", PermLevel.DEFAULT);

        config = new Config("victim.json");

        n = new Gson().fromJson(config.getText(), Integer.class);
    }

    @Override
    public boolean onCommand(MessageEvent<?> event, String... args)
    {
        synchronized (this)
        {
            if (System.currentTimeMillis() - lastUsed > 10000)
            {
                IRCUtils.sendMessageForUser(event.getChannel(), event.getUser(), "%user% has fallen victim to the slash bug! That's " + (++n) + " so far!", args);
                lastUsed = System.currentTimeMillis();
            }
            return true;
        }
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
