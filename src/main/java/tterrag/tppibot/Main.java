package tterrag.tppibot;

import static tterrag.tppibot.util.Logging.*;

import java.nio.charset.Charset;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.slf4j.impl.SimpleLogger;

import tterrag.tppibot.commands.*;
import tterrag.tppibot.listeners.EventBus;
import tterrag.tppibot.listeners.JoinListener;
import tterrag.tppibot.listeners.MessageListener;
import tterrag.tppibot.listeners.PrivateMessageListener;
import tterrag.tppibot.reactions.BannedWords;
import tterrag.tppibot.reactions.CharacterSpam;
import tterrag.tppibot.reactions.FloodSpam;
import tterrag.tppibot.registry.EventHandler;
import tterrag.tppibot.registry.PermRegistry;
import tterrag.tppibot.registry.ReactionRegistry;
import tterrag.tppibot.runnables.ConsoleCommands;
import tterrag.tppibot.runnables.MessageSender;
import tterrag.tppibot.runnables.ReminderProcess;
import tterrag.tppibot.runnables.TimeoutChecker;
import tterrag.tppibot.util.IRCUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Main
{
    public static Reminders reminderCommand;
    
    public static ReminderProcess reminders;
    public static TimeoutChecker timeouts;

    public static CharacterSpam spamFilter;
    public static FloodSpam floodFilter;
    public static BannedWords bannedWords;

    public static PircBotX bot;

    public static String overrideFile;

    
    public static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void main(String[] args) throws ParseException
    {
        log("Creating command line options...");
        Options options = new Options();
        
        options.addOption("n", "name", true, "The nick/login of the bot");
        options.addOption("d", "dataDir", true, "The directory in which to create the .tppibot folder");
        options.addOption("p", "password", true, "Nickserv password for the bot");
        
        @SuppressWarnings("static-access")
        Option channels = OptionBuilder.withArgName("channel").hasArgs().withDescription("The channels to join on startup").create("channels");
        options.addOption(channels);
        
        CommandLineParser parser = new BasicParser();
        CommandLine cmd = parser.parse(options, args);
        log("Command line options created.");
        
        overrideFile = cmd.getOptionValue("dataDir");
        
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
        reminderCommand = new Reminders();
        new Topic();
        new Commands();
        new AddCommand();
        new RemoveCommand();
        new Perms();
        new Leave();
        new ToggleSpamFilters();
        new Forgive();
        new Recover();
        new Mode();
        new HTML();
        new Shortener();
        new Say();
        new Drama();
        new BanWord();
        new Nick();

        Timeout timeout = new Timeout();
        log("Commands created.");

        log("Creating reactions...");
        spamFilter = new CharacterSpam();
        ReactionRegistry.registerReaction(spamFilter);
        floodFilter = new FloodSpam();
        ReactionRegistry.registerReaction(floodFilter);
        bannedWords = new BannedWords();
        ReactionRegistry.registerReaction(bannedWords);
        log("Reactions created.");
        
        log("Configuring bot...");        
        Configuration.Builder<PircBotX> builder = new Configuration.Builder<PircBotX>();
        builder.setName(cmd.getOptionValue("name"));
        builder.setLogin(cmd.getOptionValue("name"));
        builder.setNickservPassword(cmd.getOptionValue("password"));
        builder.setEncoding(Charset.isSupported("UTF-8") ? Charset.forName("UTF-8") : Charset.defaultCharset());
        builder.setServer("irc.esper.net", 6668);
        builder.setAutoReconnect(true);

        for (String s : cmd.getOptionValues("channels"))
        {
            builder.addAutoJoinChannel(IRCUtils.fmtChan(s));
        }

        builder.getListenerManager().addListener(MessageListener.instance);
        builder.getListenerManager().addListener(new JoinListener());
        builder.getListenerManager().addListener(new EventBus());
        builder.getListenerManager().addListener(new PrivateMessageListener());

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
        
        Thread messageSenderThread = new Thread(MessageSender.instance);
        messageSenderThread.start();
        
        log("Threads created...");
        
        log("Registering extra event receivers...");
        EventHandler.registerReceiver(reminders);
        EventHandler.registerReceiver(PermRegistry.instance());
        EventHandler.registerReceiver(spamFilter);
        EventHandler.registerReceiver(bannedWords);
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
