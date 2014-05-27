package tterrag.tppibot.commands;

import org.apache.commons.lang3.StringUtils;
import org.pircbotx.Channel;
import org.pircbotx.User;

import tterrag.tppibot.runnables.ReminderProcess;

public class AddReminder extends Command
{
    public AddReminder()
    {
        super("addremind", PermLevel.OP);
    }

    @Override
    public boolean onCommand(Channel channel, User user, String... args)
    {
        if (args.length > 0)
        {
            String reminder = StringUtils.join(args, ' ');
            ReminderProcess.addReminder("[Reminder] " + reminder);
            sendNotice(user, "Reminder added: " + reminder);
            return true;
        }
        sendNotice(user, "This requires a string argument!");
        return false;
    }
}
