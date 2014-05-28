package tterrag.tppibot.config;

import java.io.File;
import java.io.IOException;

public class Config
{
    private File configFile;
    
    public Config(String filename)
    {
        this.configFile = new File("src/main/resources/" + filename);
        
        if (!configFile.exists())
        {
            try
            {
                configFile.createNewFile();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
