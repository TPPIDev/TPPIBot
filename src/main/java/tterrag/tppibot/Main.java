package tterrag.tppibot;

import tterrag.tppibot.commands.AddCommand;
import tterrag.tppibot.commands.Help;
import tterrag.tppibot.commands.Join;
import tterrag.tppibot.commands.Kill;

public class Main
{
    public static TPPIBot bot;
    
    public static void main(String[] args)
    {
        bot = new TPPIBot();

        bot.setVerbose(true);

        bot.registerCommand(new Help());
        bot.registerCommand(new Kill());
        bot.registerCommand(new Join());
        bot.registerCommand(new AddCommand());
        
        try
        {
            bot.connect("irc.esper.net");
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            System.exit(0);
        }

        if (args.length < 1)
        {
            bot.join("#TestPackPleaseIgnore");
        }
        else
        {
            for (String s : args)
            {
                bot.join(s);
            }
        }
    }
}
