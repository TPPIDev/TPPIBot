package tterrag.tppibot.reactions;

import java.util.HashMap;
import java.util.Map;

import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.events.DisconnectEvent;
import org.pircbotx.hooks.events.MessageEvent;

import tterrag.tppibot.Main;
import tterrag.tppibot.annotations.Subscribe;
import tterrag.tppibot.commands.Command;
import tterrag.tppibot.config.Config;
import tterrag.tppibot.interfaces.IReaction;
import tterrag.tppibot.registry.CommandRegistry;
import tterrag.tppibot.util.Logging;
import static tterrag.tppibot.reactions.CharacterSpam.SpamReasons.*;

import com.google.gson.reflect.TypeToken;

public class CharacterSpam implements IReaction
{
    public enum SpamReasons
    {
        REPEATS("You had too many repeated characters."), SYMBOLS("You had too many non-alphabetic symbols.");
        
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
    
    public CharacterSpam()
    {
        repeated = new HashMap<Character, Integer>();
        
        strikesConfig = new Config("spamStrikes.json");
        
        strikes = Main.gson.fromJson(strikesConfig.getText(), new TypeToken<Map<String, Integer>>(){}.getType());
        
        if (strikes == null)
            strikes = new HashMap<String, Integer>();
    }
    
    @Override
    public synchronized void onMessage(MessageEvent<?> event)
    {
        int symbolCount = 0;

        String msg = event.getMessage();

        if (msg.length() < 12)
            return;

        for (char c : msg.toCharArray())
        {
            if (!((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == ' '))
            {
                symbolCount++;
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
            if (repeated.get(c) > msg.length() / 3)
            {
                Logging.log("too many repeated characters!");
                timeout(event, 5, REPEATS);
                finish(event.getUser());
                return;
            }
        }

        if (symbolCount > msg.length() / 3)
        {
            Logging.log("too many symbols!");
            timeout(event, 5, SYMBOLS);
            finish(event.getUser());
            return;
        }
        
        finish(null);
    }

    private void finish(User user)
    {
        repeated.clear();
        
        if (user == null) return;
        
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

    private void timeout(MessageEvent<?> event, int i, SpamReasons reason)
    {
        Command quiet = (Command) CommandRegistry.getCommand("timeout");
        
        int strikeCount = 0;
        if (strikes.containsKey(event.getUser().getHostmask()))
        {
            strikeCount = strikes.get(event.getUser().getHostmask());
        }
        
        if (strikeCount < 3)
        {
            quiet.sendMessage(event.getChannel(), event.getUser().getNick() + ", please do not spam! This is strike " + (strikeCount + 1) + "! Reason: " + reason.getText());
        }
        else
        {
            quiet.sendMessage(event.getChannel(), event.getUser().getNick() + ", please do not spam! This is strike " + (strikeCount + 1) + ", you will now be timed out for " + (5 * (strikeCount - 2)) + " minutes. Reason: " + reason.getText());
            quiet.onCommand(new MessageEvent<PircBotX>(event.getBot(), event.getChannel(), event.getBot().getUserBot(), event.getMessage()), event.getUser().getNick(), "" + (5 * (strikeCount - 2)));
        }
    }
    
    @Subscribe
    public void onDisconnect(DisconnectEvent<?> event)
    {
        strikesConfig.writeJsonToFile(strikes);
    }
}
