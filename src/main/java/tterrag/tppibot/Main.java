package tterrag.tppibot;

import tterrag.tppibot.commands.AddCommand;
import tterrag.tppibot.commands.AddReminder;
import tterrag.tppibot.commands.DisableRemind;
import tterrag.tppibot.commands.EditCommand;
import tterrag.tppibot.commands.Help;
import tterrag.tppibot.commands.Join;
import tterrag.tppibot.commands.Kill;

public class Main
{
    private static TPPIBot bot;
    
    public static void main(String[] args)
    {
        bot = new TPPIBot();

        bot.setVerbose(true);

        bot.registerCommand(new Help());
        bot.registerCommand(new Kill());
        bot.registerCommand(new Join());
        bot.registerCommand(new AddCommand());
        bot.registerCommand(new EditCommand());
        bot.registerCommand(new AddReminder());
        bot.registerCommand(new DisableRemind());
        
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
            bot.join("#PlayTPPI");
        }
        else
        {
            for (String s : args)
            {
                bot.join(s);
            }
        }
    }
    
    public static TPPIBot getBot()
    {
        return bot;
    }
}
