package tterrag.tppibot.commands;

import java.util.ArrayList;
import java.util.List;

import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

import tterrag.tppibot.annotations.ReceiveExitEvent;
import tterrag.tppibot.config.Config;
import tterrag.tppibot.util.IRCUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Timeout extends Command
{    
    public class TimeoutTime
    {
        public final long init;
        public final long time;
        
        public final String channel;
        public final String user;
        
        public TimeoutTime(long start, int mins, String chan, String user)
        {
            this.init = start;
            this.time = mins * 60 * 1000;
            this.channel = chan;
            this.user = user;
        }
        
        public boolean isTimeUp()
        {
            return System.currentTimeMillis() - init > time;
        }
    }
    
    public List<TimeoutTime> list;
    private Config config;
    
    public Timeout()
    {
        super ("timeout", PermLevel.OP);

        config = new Config("timeouts.json");

        list = new Gson().fromJson(config.getText(), new TypeToken<List<TimeoutTime>>(){}.getType());
        
        if (list == null)
            list = new ArrayList<TimeoutTime>();
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
        
        try
        {
            mins = Integer.parseInt(args[1]);
        }
        catch (NumberFormatException e)
        {
            sendNotice(event.getUser(), "Not a valid amount of minutes: \"" + args[1] + "\"");
            return false;
        }
        
        event.getBot().sendRaw().rawLine("MODE " + event.getChannel().getName()+ " +q " + user.getHostmask());
        this.list.add(new TimeoutTime(System.currentTimeMillis(), mins, event.getChannel().getName(), user.getNick()));
        return true;
    }
    
    @Override
    public String getDesc()
    {
        return "Quiets the specified user for the specified amount of minutes";
    }
    
    @ReceiveExitEvent
    public void onExitEvent()
    {
        config.writeJsonToFile(list);
    }
}
