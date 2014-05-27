package tterrag.tppibot.commands;

import org.apache.commons.lang3.StringUtils;
import org.pircbotx.Channel;
import org.pircbotx.User;

import tterrag.tppibot.util.IRCUtils;

public class Help extends Command
{
    private String helpText = "%user%, I am not a very helpful bot yet :(";
    
    public Help()
    {
        super("help", PermLevel.ANY);
    }
    
    @Override
    public boolean onCommand(Channel channel, User user, String... args)
    {
        if (channel == null || user == null) return false;
        
        IRCUtils.sendMessageForUser(channel, user, helpText, args);
        return true;
    }
    
    @Override
    public Command editCommand(String... params)
    {
        if (params.length < 1) return this;
        
        String newText = StringUtils.join(params, ' ');
        
        this.helpText = newText;
        return this;
    }
}
