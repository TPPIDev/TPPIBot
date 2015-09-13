package tterrag.tppibot.commands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

import tterrag.tppibot.runnables.MessageSender;

public class Shortener extends Command
{
    public Shortener()
    {
        super("shorten");
    }

    @Override
    public void onCommand(PircBotX bot, User user, Channel channel, List<String> lines, String... args)
    {
        if (args.length < 1)
        {
            lines.add("This command requires 1 arg (url).");
            return;
        }

        try
        {
            String link = "http://is.gd/create.php?format=simple&url=" + URLEncoder.encode(args[0], "UTF-8");
            URL url = new URL(link);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setDoOutput(true);
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            MessageSender.INSTANCE.enqueue(bot, channel == null ? user.getNick() : channel.getName(), "> " + in.readLine());
        }
        catch (IOException e)
        {
            lines.add("I/O Error!");
            e.printStackTrace();
        }
    }
}
