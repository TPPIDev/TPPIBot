package tterrag.tppibot.listeners;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.QuitEvent;

import tterrag.tppibot.registry.ExitRecieverRegistry;

public class ExitListener extends ListenerAdapter<PircBotX>
{
    @Override
    public void onQuit(QuitEvent<PircBotX> event) throws Exception
    {
        if (event.getUser().getNick().equals(event.getBot().getNick()))
        {
            ExitRecieverRegistry.processClassesOnExit();
        }
    }
}
