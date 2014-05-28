package tterrag.tppibot.listeners;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.JoinEvent;

import tterrag.tppibot.Main;

public class JoinListener extends ListenerAdapter<PircBotX>
{   
    @Override
    public void onJoin(JoinEvent<PircBotX> event) throws Exception
    {
        if (!Main.reminders.isInReminderMap(event.getChannel().getName()))
            Main.reminders.enableRemindersFor(event.getChannel().getName().toLowerCase());
    }
}
