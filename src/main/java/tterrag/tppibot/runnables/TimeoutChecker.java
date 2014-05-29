package tterrag.tppibot.runnables;

import tterrag.tppibot.Main;
import tterrag.tppibot.commands.Timeout;
import tterrag.tppibot.commands.Timeout.TimeoutTime;
import tterrag.tppibot.util.IRCUtils;

public class TimeoutChecker implements Runnable
{
    private Timeout instance;
    
    public TimeoutChecker(Timeout instance)
    {
        this.instance = instance;
    }
    
    @Override
    public void run()
    {
        while (true)
        {
            for (int i = 0; i < this.instance.list.size(); i++)
            {
                TimeoutTime time = i < this.instance.list.size() ? this.instance.list.get(i) : null;
                
                if (time == null) continue;
                
                if (time.isTimeUp())
                {
                    Main.bot.sendRaw().rawLine("MODE "+ time.channel + " -q " + IRCUtils.getUserByNick(IRCUtils.getChannelByName(Main.bot, time.channel), time.user).getHostmask());
                    this.instance.list.remove(i);
                }
            }
        }
    }
}
