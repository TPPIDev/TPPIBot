package tterrag.tppibot.commands;

import tterrag.tppibot.Main;

public class DisableRemind extends Command
{
    public DisableRemind()
    {
        super("remindersoff", PermLevel.OP);
    }

    @Override
    public boolean onCommand(String channel, String user, String... args)
    {
        if (Main.getBot().reminders.isRemindEnabledFor(channel))
        {
            sendNotice(user, "Disabling reminders for channel \"" + channel + "\"");
            Main.getBot().reminders.disableRemindersFor(channel);
            return true;
        }
        else
        {
            sendNotice(user, "Reminders already disabled!");
            return false;
        }
    }
}
