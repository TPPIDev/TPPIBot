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
import tterrag.tppibot.commands.EditPerms;
import tterrag.tppibot.commands.Forgive;
import tterrag.tppibot.commands.Help;
import tterrag.tppibot.commands.Join;
import tterrag.tppibot.commands.Kill;
import tterrag.tppibot.commands.Leave;
import tterrag.tppibot.commands.RemindersOff;
import tterrag.tppibot.commands.RemindersOn;
import tterrag.tppibot.commands.RemoveCommand;
import tterrag.tppibot.commands.Timeout;
import tterrag.tppibot.commands.ToggleSpamFilters;
import tterrag.tppibot.commands.Topic;
import tterrag.tppibot.commands.Victim;
import tterrag.tppibot.listeners.EventBus;
import tterrag.tppibot.listeners.JoinListener;
import tterrag.tppibot.listeners.MessageListener;
import tterrag.tppibot.reactions.CharacterSpam;
import tterrag.tppibot.registry.EventHandler;
import tterrag.tppibot.registry.PermRegistry;
import tterrag.tppibot.registry.ReactionRegistry;
import tterrag.tppibot.runnables.ConsoleCommands;
import tterrag.tppibot.runnables.ReminderProcess;
import tterrag.tppibot.runnables.TimeoutChecker;
import static tterrag.tppibot.util.Logging.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Main
{
    public static ReminderProcess reminders;
    public static TimeoutChecker timeouts;

    public static CharacterSpam spamFilter;
    
    public static PircBotX bot;
    
    public static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void main(String[] args)
    {
        log("Starting...");
        System.setProperty(SimpleLogger.SHOW_DATE_TIME_KEY, "true");
        System.setProperty(SimpleLogger.DATE_TIME_FORMAT_KEY, "[MM/dd HH:mm:ss]");
        System.setProperty(SimpleLogger.SHOW_THREAD_NAME_KEY, "false");
        System.setProperty(SimpleLogger.LEVEL_IN_BRACKETS_KEY, "true");
        System.setProperty(SimpleLogger.SHOW_LOG_NAME_KEY, "false");

        // create base commands
        log("Creating commands...");
        new Help();
        new Kill();
        new Join();
        new EditCommand();
        new AddReminder();
        new RemindersOff();
        new RemindersOn();
        new Topic();
        new Commands();
        new AddCommand();
        new RemoveCommand();
        new Victim();
        new EditPerms();
        new Leave();
        new ToggleSpamFilters();
        new Forgive();

        Timeout timeout = new Timeout();
        log("Commands created.");

        log("Creating reactions...");
        spamFilter = new CharacterSpam();
        ReactionRegistry.registerReaction(spamFilter);
        log("Reactions created.");

        log("Configuring bot...");
        Configuration.Builder<PircBotX> builder = new Configuration.Builder<PircBotX>();
        builder.setName("TPPIBot");
        builder.setLogin("TPPIBot");
        builder.setNickservPassword(args[0]);
        builder.setEncoding(Charset.isSupported("UTF-8") ? Charset.forName("UTF-8") : Charset.defaultCharset());
        builder.setServer("irc.esper.net", 6667);
        builder.setAutoReconnect(true);

        args = ArrayUtils.remove(args, 0);

        for (String s : args)
        {
            builder.addAutoJoinChannel(s.startsWith("#") ? s : "#" + s);
        }

        builder.getListenerManager().addListener(new MessageListener());
        builder.getListenerManager().addListener(new JoinListener());
        builder.getListenerManager().addListener(new EventBus());

        bot = new PircBotX(builder.buildConfiguration());
        log("Configured.");

        // create and start threads
        log("Creating threads...");
        reminders = new ReminderProcess(bot,

        "[Reminder] You can open the chat and press tab to talk with us!", "[Reminder] Rules: Avoid swearing - No ETA requests - No modlist requests - Don't advertise - Use common sense.");

        Thread reminderThread = new Thread(reminders);
        reminderThread.start();

        timeouts = new TimeoutChecker(timeout);
        Thread timeoutThread = new Thread(timeouts);
        timeoutThread.start();
        
        Thread consoleThread = new Thread(new ConsoleCommands());
        consoleThread.start();
        log("Threads created...");
        
        log("Registering extra event receivers...");
        EventHandler.registerReceiver(reminders);
        EventHandler.registerReceiver(PermRegistry.instance());
        EventHandler.registerReceiver(spamFilter);
        log("Registered extra event reveivers.");
        
        // start 'er up
        try
        {
            log("Connecting bot...");
            bot.startBot();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
