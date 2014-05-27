package tterrag.tppibot.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Logging
{
    private static final Logger logger = LogManager.getLogger("TPPIBot");
    
    public static void log(String message)
    {
        logger.info(message);
    }
    
    public static void error(String message)
    {
        logger.error(message);
    }
}
