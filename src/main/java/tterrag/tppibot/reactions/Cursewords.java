package tterrag.tppibot.reactions;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.pircbotx.Colors;
import org.pircbotx.hooks.events.MessageEvent;

public class Cursewords implements IReaction
{
    private List<String> curses;
    
    @Override
    public void onMessage(MessageEvent<?> event)
    {
        if (curses == null)
        {
            loadCurseWords();
        }
        
        for (String s : curses)
        {
            String message = event.getMessage().toLowerCase();
            if (message.contains(s.toLowerCase() + " ") || message.contains(" " + s.toLowerCase()))
            {
                event.getChannel().send().message(Colors.RED + event.getUser().getNick() + ", please avoid cursing!");
            }
        }
    }
    
    private void loadCurseWords()
    {
        File file = new File(new File("src").getAbsolutePath() + "/main/resources/curses.txt");
        System.out.println(file.getAbsolutePath());
        
        curses = new ArrayList<String>();

        try
        {
            Scanner scan = new Scanner(file);
            
            scan.nextLine();
            
            while (scan.hasNextLine())
            {
                curses.add(scan.nextLine());
            }
            
            scan.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }
}
