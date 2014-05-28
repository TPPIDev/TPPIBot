package tterrag.tppibot.commands;

import org.pircbotx.hooks.events.MessageEvent;

public class Join extends Command
{
    public Join()
    {
        super("join", PermLevel.OP);
    }

    @Override
    public boolean onCommand(MessageEvent<?> event, String... args)
    {
        if (args.length > 0)
        {
            for (String s : args)
            {
                event.getBot().sendIRC().joinChannel(s.startsWith("#") ? s : "#" + s);
            }
            return true;
        }
        else
        {
            sendNotice(event.getUser(), "Must supply at least one channel!");
            return false;
        }
    }
    
    @Override
    public String getDesc()
    {
        return "Joins a channel.";
    }
}
