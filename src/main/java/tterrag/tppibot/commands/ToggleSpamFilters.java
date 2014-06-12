package tterrag.tppibot.commands;

import org.pircbotx.hooks.events.MessageEvent;

import tterrag.tppibot.reactions.CharacterSpam;

public class ToggleSpamFilters extends Command
{
    public ToggleSpamFilters()
    {
        super("toggleFilter", PermLevel.OP);
    }
    
    @Override
    public boolean onCommand(MessageEvent<?> event, String... args)
    {
        if (!CharacterSpam.toggleBlacklistChannel(event.getChannel().getName()))
        {
            sendMessage(event.getChannel(), "Spam filter enabled on channel " + event.getChannel().getName() + ".");
        }
        else
        {
            sendMessage(event.getChannel(), "Spam filter disabled on channel " + event.getChannel().getName() + ".");
        }
        return true;
    }
}
