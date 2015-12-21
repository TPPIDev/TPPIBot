package tterrag.tppibot.commands;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

import tterrag.tppibot.runnables.MessageSender;
import tterrag.tppibot.util.IRCUtils;

public class Drama extends Command {

    public Drama() {
        super("drama");
    }

    @Override
    public void onCommand(PircBotX bot, User user, Channel channel, List<String> lines, String... args) {
        try {
            URI uri = new URI("http://tterrag.com/drama.php?plain");
            HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String drama = in.readLine();

            if (channel != null) {
                // ping avoiding
                String[] words = drama.split(" ");
                for (int i = 0; i < words.length; i++) {
                    User possibleUser = IRCUtils.getUserByNick(channel, words[i]);
                    if (possibleUser != null) {
                        char[] username = words[i].toCharArray();
                        username = ArrayUtils.add(username, 1, '.');
                        words[i] = new String(username);
                    }
                }
                drama = StringUtils.join(words, " ");
            }

            MessageSender.INSTANCE.enqueue(bot, channel == null ? user.getNick() : channel.getName(), drama);
        } catch (Exception e) {
            lines.add(e.getClass().getName());
            e.printStackTrace();
        }
    }
}
