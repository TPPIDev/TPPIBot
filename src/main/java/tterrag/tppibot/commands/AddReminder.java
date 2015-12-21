package tterrag.tppibot.commands;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

import tterrag.tppibot.Main;

public class AddReminder extends Command {

    public AddReminder() {
        super("addremind", PermLevel.TRUSTED);
    }

    @Override
    public void onCommand(PircBotX bot, User user, Channel channel, List<String> lines, String... args) {
        if (args.length > 0) {
            String reminder = StringUtils.join(args, ' ');
            Main.reminders.addReminder("[Reminder] " + reminder);
            lines.add("Reminder added: " + reminder);
        } else {
            lines.add("This requires a string argument!");
        }
    }

    @Override
    public String getDesc() {
        return "Adds a reminder to the queue, automatically prepends [Reminder]";
    }
}
