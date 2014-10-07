package tterrag.tppibot.reactions;

import static tterrag.tppibot.reactions.CharacterSpam.SpamReasons.*;

import java.io.InputStream;
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
        for (String s : curses)
        {
            Matcher matcher = Pattern.compile("\\b(" + s + ")\\b", Pattern.CASE_INSENSITIVE).matcher(event.getMessage());
            while (matcher.find())
            {
                String word = matcher.group();
                if (word.equalsIgnoreCase(s))
                {
                    PermLevel level = PermRegistry.instance().getPermLevelForUser(event.getChannel(), event.getUser());
                    if (!IRCUtils.isPermLevelAboveOrEqualTo(level, PermLevel.TRUSTED))
                    {
                        Main.spamFilter.finish(Main.spamFilter.timeout(event, CURSE) ? event.getUser() : null);
                    }
                }
            }
        }
    }

    private void loadCurseWords()
    {
        InputStream in = Main.class.getResourceAsStream("/curses.txt");

        curses = new ArrayList<String>();

        Scanner scan = new Scanner(in);

        while (scan.hasNextLine())
        {
            curses.add(scan.nextLine().trim());
        }

        scan.close();
    }
}
