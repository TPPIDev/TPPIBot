package tterrag.tppibot.commands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

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
            URI uri = new URI("http://is.gd/create.php?format=simple&url=" + args[0]);
            HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();

            connection.setDoOutput(true);
            connection.setRequestMethod("POST");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            lines.add("> " + in.readLine());
        }
        catch (IOException e)
        {
            lines.add("I/O Error!");
            e.printStackTrace();
        }
        catch (URISyntaxException e)
        {
            lines.add("Invalid URL.");
            e.printStackTrace();
        }
    }
}
