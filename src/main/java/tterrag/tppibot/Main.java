package tterrag.tppibot;

import java.nio.charset.Charset;

import org.apache.commons.lang3.ArrayUtils;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.slf4j.impl.SimpleLogger;

import tterrag.tppibot.commands.AddCommand;
import tterrag.tppibot.commands.AddReminder;
import tterrag.tppibot.commands.Commands;
import tterrag.tppibot.commands.EditCommand;
import tterrag.tppibot.commands.Help;
import tterrag.tppibot.commands.Join;
import tterrag.tppibot.commands.Kill;
import tterrag.tppibot.commands.RemindersOff;
import tterrag.tppibot.commands.RemindersOn;
import tterrag.tppibot.commands.RemoveCommand;
import tterrag.tppibot.commands.Timeout;
import tterrag.tppibot.commands.Topic;
import tterrag.tppibot.listeners.ExitListener;
import tterrag.tppibot.listeners.JoinListener;
import tterrag.tppibot.listeners.MessageListener;
import tterrag.tppibot.registry.CommandRegistry;
import tterrag.tppibot.registry.ExitRecieverRegistry;
import tterrag.tppibot.runnables.ReminderProcess;
import tterrag.tppibot.runnables.TimeoutChecker;

public class Main
{
    public static ReminderProcess reminders;
    public static TimeoutChecker timeouts;

    public static void main(String[] args)
    {
        System.setProperty(SimpleLogger.SHOW_DATE_TIME_KEY, "true");
        System.setProperty(SimpleLogger.DATE_TIME_FORMAT_KEY, "[MM/dd HH:mm:ss]");
        System.setProperty(SimpleLogger.SHOW_THREAD_NAME_KEY, "false");
        System.setProperty(SimpleLogger.LEVEL_IN_BRACKETS_KEY, "true");
        System.setProperty(SimpleLogger.SHOW_LOG_NAME_KEY, "false");
        System.out.println("Starting");

        
        CommandRegistry.registerCommand(new Help());
        CommandRegistry.registerCommand(new Kill());
        CommandRegistry.registerCommand(new Join());
        CommandRegistry.registerCommand(new EditCommand());
        CommandRegistry.registerCommand(new AddReminder());
        CommandRegistry.registerCommand(new RemindersOff());
        CommandRegistry.registerCommand(new RemindersOn());
        CommandRegistry.registerCommand(new Topic());
        CommandRegistry.registerCommand(new Commands());
        
        AddCommand addcmd = new AddCommand();
        CommandRegistry.registerCommand(addcmd);
        ExitRecieverRegistry.registerReceiver(addcmd);
        
        CommandRegistry.registerCommand(new RemoveCommand());
        
        Timeout timeout = new Timeout();
        CommandRegistry.registerCommand(timeout);
        ExitRecieverRegistry.registerReceiver(timeout);

        // DISABLED UNTIL FURTHER NOTICE reactions.registerReaction(new Cursewords());
        
        Configuration.Builder<PircBotX> builder = new Configuration.Builder<PircBotX>();
        System.out.println("Building config");
        builder.setName("TPPIBot");
        builder.setLogin("TPPIBot");
        builder.setNickservPassword(args[0]);
        builder.setEncoding(Charset.isSupported("UTF-8") ? Charset.forName("UTF-8") : Charset.defaultCharset());
        builder.setServer("irc.esper.net", 6667);
        
        args = ArrayUtils.remove(args, 0);
        
        for (String s : args)
        {
            builder.addAutoJoinChannel(s.startsWith("#") ? s : "#" + s);
        }
        
        builder.getListenerManager().addListener(new MessageListener());
        builder.getListenerManager().addListener(new JoinListener());
        builder.getListenerManager().addListener(new ExitListener());

        PircBotX bot = new PircBotX(builder.buildConfiguration());
        System.out.println("Built config");

        reminders = new ReminderProcess(bot,

        "[Reminder] You can open the chat and press tab to talk with us!", "[Reminder] Rules: Avoid swearing - No ETA requests - No modlist requests - Don't advertise - Use common sense.");

        Thread reminderThread = new Thread(reminders);
        ExitRecieverRegistry.registerReceiver(reminders);
        reminderThread.start();
        
        timeouts = new TimeoutChecker(timeout);
        Thread timeoutThread = new Thread(timeouts);
        timeoutThread.start();
        
        try
        {
            System.out.println("Connecting to Server!");
            bot.startBot();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
