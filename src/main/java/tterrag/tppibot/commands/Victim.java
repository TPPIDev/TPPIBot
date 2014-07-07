package tterrag.tppibot.commands;

import java.util.List;

import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.events.DisconnectEvent;

import tterrag.tppibot.annotations.Subscribe;
import tterrag.tppibot.config.Config;
import tterrag.tppibot.util.IRCUtils;

public class Victim extends Command
{
    private int n;
    private Config config;

    private long lastUsed = 0;

    public Victim()
    {
        super("victim", PermLevel.DEFAULT);

        config = new Config("victim.json");

        String text = config.getText();
        if (text == "")
            n = 0;
        else
            n = Integer.parseInt(text);
    }

    @Override
    public void onCommand(PircBotX bot, User user, List<String> lines, String... args)
    {
        synchronized (this)
        {
            if (System.currentTimeMillis() - lastUsed > 10000)
            {
                lines.add(IRCUtils.getMessageForUser(user, "%user% has fallen victim to the slash bug! That's " + (++n) + " so far!", args));
                lastUsed = System.currentTimeMillis();
            }
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
        config.writeInt(n);
    }
}
