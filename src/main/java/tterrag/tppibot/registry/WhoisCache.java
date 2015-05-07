package tterrag.tppibot.registry;

import java.util.Map;

import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.WaitForQueue;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.NickChangeEvent;
import org.pircbotx.hooks.events.QuitEvent;
import org.pircbotx.hooks.events.WhoisEvent;

import tterrag.tppibot.Main;
import tterrag.tppibot.annotations.Subscribe;

import com.google.common.collect.Maps;

public class WhoisCache
{
    private static WhoisCache instance;
    
    public static WhoisCache instance()
    {
        return instance != null ? instance : (instance = new WhoisCache());
    }

    private Map<String, String> whoisCache = Maps.newHashMap();

    private WhoisCache()
    {
        EventHandler.registerReceiver(this);
    }

    public String getAccount(User user)
    {
        String acct = whoisCache.get(user.getNick());
        return acct != null ? acct : addOrUpdateEntry(user);
    }

    @SuppressWarnings("unchecked")
    public String addOrUpdateEntry(User user)
    {
        String acct;
        WaitForQueue waitForQueue = new WaitForQueue(Main.bot);
        WhoisEvent<PircBotX> test = null;
        try
        {
            Main.bot.sendRaw().rawLineNow("WHOIS " + user.getNick());
            test = waitForQueue.waitFor(WhoisEvent.class);
            waitForQueue.close();
            acct = test.getRegisteredAs();
        }
        catch (InterruptedException ex)
        {
            ex.printStackTrace();
            acct = null;
        }

        return whoisCache.put(user.getNick(), acct);
    }

    @Subscribe
    public void onJoin(JoinEvent<PircBotX> event)
    {
        addOrUpdateEntry(event.getUser());
    }

    @Subscribe
    public void onNickChange(NickChangeEvent<PircBotX> event)
    {
        whoisCache.remove(event.getUser().getNick());
        addOrUpdateEntry(event.getUser());
    }

    @Subscribe
    public void onQuit(QuitEvent<PircBotX> event)
    {
        whoisCache.remove(event.getUser().getNick());
    }
}
