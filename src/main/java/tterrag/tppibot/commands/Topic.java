package tterrag.tppibot.commands;

import java.util.List;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

import tterrag.tppibot.interfaces.IChannelCommand;

public class Topic extends Command implements IChannelCommand
{
    private long delayTime;

    public Topic()
    {
        super("topic", PermLevel.DEFAULT);
        delayTime = 0;
    }

    @Override
    public void onCommand(PircBotX bot, User user, Channel channel, List<String> lines, String... args)
    {
        if (delayTime == 0 || System.currentTimeMillis() - delayTime > 60000)
        {
            lines.add(channel.getTopic());
            delayTime = System.currentTimeMillis();
        }
        else
        {
            user.send().notice("Please do not spam this command!");
        }
    }

    @Override
    public String getDesc()
    {
        return "Prints the topic of the current channel.";
    }

    @Override
    public boolean canChannelBeNull()
    {
        return false;
    }
}
