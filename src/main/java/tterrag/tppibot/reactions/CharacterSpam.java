package tterrag.tppibot.reactions;

import java.util.HashMap;
import java.util.Map;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;

import tterrag.tppibot.commands.Command;
import tterrag.tppibot.interfaces.IReaction;
import tterrag.tppibot.registry.CommandRegistry;
import tterrag.tppibot.util.Logging;

public class CharacterSpam implements IReaction
{
    private Map<Character, Integer> repeated = new HashMap<Character, Integer>();

    @Override
    public synchronized void onMessage(MessageEvent<?> event)
    {
        int symbolCount = 0;

        String msg = event.getMessage();

        if (msg.length() < 12)
            return;

        for (char c : msg.toCharArray())
        {
            if (!((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || c == ' '))
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
                timeout(event, 5);
                finish();
                return;
            }
        }

        if (symbolCount > msg.length() / 3)
        {
            Logging.log("too many symbols!");
            timeout(event, 5);
            finish();
            return;
        }
        
        finish();
    }

    private void finish()
    {
        repeated.clear();
    }

    private void timeout(MessageEvent<?> event, int i)
    {
        Command quiet = (Command) CommandRegistry.getCommand("timeout");
        quiet.sendMessage(event.getChannel(), event.getUser().getNick() + ", please do not spam!");
        quiet.onCommand(new MessageEvent<PircBotX>(event.getBot(), event.getChannel(), event.getBot().getUserBot(), event.getMessage()), event.getUser().getNick(), "" + 5);
    }
}
