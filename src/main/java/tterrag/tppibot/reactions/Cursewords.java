package tterrag.tppibot.reactions;

import static tterrag.tppibot.reactions.CharacterSpam.SpamReasons.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.pircbotx.hooks.events.MessageEvent;

import tterrag.tppibot.Main;
import tterrag.tppibot.interfaces.ICommand.PermLevel;
import tterrag.tppibot.interfaces.IReaction;
import tterrag.tppibot.registry.PermRegistry;
import tterrag.tppibot.util.IRCUtils;

public class Cursewords implements IReaction
{
    private List<String> curses;

    public Cursewords()
    {
        loadCurseWords();
    }

    @Override
    public void onMessage(MessageEvent<?> event)
    {
        PermLevel level = PermRegistry.instance().getPermLevelForUser(event.getChannel(), event.getUser());

        if (!IRCUtils.isPermLevelAboveOrEqualTo(level, PermLevel.TRUSTED))
        {
            for (String s : curses)
            {
                Matcher matcher = Pattern.compile("\\b(" + s + ")\\b").matcher(event.getMessage());
                while (matcher.find())
                {
                    String word = matcher.group();
                    if (word.equals(s))
                    {
                        Main.spamFilter.finish(Main.spamFilter.timeout(event, CURSE) ? event.getUser() : null);
                    }
                }
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
