package tterrag.tppibot;

import java.util.HashMap;
import java.util.Map;

import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.Queue;
import org.jibble.pircbot.User;

import tterrag.tppibot.runnables.ReminderProcess;

public class TPPIBot extends PircBot
{

    private Map<String, Boolean> reminderMap;
    private Queue reminders;

    public TPPIBot()
    {
        this.setName("TPPIBot");

        reminderMap = new HashMap<String, Boolean>();

        reminders = new Queue();
        reminders
                .add("[Reminder] You can open the chat and press tab to talk with us!");
        reminders
                .add("[Reminder] Rules: Avoid swearing - No ETA requests - No modlist requests - Don't advertise - Use common sense.");

        runThreads();
    }

    private void runThreads()
    {
        Thread reminderThread = new Thread(new ReminderProcess(this));
        reminderThread.start();
    }

    @Override
    protected void onMessage(String channel, String sender, String login,
            String hostname, String message)
    {
        if (message.startsWith("`"))
        {
            message = pruneMessage(message);
            if (userIsOp(channel, sender))
            {
                if (message.startsWith("test"))
                {
                    this.sendMessage(channel, "testing!");
                }
            }
        }
    }

    private boolean userIsOp(String channel, String user)
    {
        for (User u : this.getUsers(channel))
        {
            if (u.equals(user) && u.isOp())
                return true;
        }
        return false;
    }

    private String pruneMessage(String message)
    {
        return message.substring(1);
    }

    public static void main(String[] args)
    {
        TPPIBot bot = new TPPIBot();

        bot.setVerbose(true);

        try
        {
            bot.connect("irc.esper.net");
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            System.exit(0);
        }

        bot.join("#PlayTPPI");
    }

    private void join(String channel)
    {
        this.joinChannel(channel);
        this.reminderMap.put(channel, true);
    }

    public void remind(String channel)
    {
        String remind = (String) reminders.next();
        this.sendMessage(channel, remind);
        reminders.add(remind);
    }

    public synchronized boolean areRemindersEnabledFor(String channel)
    {
        if (reminderMap == null)
            return false;
        else
            return reminderMap.get(channel);
    }
}
