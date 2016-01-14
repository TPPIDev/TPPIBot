package tterrag.tppibot.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.pircbotx.Channel;
import org.pircbotx.Colors;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.events.DisconnectEvent;

import tterrag.tppibot.config.Config;
import tterrag.tppibot.runnables.MessageSender;
import tterrag.tppibot.util.IRCUtils;

import com.google.common.eventbus.Subscribe;
import com.google.gson.reflect.TypeToken;

public class Timeout extends Command {

    public class TimeoutTime {

        private long init;
        private long time;

        public final String channel;
        public final String nick, hostmask;

        public TimeoutTime(long start, long secs, String chan, User user) {
            this.init = start;
            this.time = secs * 1000;
            this.channel = chan;
            this.nick = user.getNick();
            this.hostmask = user.getHostmask();
        }

        public boolean isTimeUp() {
            return System.currentTimeMillis() - init > time;
        }

        public void addTime(int secs) {
            this.time += secs * 1000;
        }

        public long getTime() {
            return time;
        }

        public void setStartNow() {
            this.init = System.currentTimeMillis();
        }
    }

    public List<TimeoutTime> list;
    public Map<String, Integer> pastOffenders;
    private Config timeoutConfig, offendersConfig;

    public Timeout() {
        super("timeout", PermLevel.TRUSTED);

        timeoutConfig = new Config("timeouts.json");
        offendersConfig = new Config("pastOffenders.json");

        list = gson.fromJson(timeoutConfig.getText(), new TypeToken<List<TimeoutTime>>() {
        }.getType());

        if (list == null)
            list = new ArrayList<TimeoutTime>();

        pastOffenders = gson.fromJson(offendersConfig.getText(), new TypeToken<Map<String, Integer>>() {
        }.getType());

        if (pastOffenders == null)
            pastOffenders = new HashMap<String, Integer>();
    }

    @Override
    public void onCommand(PircBotX bot, User user, Channel channel, List<String> lines, String... args) {
        if (args.length < 2) {
            lines.add("This command requires 2 args, [nick] and [time] (minutes)");
            return;
        }

        Optional<User> toTimeout = IRCUtils.getUserByNick(channel, args[0]);

        if (!toTimeout.isPresent()) {
            lines.add("No such user \"" + args[0] + "\"!");
            return;
        }

        int seconds = 0;

        try {
            seconds = IRCUtils.getSecondsFromString(args[1]);
        } catch (NumberFormatException e) {
            lines.add("Not a valid amount of time: \"" + args[1] + "\"");
            return;
        }

        bot.sendRaw().rawLine("MODE " + channel.getName() + " +q " + toTimeout.get().getHostmask());
        boolean newOffense = true;

        for (int i = 0; i < list.size(); i++) {
            TimeoutTime t = list.get(i);
            if (t.hostmask.equals(toTimeout.get().getNick())) {
                list.remove(t);
                newOffense = false;
            }
        }

        this.list.add(new TimeoutTime(System.currentTimeMillis(), seconds, channel.getName(), toTimeout.get()));

        String hostmask = toTimeout.get().getHostmask();

        if (newOffense) {
            if (pastOffenders.containsKey(hostmask)) {
                int pastTimeouts = pastOffenders.get(hostmask);
                MessageSender.INSTANCE
                        .enqueueNotice(bot, user.getNick(), String.format("The user \"%s\" with hostmask \"%s\" has been timed out %s time%s before.", toTimeout.get().getNick(), hostmask, Colors.BOLD
                                + pastTimeouts + Colors.NORMAL, pastTimeouts <= 1 ? "" : "s"));
                pastOffenders.put(hostmask, pastTimeouts + 1);
            } else {
                this.pastOffenders.put(hostmask, 1);
            }
        }
    }

    @Override
    public String getDesc() {
        return "Quiets the specified user for the specified amount of time. Minutes by default, or specified by a character at the end (e.g. 's' or 'd')";
    }

    @Override
    public boolean shouldReceiveEvents() {
        return true;
    }

    @Override
    public boolean needsOp() {
        return true;
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent<PircBotX> event) {
        timeoutConfig.writeJsonToFile(list);
        offendersConfig.writeJsonToFile(pastOffenders);
    }

    @Override
    public boolean executeWithoutChannel() {
        return false;
    }
}
