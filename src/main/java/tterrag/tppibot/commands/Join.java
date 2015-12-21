package tterrag.tppibot.commands;

import java.util.Arrays;
import java.util.List;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

import tterrag.tppibot.util.IRCUtils;

public class Join extends Command {

    public Join() {
        super("join", PermLevel.CONTROLLER);
    }

    @Override
    public void onCommand(PircBotX bot, User user, Channel channel, List<String> lines, String... args) {
        if (args.length > 0) {
            Arrays.stream(args).forEach(s -> bot.sendIRC().joinChannel(IRCUtils.fmtChan(s)));
        } else {
            lines.add("Must supply at least one channel!");
        }
    }

    @Override
    public String getDesc() {
        return "Joins a channel.";
    }
}
