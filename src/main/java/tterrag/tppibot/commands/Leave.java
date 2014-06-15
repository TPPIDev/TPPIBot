package tterrag.tppibot.commands;

import org.pircbotx.hooks.events.MessageEvent;

public class Leave extends Command
{
    public Leave()
    {
        super("leave", PermLevel.TRUSTED);
    }
    
    @Override
    public boolean onCommand(MessageEvent<?> event, String... args)
    {
        event.getChannel().send().part("Why do you hate me :( ");
        return true;
    }
}
