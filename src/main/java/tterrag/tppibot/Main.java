package tterrag.tppibot;

import java.nio.charset.Charset;

import org.apache.commons.lang3.ArrayUtils;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;

import tterrag.tppibot.commands.AddCommand;
import tterrag.tppibot.commands.AddReminder;
import tterrag.tppibot.commands.CommandRegistry;
import tterrag.tppibot.commands.DisableRemind;
import tterrag.tppibot.commands.EditCommand;
import tterrag.tppibot.commands.Help;
import tterrag.tppibot.commands.Join;
import tterrag.tppibot.commands.Kill;
import tterrag.tppibot.runnables.ReminderProcess;

public class Main
{
    private static PircBotX bot;
    
    private static CommandRegistry commands;
    
    public static ReminderProcess reminders;
    
    public static void main(String[] args)
    {
        commands = new CommandRegistry();
                
        commands.registerCommand(new Help());
        commands.registerCommand(new Kill());
        commands.registerCommand(new Join());
        commands.registerCommand(new AddCommand());
        commands.registerCommand(new EditCommand());
        commands.registerCommand(new AddReminder());
        commands.registerCommand(new DisableRemind());
        
        if (args.length < 1)
        {
            System.out.println("Not enough args.");
            System.exit(0);
        }
        
        Configuration.Builder<PircBotX> builder = new Configuration.Builder<PircBotX>();
        builder.setName("TPPIBot");
        builder.setLogin("TPPIBot");
        builder.setEncoding(Charset.isSupported("UTF-8") ? Charset.forName("UTF-8") : Charset.defaultCharset());
        builder.setNickservPassword(args[0]);
        builder.setServer("irc.esper.net", 6667);
        
        bot = new PircBotX(builder.buildConfiguration());

        args = ArrayUtils.remove(args, 0);
        
        try
        {
            bot.startBot();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        
        if (args.length < 1)
        {
            bot.sendIRC().joinChannel("#PlayTPPI");
        }
        else
        {
            for (String s : args)
            {
                bot.sendIRC().joinChannel(s);
            }
        }
        
        reminders = new ReminderProcess(bot,
                
                "[Reminder] You can open the chat and press tab to talk with us!",
                "[Reminder] Rules: Avoid swearing - No ETA requests - No modlist requests - Don't advertise - Use common sense."
        );
        
        Thread reminderThread = new Thread(reminders);
        reminderThread.start();
    }
    
    public static PircBotX getBot()
    {
        return bot;
    }
    
    public static CommandRegistry getCommandRegistry()
    {
        return commands;
    }
}
