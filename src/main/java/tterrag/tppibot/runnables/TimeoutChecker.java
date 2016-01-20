package tterrag.tppibot.runnables;

import java.util.Optional;

import org.pircbotx.Channel;
import org.pircbotx.User;

import tterrag.tppibot.Main;
import tterrag.tppibot.commands.Timeout;
import tterrag.tppibot.commands.Timeout.TimeoutTime;
import tterrag.tppibot.util.IRCUtils;
import tterrag.tppibot.util.Logging;
import tterrag.tppibot.util.ThreadUtils;

public class TimeoutChecker implements Runnable {

    private Timeout instance;
    private final int retry = 15 * 60;

    public TimeoutChecker(Timeout instance) {
        this.instance = instance;
    }

    @Override
    public void run() {
        ThreadUtils.sleep(15000);
        while (true) {
            ThreadUtils.sleep(1000);
            try {
                for (int i = 0; i < this.instance.list.size(); i++) {
                    TimeoutTime time = i < this.instance.list.size() ? this.instance.list.get(i) : null;

                    if (time == null)
                        continue;

                    if (time.isTimeUp()) {
                        Optional<Channel> channel = IRCUtils.getChannelByName(Main.bot, time.channel);
                        
                        if (!channel.isPresent()) {
                            Logging.log("Bot is not connected to " + time.channel + ", adding " + (retry / 60) + " minutes to the timeout on user " + time.nick + ".");
                            time.addTime(retry);
                            continue;
                        }

                        if (IRCUtils.userIsOp(channel.get(), Main.bot.getUserBot())) {
                            Optional<User> user = IRCUtils.getUserByNick(channel.get(), time.nick);

                            Main.bot.sendRaw().rawLine("MODE " + time.channel + " -q " + time.hostmask);
                            if (user.isPresent()) {
                                IRCUtils.modeSensitiveEnqueue(Main.bot, user.get(), channel.get(), user.get().getNick()
                                        + ", you are no longer timed out. Be warned, repeat offenses could result in a ban.");
                            }
                            this.instance.list.remove(i);
                        } else {
                            try {
                                Main.bot.sendIRC().message(channel.get().getName(), "Please op me so I may remove the timeout on " + time.nick + "!");
                            } catch (Exception e) {
                                // what do I do now??
                            } finally {
                                time.setStartNow();
                            }
                        }
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
                Logging.error("Error in timeout thread, continuing...");
            }
        }
    }

    public synchronized int removePastOffenses(User user, int amnt) {
        for (String nick : instance.pastOffenders.keySet()) {
            if (nick.equals(user.getNick())) {
                instance.pastOffenders.put(nick, Math.max(0, instance.pastOffenders.get(nick) - amnt));
                return instance.pastOffenders.get(nick);
            }
        }
        return 0;
    }
}
