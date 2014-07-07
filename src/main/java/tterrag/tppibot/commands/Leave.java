package tterrag.tppibot.commands;

import java.util.List;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

import tterrag.tppibot.interfaces.IChannelCommand;

public class Leave extends Command implements IChannelCommand
{
    public Leave()
    {
        super("leave", PermLevel.TRUSTED);
    }
    
    @Override
    public void onCommand(PircBotX bot, User user, Channel channel, List<String> lines, String... args)
    {
        channel.send().part("Why do you hate me :( ");
    }

    @Override
    public boolean canChannelBeNull()
    {
        return false;
    }
}
