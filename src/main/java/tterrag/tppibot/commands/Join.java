package tterrag.tppibot.commands;

import tterrag.tppibot.Main;

public class Join extends Command
{
    public Join()
    {
        super("join", PermLevel.OP);
    }

    @Override
    public boolean onCommand(String channel, String user, String... args)
    {
        if (args.length > 0)
        {
            for (String s : args)
            {
                Main.bot.join(s);
            }
            return true;
        }
        else 
        {
            sendNotice(user, "Must supply at least one channel!");
            return false;
        }
    }
}
