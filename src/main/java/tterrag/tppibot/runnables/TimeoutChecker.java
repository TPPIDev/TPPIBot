package tterrag.tppibot.runnables;

import tterrag.tppibot.commands.Timeout;
import tterrag.tppibot.commands.Timeout.TimeoutTime;

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
                    time.bot.sendRaw().rawLine("MODE "+ time.channel.getName() + " -q " + time.user.getHostmask());
                    this.instance.list.remove(i);
                }
            }
        }
    }
}
