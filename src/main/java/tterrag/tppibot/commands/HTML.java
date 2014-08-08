package tterrag.tppibot.commands;

import java.io.IOException;
import java.util.List;

import org.jsoup.Jsoup;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

import tterrag.tppibot.util.PastebinPaster;

public class HTML extends Command
{
    private final PastebinPaster poster = new PastebinPaster();

    public HTML()
    {
        super("html");
    }

    @Override
    public void onCommand(PircBotX bot, User user, Channel channel, List<String> lines, String... args)
    {
        if (args.length < 1)
        {
            lines.add("This command requires 1 arg.");
            return;
        }

        String html;
        try
        {
            html = Jsoup.connect(args[0]).get().html();
        }
        catch (IOException e)
        {
            lines.add(e.getClass().getName());
            return;
        }

        String paste = poster.pasteData(html);

        lines.add("HTML Source of " + args[0] + " :  " + paste);
    }
}
