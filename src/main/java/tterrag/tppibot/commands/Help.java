package tterrag.tppibot.commands;

import tterrag.tppibot.Main;

public class Help extends Command
{
    public Help()
    {
        super("help", PermLevel.ANY);
    }
    
    @Override
    public boolean onCommand(String channel, String user, String... args)
    {
        if (channel == null || user == null) return false;
        
        Main.bot.sendMessage(channel, user + ", I am not a very helpful bot yet :(");
        return true;
    }

    @Override
    public Command editCommand(Object... params)
    {
        return this;
    }
}
