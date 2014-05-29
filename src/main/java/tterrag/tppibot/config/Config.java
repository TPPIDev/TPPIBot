package tterrag.tppibot.config;

import java.io.File;
import java.io.IOException;

import tterrag.tppibot.util.SaveUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Config
{
    private File configFile;
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

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

    public void addJsonToFile(Object o)
    {
        String json = gson.toJson(o);

        SaveUtils.addToFile(configFile, json + "\n");
    }

    /**
     * Warning, overwrites current text if it exists
     */
    public void writeJsonToFile(Object o)
    {
        String json = gson.toJson(o);

        SaveUtils.saveAllToFile(configFile, json + "\n");
    }

    public String getText()
    {
        return SaveUtils.readTextFile(configFile);
    }
}
