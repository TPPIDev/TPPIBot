package tterrag.tppibot.commands;

import org.pircbotx.hooks.events.MessageEvent;

public class Topic extends Command
{
    private long delayTime;

    public Topic()
    {
        super("topic", PermLevel.ANY);
        delayTime = 0;
    }

    @Override
    public boolean onCommand(MessageEvent<?> event, String... args)
    {
        if (delayTime == 0 || System.currentTimeMillis() - delayTime > 60000)
        {
            sendMessage(event.getChannel(), event.getChannel().getTopic());
            delayTime = System.currentTimeMillis();
            return true;
        }
        sendNotice(event.getUser(), "Please do not spam this command!");
        return false;
    }

    @Override
    public String getDesc()
    {
        return "Prints the topic of the current channel.";
    }
}
