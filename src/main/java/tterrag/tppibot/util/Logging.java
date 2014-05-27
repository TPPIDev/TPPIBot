package tterrag.tppibot.util;

import java.util.logging.Logger;

public class Logging
{
    private static final Logger logger = Logger.getLogger("TPPIBot");
    
    public static void log(String message)
    {
        logger.info(message);
    }
    
    public static void severe(String message)
    {
        logger.severe(message);
    }
}
