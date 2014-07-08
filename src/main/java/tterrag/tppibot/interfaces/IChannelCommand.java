package tterrag.tppibot.interfaces;

import java.util.List;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

public interface IChannelCommand extends ICommand
{
    public void onCommand(PircBotX bot, User user, Channel channel, List<String> lines, String... args);
    
}
