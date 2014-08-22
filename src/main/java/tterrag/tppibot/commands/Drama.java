package tterrag.tppibot.commands;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.List;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

import tterrag.tppibot.runnables.MessageSender;

public class Drama extends Command
{
    public Drama()
    {
        super("drama");
    }

    @Override
    public void onCommand(PircBotX bot, User user, Channel channel, List<String> lines, String... args)
    {
        try
        {
            URI uri = new URI("http://asie.pl/drama.php");
            HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
            
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String s = "";
            while ((s = in.readLine()) != null)
            {
                if (s.startsWith("<h1>"))
                {
                    MessageSender.instance.enqueue(bot, channel == null ? user.getNick() : channel.getName(), s.substring(4, s.length() -5));
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
