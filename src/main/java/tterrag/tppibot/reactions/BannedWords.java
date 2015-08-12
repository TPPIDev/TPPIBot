package tterrag.tppibot.reactions;

import static tterrag.tppibot.reactions.CharacterSpam.SpamReasons.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.pircbotx.Colors;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.DisconnectEvent;
import org.pircbotx.hooks.events.MessageEvent;

import tterrag.tppibot.Main;
import tterrag.tppibot.annotations.Subscribe;
import tterrag.tppibot.config.Config;
import tterrag.tppibot.interfaces.ICommand.PermLevel;
import tterrag.tppibot.interfaces.IReaction;
import tterrag.tppibot.registry.PermRegistry;
import tterrag.tppibot.util.IRCUtils;

import com.google.gson.reflect.TypeToken;

public class BannedWords implements IReaction
{
    private List<String> bannedWords;
    private Config bannedConfig;

    public BannedWords()
    {
        bannedConfig = new Config("bannedWords.json");

        bannedWords = Config.gson.fromJson(bannedConfig.getText(), new TypeToken<List<String>>() {}.getType());

        if (bannedWords == null)
        {
            loadDefaultWords();
        }
    }

    @Override
    public void onMessage(MessageEvent<?> event)
    {
        if (Main.spamFilter.filtersEnabled(event.getChannel().getName()))
        {
            for (String s : bannedWords)
            {
                Matcher matcher = Pattern.compile("\\b(" + s + ")\\b", Pattern.CASE_INSENSITIVE).matcher(Colors.removeFormattingAndColors(event.getMessage()));
                while (matcher.find())
                {
                    String word = matcher.group();
                    if (word.equalsIgnoreCase(s))
                    {
                        PermLevel level = PermRegistry.INSTANCE.getPermLevelForUser(event.getChannel(), event.getUser());
                        if (!IRCUtils.isPermLevelAboveOrEqualTo(level, PermLevel.TRUSTED))
                        {
                            Main.spamFilter.finish(Main.spamFilter.timeout(event, CURSE) ? event.getUser() : null);
                        }
                    }
                }
            }
        }
    }

    private void loadDefaultWords()
    {
        InputStream in = Main.class.getResourceAsStream("/curses.txt");

        bannedWords = new ArrayList<String>();

        Scanner scan = new Scanner(in);

        while (scan.hasNextLine())
        {
            bannedWords.add(scan.nextLine().trim());
        }

        scan.close();
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent<PircBotX> event)
    {
        bannedConfig.writeJsonToFile(bannedWords);
    }

    public void addWord(String string)
    {
        if (!bannedWords.contains(string))
        {
            bannedWords.add(string);
        }
    }

    public void removeWord(String string)
    {
        bannedWords.remove(string);
    }
}
