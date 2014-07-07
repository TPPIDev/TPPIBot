package tterrag.tppibot.commands;

import java.util.List;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

import tterrag.tppibot.interfaces.IChannelCommand;
import tterrag.tppibot.reactions.CharacterSpam;

public class ToggleSpamFilters extends Command implements IChannelCommand
{
    public ToggleSpamFilters()
    {
        super("toggleFilter", PermLevel.TRUSTED);
    }
    
    @Override
    public void onCommand(PircBotX bot, User user, Channel channel, List<String> lines, String... args)
    {
        if (CharacterSpam.toggleBlacklistChannel(channel.getName()))
        {
            lines.add("Spam filter disabled on channel " + channel.getName() + ".");
        }
        else
        {            
            lines.add("Spam filter enabled on channel " + channel.getName() + ".");
        }
    }

    @Override
    public boolean canChannelBeNull()
    {
        return false;
    }
}
