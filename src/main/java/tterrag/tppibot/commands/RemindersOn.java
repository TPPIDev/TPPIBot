package tterrag.tppibot.commands;

import java.util.List;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

import tterrag.tppibot.Main;

public class RemindersOn extends Command
{
    public RemindersOn()
    {
        super("reminderson", PermLevel.TRUSTED);
    }

    @Override
    public void onCommand(PircBotX bot, User user, Channel channel, List<String> lines, String... args)
    {
        if (!Main.reminders.isRemindEnabledFor(channel.getName()))
        {
            lines.add("Enabling reminders for channel \"" + channel.getName() + "\"");
            Main.reminders.enableRemindersFor(channel.getName());
        }
        else
        {
            lines.add("Reminders already enabled!");
        }
    }

    @Override
    public String getDesc()
    {
        return "Turns reminders on for the current channel";
    }

    @Override
    public boolean executeWithoutChannel()
    {
        return false;
    }
}
