package tterrag.tppibot.runnables;

import org.pircbotx.Channel;
import org.pircbotx.User;

import tterrag.tppibot.Main;
import tterrag.tppibot.commands.Timeout;
import tterrag.tppibot.commands.Timeout.TimeoutTime;
import tterrag.tppibot.util.IRCUtils;
import tterrag.tppibot.util.Logging;
import tterrag.tppibot.util.ThreadUtils;

public class TimeoutChecker implements Runnable
{
    private Timeout instance;
    private final int retry = 15 * 60;

    public TimeoutChecker(Timeout instance)
    {
        this.instance = instance;
    }

    @Override
    public void run()
    {
        ThreadUtils.sleep(15000);
        while (true)
        {
            ThreadUtils.sleep(1000);
            try
            {
                for (int i = 0; i < this.instance.list.size(); i++)
                {
                    TimeoutTime time = i < this.instance.list.size() ? this.instance.list.get(i) : null;

                    if (time == null)
                        continue;

                    if (time.isTimeUp())
                    {
                        Channel channel = IRCUtils.getChannelByName(Main.bot, time.channel);

                        if (channel == null)
                        {
                            Logging.log("Bot is not connected to " + time.channel + ", adding " + (retry / 60) + " minutes to the timeout on user " + time.user + ".");
                            time.addTime(retry);
                        }
                        else
                        {
                            User user = IRCUtils.getUserByNick(channel, time.user);

                            if (user == null)
                            {
                                Logging.log("Could not find user " + time.user + " in channel " + time.channel + ", adding " + (retry / 60) + " minutes to the timeout on user " + time.user + ".");
                                time.addTime(retry);
                            }
                            else
                            {
                                Main.bot.sendRaw().rawLine("MODE " + time.channel + " -q " + IRCUtils.getUserByNick(IRCUtils.getChannelByName(Main.bot, time.channel), time.user).getHostmask());
                                this.instance.list.remove(i);
                            }
                        }
                    }
                }
            }
            catch (Throwable t)
            {
                t.printStackTrace();
                Logging.error("Error in timeout thread, continuing...");
            }
        }
    }
}
