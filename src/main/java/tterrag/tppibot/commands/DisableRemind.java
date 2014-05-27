package tterrag.tppibot.commands;

import org.pircbotx.hooks.events.MessageEvent;

import tterrag.tppibot.Main;

public class DisableRemind extends Command
{
    public DisableRemind()
    {
        super("remindersoff", PermLevel.OP);
    }

    @Override
    public boolean onCommand(MessageEvent<?> event, String... args)
    {
        if (Main.reminders.isRemindEnabledFor(event.getChannel().getName()))
        {
            sendNotice(event.getUser(), "Disabling reminders for channel \"" + event.getChannel() + "\"");
            Main.reminders.disableRemindersFor(event.getChannel().getName());
            return true;
        }
        else
        {
            sendNotice(event.getUser(), "Reminders already disabled!");
            return false;
        }
    }
}
