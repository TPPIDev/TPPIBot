package tterrag.tppibot.commands;

import java.util.List;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

public class Nick extends Command {

    public Nick() {
        super("nick", PermLevel.CONTROLLER);
    }

    @Override
    public void onCommand(PircBotX bot, User user, Channel channel, List<String> lines, String... args) {
        bot.sendRaw().rawLine("NICK :" + args[0]);
    }
}
