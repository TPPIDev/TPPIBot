package tterrag.tppibot.commands;

import java.util.List;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

import tterrag.tppibot.Main;

public class BanWord extends Command
{
    public BanWord()
    {
        super("banned", PermLevel.CONTROLLER);
    }
    
    @Override
    public void onCommand(PircBotX bot, User user, Channel channel, List<String> lines, String... args)
    {
        if (args.length < 2)
        {
            lines.add("This command requires 2 args, [action] and [word]");
            return;
        }
        
        if ("add".equals(args[0]))
        {
            Main.bannedWords.addWord(args[1]);
            lines.add("Successfully banned word.");
        }
        else if ("remove".equals(args[0]))
        {
            Main.bannedWords.removeWord(args[1]);
            lines.add("Successfully removed word.");
        }
        else
        {
            lines.add(args[0] + " is not a valid action.");
        }
    }
}
