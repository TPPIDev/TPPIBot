package tterrag.tppibot.registry;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.events.MessageEvent;

import tterrag.tppibot.annotations.Subscribe;

/**
 * Register classes with this class and have a method (or more) inside (non-static) that have the {@link @Subscribe} annotation.
 * <p>
 * Classes that use this should not do so for very common events such as {@link MessageEvent} due to reflection overhead
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class EventHandler
{
    private static List<ReceiverMethod> registrar = new ArrayList<ReceiverMethod>();
    
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
    
    public static void registerReceiver(Object o)
    {
        for (Method m : o.getClass().getDeclaredMethods())
        {
            Subscribe annotation = m.getAnnotation(Subscribe.class);
            if (annotation != null)
            {
                Class<?>[] params = m.getParameterTypes();
                if (params.length != 1)//FIXME || !params[0].isAssignableFrom(Event.class))
                {
                    throw new IllegalArgumentException("A method that is @Subscribe must take ONE parameter of type Event or subclass");
                }
                registerReceiver(o, m, (Class<? extends Event>) params[0]);
            }
        }
    }

    private static void registerReceiver(Object o, Method m, Class<? extends Event> clazz)
    {
        registrar.add(new ReceiverMethod(o, m, (Class<? extends Event>) clazz));
    }

    public static void post(Event<PircBotX> event)
    {
        for (ReceiverMethod r : registrar)
        {
            if (r.eventType.isAssignableFrom(event.getClass()))
            {
                try
                {
                    r.method.invoke(r.instance, event);
                }
                catch (Throwable t)
                {
                    t.printStackTrace();
                }
            }
        }
    }
}
