package tterrag.tppibot;

import java.nio.charset.Charset;

import org.slf4j.impl.SimpleLogger;
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
import tterrag.tppibot.listeners.MessageListener;
import tterrag.tppibot.runnables.ReminderProcess;

public class Main
{
    private static PircBotX bot;

    private static CommandRegistry commands;

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
        commands.registerCommand(new DisableRemind());

        Configuration.Builder<PircBotX> builder = new Configuration.Builder<PircBotX>();
        System.out.println("Building config");
        builder.setName("TPPIBot");
        builder.setLogin("TPPIBot");
        builder.setNickservPassword(args[0]);
        builder.setEncoding(Charset.isSupported("UTF-8") ? Charset.forName("UTF-8") : Charset.defaultCharset());
        builder.addAutoJoinChannel(args[1].startsWith("#") ? args[1] : "#" + args[1]);
        builder.setServer("irc.esper.net", 6667);
        builder.getListenerManager().addListener(new MessageListener());

        PircBotX bot = new PircBotX(builder.buildConfiguration());
        System.out.println("Built config");

        try
        {
            System.out.println("Connecting to Server!");
            bot.startBot();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

        reminders = new ReminderProcess(bot,

        "[Reminder] You can open the chat and press tab to talk with us!", "[Reminder] Rules: Avoid swearing - No ETA requests - No modlist requests - Don't advertise - Use common sense.");

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
