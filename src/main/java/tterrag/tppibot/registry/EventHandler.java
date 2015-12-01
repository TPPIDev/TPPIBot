package tterrag.tppibot.registry;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.events.MessageEvent;

import tterrag.tppibot.annotations.Subscribe;
import tterrag.tppibot.util.Logging;

/**
 * Register classes with this class and have a method (or more) inside
 * (non-static) that have the {@link @Subscribe} annotation.
 * <p>
 * Classes that use this should not do so for very common events such as
 * {@link MessageEvent} due to reflection overhead
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public enum EventHandler
{
    INSTANCE;
    
    private List<ReceiverMethod> registrar = new ArrayList<ReceiverMethod>();

    private static class ReceiverMethod
    {
        public final Object instance;
        public final Method method;
        public final Class<? extends Event> eventType;

        public ReceiverMethod(Object instance, Method method, Class<? extends Event> eventType)
        {
            this.instance = instance;
            this.method = method;
            this.eventType = eventType;
        }
    }

    public void registerReceiver(Object o)
    {
        for (Method m : o.getClass().getDeclaredMethods())
        {
            Subscribe annotation = m.getAnnotation(Subscribe.class);
            if (annotation != null)
            {
                Class<?>[] params = m.getParameterTypes();
                if (params.length != 1 || !Event.class.isAssignableFrom(params[0])) { throw new IllegalArgumentException(
                        "A method that is @Subscribe must take ONE parameter of type Event or subclass"); }
                registerReceiver(o, m, (Class<? extends Event>) params[0]);
            }
        }
    }

    private void registerReceiver(Object o, Method m, Class<? extends Event> clazz)
    {
        registrar.add(new ReceiverMethod(o, m, clazz));
    }

    public void post(Event<PircBotX> event)
    {
        for (ReceiverMethod r : registrar)
        {
            if (r.eventType.isAssignableFrom(event.getClass()))
            {
                try
                {
                    r.method.invoke(r.instance, event);
                    Logging.debug("Successfully posted event " + event.getClass().getSimpleName() + " to class " + r.instance.getClass().getSimpleName());
                }
                catch (Throwable t)
                {
                    t.printStackTrace();
                }
            }
        }
    }
}
