package tterrag.tppibot.commands;

import org.pircbotx.hooks.events.MessageEvent;

import tterrag.tppibot.Main;

public class RemindersOff extends Command
{
    public RemindersOff()
    {
        super("remindersoff", PermLevel.TRUSTED);
    }

    @Override
    public boolean onCommand(MessageEvent<?> event, String... args)
    {
        if (Main.reminders.isRemindEnabledFor(event.getChannel().getName()))
        {
            sendNotice(event.getUser(), "Disabling reminders for channel \"" + event.getChannel().getName() + "\"");
            Main.reminders.disableRemindersFor(event.getChannel().getName());
            return true;
        }
        else
        {
            sendNotice(event.getUser(), "Reminders already disabled!");
            return false;
        }
    }

    @Override
    public String getDesc()
    {
        return "Turns reminders off for the current channel";
    }
}
