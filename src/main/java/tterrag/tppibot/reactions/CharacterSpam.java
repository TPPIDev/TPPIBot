package tterrag.tppibot.reactions;

import java.util.HashMap;
import java.util.Map;

import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

import tterrag.tppibot.commands.Command;
import tterrag.tppibot.interfaces.IReaction;
import tterrag.tppibot.registry.CommandRegistry;

public class CharacterSpam implements IReaction
{
    private Map<Character, Integer> repeated = new HashMap<Character, Integer>();
    
    @Override
    public void onMessage(MessageEvent<?> event)
    {
        int symbolCount = 0;
        
        String msg = event.getMessage();
        
        if (msg.length() < 8)
            return;
        
        for (char c : msg.toCharArray())
        {
            if (c < 'A' && c > 'z')
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
                timeout(event, 5);
            }
        }
        
        if (symbolCount > msg.length() / 3)
        {
            timeout(event, 5);
        }
        
        repeated.clear();
    }

    private void timeout(MessageEvent<?> event, int i)
    {
        Command quiet = (Command) CommandRegistry.getCommand("timeout");
        quiet.sendMessage(event.getChannel(), event.getUser().getNick() + ", please do not spam!");   
        quiet.onCommand(event, event.getUser().getNick(), "" + 5);
    }
}
