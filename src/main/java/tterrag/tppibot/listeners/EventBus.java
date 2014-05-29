package tterrag.tppibot.listeners;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.ListenerAdapter;

import tterrag.tppibot.registry.EventHandler;

public class EventBus extends ListenerAdapter<PircBotX>
{
    @Override
    public void onEvent(Event<PircBotX> event) throws Exception
    {
        EventHandler.post(event);
    }
}
