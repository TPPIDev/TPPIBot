package tterrag.tppibot.commands;

import org.apache.commons.lang3.StringUtils;
import org.pircbotx.hooks.events.MessageEvent;

import tterrag.tppibot.Main;

public class AddReminder extends Command
{
    public AddReminder()
    {
        super("addremind", PermLevel.OP);
    }

    @Override
    public boolean onCommand(MessageEvent<?> event, String... args)
    {
        if (args.length > 0)
        {
            String reminder = StringUtils.join(args, ' ');
            Main.reminders.addReminder("[Reminder] " + reminder);
            sendNotice(event.getUser(), "Reminder added: " + reminder);
            return true;
        }
        sendNotice(event.getUser(), "This requires a string argument!");
        return false;
    }
    
    @Override
    public String getDesc()
    {
        return "Adds a reminder to the queue, automatically prepends [Reminder]";
    }
}
