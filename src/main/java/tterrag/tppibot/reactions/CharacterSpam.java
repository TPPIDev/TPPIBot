package tterrag.tppibot.reactions;

import static tterrag.tppibot.reactions.CharacterSpam.SpamReasons.REPEATS;
import static tterrag.tppibot.reactions.CharacterSpam.SpamReasons.SYMBOLS;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.events.DisconnectEvent;
import org.pircbotx.hooks.events.MessageEvent;

import tterrag.tppibot.Main;
import tterrag.tppibot.annotations.Subscribe;
import tterrag.tppibot.config.Config;
import tterrag.tppibot.interfaces.ICommand.PermLevel;
import tterrag.tppibot.interfaces.IReaction;
import tterrag.tppibot.runnables.MessageSender;
import tterrag.tppibot.util.IRCUtils;
import tterrag.tppibot.util.Logging;

import com.google.common.collect.Sets;
import com.google.gson.reflect.TypeToken;

public class CharacterSpam implements IReaction
{
    public enum SpamReasons
    {
        REPEATS("You had too many repeated characters."), 
        SYMBOLS("You had too many non-alphabetic symbols."), 
        CAPS("Too much caps!"), 
        FLOOD("Too many messages!");

        private String text;

        SpamReasons(String text)
        {
            this.text = text;
        }

        public String getText()
        {
            return text;
        }
    }

    private Map<Character, Integer> repeated;

    private Map<String, Integer> strikes;
    private Config strikesConfig;

    private static Set<String> blacklistChannels = Sets.newConcurrentHashSet();
    private Config blacklistConfig;
    
    private static List<Character> whitelistedChars = Arrays.asList(new Character[]{' ', '.'});

    public CharacterSpam()
    {
        repeated = new HashMap<Character, Integer>();

        strikesConfig = new Config("spamStrikes.json");
        blacklistConfig = new Config("spamChannelBlacklist.json");

        strikes = Main.gson.fromJson(strikesConfig.getText(), new TypeToken<Map<String, Integer>>() {}.getType());
        blacklistChannels = Main.gson.fromJson(blacklistConfig.getText(), new TypeToken<Set<String>>() {}.getType());

        if (strikes == null)
            strikes = new HashMap<String, Integer>();
        if (blacklistChannels == null)
            blacklistChannels = Sets.newConcurrentHashSet();
    }

    @Override
    public synchronized void onMessage(MessageEvent<?> event)
    {
        int symbolCount = 0;
        int caps = 0;

        String msg = event.getMessage();

        if (msg.length() < 12)
            return;

        if (blacklistChannels.contains(event.getChannel().getName().toLowerCase()))
            return;

        for (char c : msg.toCharArray())
        {
            if (!((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || whitelistedChars.contains(c)))
            {
                symbolCount++;
            }
            
            if (c >= 'A' && c <= 'Z')
            {
                caps++;
            }

            if (repeated.containsKey(c))
            {
                repeated.put(c, repeated.get(c) + 1);
            }
            else
            {
                repeated.put(c, 1);
            }
        }

        for (char c : repeated.keySet())
        {
            if (repeated.get(c) > msg.length() / 2)
            {
                Logging.log("too many repeated characters!");
                finish(timeout(event, REPEATS) ? event.getUser() : null);
                return;
            }
        }

        if (symbolCount > msg.length() / 2)
        {
            Logging.log("too many symbols!");
            finish(timeout(event, SYMBOLS) ? event.getUser() : null);
            return;
        }
        
// No caps for now
//        if (caps > (double) msg.length() / 1.75d)
//        {
//            Logging.log("caps!");
//            finish(timeout(event, CAPS) ? event.getUser() : null);
//            return;
//        }

        finish(null);
    }

    public void finish(User user)
    {
        repeated.clear();

        if (user == null)
            return;

        String hostmask = user.getHostmask();

        if (strikes.containsKey(hostmask))
        {
            strikes.put(hostmask, strikes.get(hostmask) + 1);
        }
        else
        {
            strikes.put(hostmask, 1);
        }
    }

    private boolean timeout(MessageEvent<?> event, SpamReasons reason)
    {
        return timeout(event.getBot(), event.getUser(), event.getChannel(), reason);
    }
    
    public boolean timeout(PircBotX bot, User user, Channel channel, SpamReasons reason)
    {
        if (IRCUtils.userIsOp(channel, bot.getUserBot()) && !IRCUtils.isUserAboveOrEqualTo(channel, PermLevel.TRUSTED, user))
        {
            int strikeCount = 0;
            if (strikes.containsKey(user.getHostmask()))
            {
                strikeCount = strikes.get(user.getHostmask());
            }

            if (strikeCount < 3)
            {
                MessageSender.instance.enqueue(bot, channel.getName(), user.getNick() + ", please do not do that! This is strike " + (strikeCount + 1) + "! Reason: " + reason.getText());
            }
            else
            {
                MessageSender.instance.enqueue(bot, channel.getName(), user.getNick() + ", please do not do that! This is strike " + (strikeCount + 1) + ", you will now be timed out for "
                        + (5 * (strikeCount - 2)) + " minutes. Reason: " + reason.getText());
                IRCUtils.timeout(bot, user, channel, "" + 5 * (strikeCount - 2));
            }
            return true;
        }
        return false;
    }

    /**
     * @return true if added, false if removed
     */
    public static boolean toggleBlacklistChannel(String channelname)
    {
        synchronized (blacklistChannels)
        {
            channelname = channelname.toLowerCase();
            if (blacklistChannels.contains(channelname))
            {
                blacklistChannels.remove(channelname);
                return false;
            }
            else
            {
                blacklistChannels.add(channelname);
                return true;
            }
        }
    }
    
    public boolean filtersEnabled(String channelname)
    {
        synchronized (blacklistChannels)
        {
            channelname = channelname.toLowerCase();
            return !blacklistChannels.contains(channelname);
        }
    }
    
    public int getStrikes(User user)
    {
        return strikes.get(user.getHostmask());
    }
    
    public int setStrikes(User user, int amnt)
    {
        amnt = Math.max(0, amnt);
        strikes.put(user.getHostmask(), amnt);
        return amnt;
    }
    
    public int addStrikes(User user, int amnt)
    {
        if (!strikes.containsKey(user.getHostmask()))
            return setStrikes(user, amnt);
        else
            return setStrikes(user, getStrikes(user) + amnt);
    }

    public int removeStrikes(User user, int amnt)
    {
        return addStrikes(user, -amnt);
    }
    
    @Subscribe
    public void onDisconnect(DisconnectEvent<?> event)
    {
        strikesConfig.writeJsonToFile(strikes);
        blacklistConfig.writeJsonToFile(blacklistChannels);
    }
}
