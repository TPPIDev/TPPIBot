package tterrag.tppibot.commands;

import java.util.List;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

import tterrag.tppibot.runnables.MessageSender;

public class Topic extends Command
{
    public Topic()
    {
        super("topic", PermLevel.DEFAULT);
    }

    @Override
    public void onCommand(PircBotX bot, User user, Channel channel, List<String> lines, String... args)
    {
        MessageSender.INSTANCE.enqueue(bot, channel.getName(), channel.getTopic());
    }

    @Override
    public String getDesc()
    {
        return "Prints the topic of the current channel.";
    }

    @Override
    public boolean executeWithoutChannel()
    {
        return false;
    }
}
