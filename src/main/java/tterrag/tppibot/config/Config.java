package tterrag.tppibot.config;

import java.io.File;
import java.io.IOException;

import tterrag.tppibot.Main;
import tterrag.tppibot.util.SaveUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Config
{
    private File configFile;
    public static final File baseDir;

    public static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    static
    {
        baseDir = new File(Main.overrideFile == null ? System.getProperty("user.home") + "/.tppibot" : Main.overrideFile);

        if (!baseDir.exists())
        {
            baseDir.mkdirs();
        }
    }

    public Config(String filename)
    {
        this.configFile = new File(baseDir.getAbsolutePath() + "/" + filename);

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

    public void writeInt(int n)
    {
        SaveUtils.saveAllToFile(configFile, "" + n);
    }
}
