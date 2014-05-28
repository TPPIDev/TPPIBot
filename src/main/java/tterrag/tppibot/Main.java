package tterrag.tppibot;

import java.nio.charset.Charset;

import org.apache.commons.lang3.ArrayUtils;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.slf4j.impl.SimpleLogger;

import tterrag.tppibot.commands.AddCommand;
import tterrag.tppibot.commands.AddReminder;
import tterrag.tppibot.commands.CommandRegistry;
import tterrag.tppibot.commands.EditCommand;
import tterrag.tppibot.commands.Help;
import tterrag.tppibot.commands.Join;
import tterrag.tppibot.commands.Kill;
import tterrag.tppibot.commands.RemindersOff;
import tterrag.tppibot.commands.RemindersOn;
import tterrag.tppibot.commands.Topic;
import tterrag.tppibot.listeners.JoinListener;
import tterrag.tppibot.listeners.MessageListener;
import tterrag.tppibot.reactions.ReactionRegistry;
import tterrag.tppibot.runnables.ReminderProcess;

public class Main
{
    private static PircBotX bot;

    private static CommandRegistry commands;
    private static ReactionRegistry reactions;

    public static ReminderProcess reminders;

    public static void main(String[] args)
    {
        System.setProperty(SimpleLogger.SHOW_DATE_TIME_KEY, "true");
        System.setProperty(SimpleLogger.DATE_TIME_FORMAT_KEY, "[MM/dd HH:mm:ss]");
        System.setProperty(SimpleLogger.SHOW_THREAD_NAME_KEY, "false");
        System.setProperty(SimpleLogger.LEVEL_IN_BRACKETS_KEY, "true");
        System.setProperty(SimpleLogger.SHOW_LOG_NAME_KEY, "false");
        System.out.println("Starting");

        
        commands = new CommandRegistry();

        commands.registerCommand(new Help());
        commands.registerCommand(new Kill());
        commands.registerCommand(new Join());
        commands.registerCommand(new AddCommand());
        commands.registerCommand(new EditCommand());
        commands.registerCommand(new AddReminder());
        commands.registerCommand(new RemindersOff());
        commands.registerCommand(new RemindersOn());
        commands.registerCommand(new Topic());
        
        
        reactions = new ReactionRegistry();
        
        //reactions.registerReaction(new Cursewords());
                
        
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

        PircBotX bot = new PircBotX(builder.buildConfiguration());
        System.out.println("Built config");

        reminders = new ReminderProcess(bot,

        "[Reminder] You can open the chat and press tab to talk with us!", "[Reminder] Rules: Avoid swearing - No ETA requests - No modlist requests - Don't advertise - Use common sense.");

        Thread reminderThread = new Thread(reminders);
        reminderThread.start();
        
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

    public static PircBotX getBot()
    {
        return bot;
    }

    public static CommandRegistry getCommandRegistry()
    {
        return commands;
    }

    public static ReactionRegistry getReactionRegistry()
    {
        return reactions;
    }
}
