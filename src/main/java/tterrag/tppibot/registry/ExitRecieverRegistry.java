package tterrag.tppibot.registry;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import tterrag.tppibot.annotations.ReceiveExitEvent;
import tterrag.tppibot.util.Logging;

public class ExitRecieverRegistry
{
    private static List<Object> registered = new ArrayList<Object>();
    
    public static void registerReceiver(Object o)
    {
        registered.add(o);
    }
    
    public static void processClassesOnExit()
    {
        for (Object o : registered)
        {
            for (Method m : o.getClass().getDeclaredMethods())
            {
                if (m.getAnnotation(ReceiveExitEvent.class) != null)
                {
                    try
                    {
                        m.invoke(o, new Object[]{});
                    }
                    catch (Throwable t)
                    {
                        t.printStackTrace();
                        Logging.error("Could not invoke method " + m.getName() + " this could have serious side-effects!");
                    }
                }
            }
        }
    }
}
