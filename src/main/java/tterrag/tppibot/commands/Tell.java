package tterrag.tppibot.commands;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.events.DisconnectEvent;
import org.pircbotx.hooks.events.MessageEvent;
import tterrag.tppibot.annotations.Subscribe;
import tterrag.tppibot.config.Config;
import tterrag.tppibot.runnables.MessageSender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tell extends Command {

    private Config tellsConfig;
    private Map<String, TellMessage> tells = new HashMap<>();

    public Tell() {
        super("tell");

        tellsConfig = new Config("tells.json");

        if (Strings.isNullOrEmpty(tellsConfig.getText())) {
            tellsConfig.writeJsonToFile(tells);
        } else {
            tells = new Gson().fromJson(tellsConfig.getText(), Map.class);
        }
    }

    @Override
    public void onCommand(PircBotX bot, User user, Channel channel, List<String> lines, String... args) {

        if (args.length < 2) {
            lines.add("This command requires at least two args.");
            return;
        }

        String message = "";

        for (int i = 1; i < args.length; i++)
            message += (message.length() > 0 ? " " : "") + args[i];

        TellMessage send = new TellMessage(args[0], message, channel, user);

        if (!tells.containsValue(send)) {
            tells.put(send.getSendTo(), send);
            MessageSender.INSTANCE.enqueueNotice(bot, user.getNick(), "I'll pass that along.");
        } else {
            MessageSender.INSTANCE.enqueueNotice(bot, user.getNick(), "I can't let you do that.");
        }
    }

    @Override
    public String getDesc() {
        return "Stores a message to send to a user when they return.";
    }

    @Override
    public boolean shouldReceiveEvents() {
        return true;
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent<PircBotX> event) {
        tellsConfig.writeJsonToFile(tells);
    }

    @Subscribe
    public void onMessage(MessageEvent<PircBotX> event) {
        if (tells.containsKey(event.getUser().getNick())) {
            TellMessage toSend = tells.get(event.getUser().getNick());
            if (toSend.getChannel() == event.getChannel()) {
                MessageSender.INSTANCE.enqueueNotice(event.getBot(), toSend.getSendTo(), "\"" + toSend.getMessage() + "\" - " + toSend.getFrom().getNick());
                tells.remove(toSend.getSendTo(), toSend);
            }
        }
    }

    @RequiredArgsConstructor
    @Getter
    @EqualsAndHashCode
    @ToString
    public static class TellMessage {

        private final String sendTo;
        private final String message;
        private final Channel channel;
        private final User from;
    }
}
