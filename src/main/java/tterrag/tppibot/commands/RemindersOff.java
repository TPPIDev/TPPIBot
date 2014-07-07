package tterrag.tppibot.commands;

import java.util.List;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

import tterrag.tppibot.Main;
import tterrag.tppibot.interfaces.IChannelCommand;

public class RemindersOff extends Command implements IChannelCommand
{
    public RemindersOff()
    {
        super("remindersoff", PermLevel.TRUSTED);
    }

    @Override
    public void onCommand(PircBotX bot, User user, Channel channel, List<String> lines, String... args)
    {
        if (Main.reminders.isRemindEnabledFor(channel.getName()))
        {
            lines.add("Disabling reminders for channel \"" + channel.getName() + "\"");
            Main.reminders.disableRemindersFor(channel.getName());
        }
        else
        {
            lines.add("Reminders already disabled!");
        }
    }

    @Override
    public String getDesc()
    {
        return "Turns reminders off for the current channel";
    }

    @Override
    public boolean canChannelBeNull()
    {
        return false;
    }
}
