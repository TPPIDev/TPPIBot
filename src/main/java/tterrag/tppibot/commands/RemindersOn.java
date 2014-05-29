package tterrag.tppibot.commands;

import org.pircbotx.hooks.events.MessageEvent;

import tterrag.tppibot.Main;

public class RemindersOn extends Command
{
    public RemindersOn()
    {
        super("reminderson", PermLevel.CHANOP);
    }

    @Override
    public boolean onCommand(MessageEvent<?> event, String... args)
    {
        if (!Main.reminders.isRemindEnabledFor(event.getChannel().getName()))
        {
            sendNotice(event.getUser(), "Enabling reminders for channel \"" + event.getChannel().getName() + "\"");
            Main.reminders.enableRemindersFor(event.getChannel().getName());
            return true;
        }
        else
        {
            sendNotice(event.getUser(), "Reminders already enabled!");
            return false;
        }
    }

    @Override
    public String getDesc()
    {
        return "Turns reminders on for the current channel";
    }
}
