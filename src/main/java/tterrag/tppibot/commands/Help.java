package tterrag.tppibot.commands;

import org.apache.commons.lang3.StringUtils;

import tterrag.tppibot.Main;

public class Help extends Command
{
    private String helpText = "%user%, I am not a very helpful bot yet :(";
    
    public Help()
    {
        super("help", PermLevel.ANY);
    }
    
    @Override
    public boolean onCommand(String channel, String user, String... args)
    {
        if (channel == null || user == null) return false;
        
        Main.getBot().sendMessage(channel, helpText.replace("%user%", args.length >= 1 ? args[0] : user));
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
