package tterrag.tppibot.commands;

import java.util.List;

import org.pircbotx.PircBotX;
import org.pircbotx.User;

import tterrag.tppibot.Main;
import tterrag.tppibot.util.ThreadUtils;

public class Kill extends Command
{
    public Kill()
    {
        super("kill", PermLevel.CONTROLLER);
    }

    @Override
    public void onCommand(PircBotX bot, User user, List<String> lines, String... args)
    {                
        killBot(bot);
    }
    
    public static boolean killBot(PircBotX bot)
    {
        if (bot.isConnected())
        {
            bot.stopBotReconnect();
            bot.sendIRC().quitServer("x.x");
        }
        
        ThreadUtils.sleep(2000);

        System.exit(0);

        return true;
    }

    @Override
    public String getDesc()
    {
        return "I am kill. No.";
    }
    
    @Override
    public boolean handleConsoleCommand(String... args)
    {
        killBot(Main.bot);
        return true;
    }
}
