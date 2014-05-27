package tterrag.tppibot.commands;

import org.pircbotx.Channel;
import org.pircbotx.User;


public class DisableRemind extends Command
{
    public DisableRemind()
    {
        super("remindersoff", PermLevel.OP);
    }

    @Override
    public boolean onCommand(Channel channel, User user, String... args)
    {
//        if (Main.getBot().reminders.isRemindEnabledFor(channel))
//        {
//            sendNotice(user, "Disabling reminders for channel \"" + channel + "\"");
//            Main.getBot().reminders.disableRemindersFor(channel);
//            return true;
//        }
//        else
//        {
//            sendNotice(user, "Reminders already disabled!");
            return false;
//        }
    }
}
