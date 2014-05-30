package tterrag.tppibot.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pircbotx.Colors;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.events.DisconnectEvent;
import org.pircbotx.hooks.events.MessageEvent;

import tterrag.tppibot.annotations.Subscribe;
import tterrag.tppibot.config.Config;
import tterrag.tppibot.util.IRCUtils;

import com.google.gson.reflect.TypeToken;

public class Timeout extends Command
{
    public class TimeoutTime
    {
        public final long init;
        private long time;

        public final String channel;
        public final String user;

        public TimeoutTime(long start, int secs, String chan, String user)
        {
            this.init = start;
            this.time = secs * 1000;
            this.channel = chan;
            this.user = user;
        }

        public boolean isTimeUp()
        {
            return System.currentTimeMillis() - init > time;
        }

        public void addTime(int secs)
        {
            this.time += secs * 1000;
        }

        public long getTime()
        {
            return time;
        }
    }

    public List<TimeoutTime> list;
    private Map<String, Integer> pastOffenders;
    private Config timeoutConfig, offendersConfig;

    public Timeout()
    {
        super("timeout", PermLevel.CHANOP);

        timeoutConfig = new Config("timeouts.json");
        offendersConfig = new Config("pastOffenders.json");

        list = gson.fromJson(timeoutConfig.getText(), new TypeToken<List<TimeoutTime>>() {}.getType());

        if (list == null)
            list = new ArrayList<TimeoutTime>();

        pastOffenders = gson.fromJson(offendersConfig.getText(), new TypeToken<Map<String, Integer>>() {}.getType());

        if (pastOffenders == null)
            pastOffenders = new HashMap<String, Integer>();
    }

    @Override
    public boolean onCommand(MessageEvent<?> event, String... args)
    {
        if (args.length < 2)
        {
            sendNotice(event.getUser(), "This command requires 2 args, [nick] and [time] (minutes)");
            return false;
        }

        User user = IRCUtils.getUserByNick(event.getChannel(), args[0]);
        int mins = 0;

        if (user == null)
        {
            sendMessage(event.getChannel(), "No such user \"" + args[0] + "\"!");
            return false;
        }

        String modifier = "none";

        char c = args[1].charAt(args[1].length() - 1);
        modifier = c >= '0' && c <= '9' ? "none" : Character.toString(c).toLowerCase();
        if (!modifier.equals("none"))
        {
            args[1] = args[1].substring(0, args[1].length() - 1);
        }

        int mult = getMultiplierForModifier(modifier);

        try
        {
            mins = Integer.parseInt(args[1]);
        }
        catch (NumberFormatException e)
        {
            sendNotice(event.getUser(), "Not a valid amount of time: \"" + (mins * mult) + "\"");
            return false;
        }

        event.getBot().sendRaw().rawLine("MODE " + event.getChannel().getName() + " +q " + user.getHostmask());
        boolean newOffense = true;

        for (int i = 0; i < list.size(); i++)
        {
            TimeoutTime t = list.get(i);
            if (t.user.equals(user.getNick()))
            {
                list.remove(t);
                newOffense = false;
            }
        }

        this.list.add(new TimeoutTime(System.currentTimeMillis(), mins * mult, event.getChannel().getName(), user.getNick()));

        String hostmask = user.getHostmask();

        if (newOffense)
        {
            if (pastOffenders.containsKey(hostmask))
            {
                int pastTimeouts = pastOffenders.get(hostmask);
                sendNotice(event.getUser(), String.format("The user \"%s\" with hostmask \"%s\" has been timed out %s time%s before.", user.getNick(), hostmask, Colors.BOLD + pastTimeouts
                        + Colors.NORMAL, pastTimeouts <= 1 ? "" : "s"));
                pastOffenders.put(hostmask, pastTimeouts + 1);
            }
            else
            {
                this.pastOffenders.put(hostmask, 1);
            }
        }

        return true;
    }

    private int getMultiplierForModifier(String modifier)
    {
        switch (modifier)
        {
        case "s":
            return 1;
        case "m":
            return 60;
        case "h":
            return 3600;
        case "d":
            return 86400;
        case "w":
            return 604800;
        default:
            return 60;
        }
    }

    @Override
    public String getDesc()
    {
        return "Quiets the specified user for the specified amount of time. Minutes by default, or specified by a character at the end (e.g. 's' or 'd')";
    }

    @Override
    public boolean shouldReceiveEvents()
    {
        return true;
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent<PircBotX> event)
    {
        timeoutConfig.writeJsonToFile(list);
        offendersConfig.writeJsonToFile(pastOffenders);
    }
}
