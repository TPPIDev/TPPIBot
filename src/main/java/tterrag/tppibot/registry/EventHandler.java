package tterrag.tppibot.registry;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.events.MessageEvent;

import tterrag.tppibot.util.Logging;

import com.google.common.eventbus.EventBus;

/**
 * Register classes with this class and have a method (or more) inside (non-static) that have the {@link @Subscribe} annotation.
 * <p>
 * Classes that use this should not do so for very common events such as {@link MessageEvent} due to reflection overhead
 */
public enum EventHandler {
    INSTANCE;

    private final EventBus bus = new EventBus("irc-events");

    public void registerReceiver(Object o) {
        bus.register(o);
    }

    public void post(Event<PircBotX> event) {
        bus.post(event);
        Logging.debug("Successfully posted event " + event.getClass().getSimpleName());
    }
}
