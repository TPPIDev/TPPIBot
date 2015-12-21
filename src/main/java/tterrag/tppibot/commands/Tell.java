package tterrag.tppibot.commands;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.events.DisconnectEvent;
import org.pircbotx.hooks.events.MessageEvent;

import tterrag.tppibot.config.Config;
import tterrag.tppibot.runnables.MessageSender;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.eventbus.Subscribe;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class Tell extends Command {

    public static class MultimapJson implements JsonSerializer<Multimap<?, ?>> {

        @Override
        public JsonElement serialize(Multimap<?, ?> src, Type typeOfSrc, JsonSerializationContext context) {
            return context.serialize(src.asMap());
        }
    }

    private Config tellsConfig;
    private ArrayListMultimap<String, TellMessage> tells = ArrayListMultimap.create();

    @SuppressWarnings("serial")
    public Tell() {
        super("tell");

        tellsConfig = new Config("tells.json");
        tellsConfig.setGson(new GsonBuilder().setPrettyPrinting().registerTypeHierarchyAdapter(Multimap.class, new MultimapJson()).create());

        if (Strings.isNullOrEmpty(tellsConfig.getText())) {
            tellsConfig.writeJsonToFile(tells);
        } else {
            Map<String, Collection<TellMessage>> temp = new Gson().fromJson(tellsConfig.getText(), new TypeToken<Map<String, Collection<TellMessage>>>() {
            }.getType());
            temp.entrySet().forEach(e -> tells.putAll(e.getKey(), e.getValue()));
        }
    }

    @Override
    public void onCommand(PircBotX bot, User user, Channel channel, List<String> lines, String... args) {
        if (args.length < 2) {
            lines.add("This command requires at least two args.");
            return;
        }

        String message = Joiner.on(' ').join(args);
        TellMessage send = new TellMessage(args[0], message, channel.getName(), user.getNick());

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
            Collection<TellMessage> messages = tells.get(event.getUser().getNick());
            Collection<TellMessage> toRemove = Lists.newArrayList();
            for (TellMessage toSend : messages) {
                if (toSend.getChannel().equals(event.getChannel().getName())) {
                    MessageSender.INSTANCE.enqueueNotice(event.getBot(), toSend.getSendTo(), "\"" + toSend.getMessage() + "\" - " + toSend.getFrom());
                    toRemove.add(toSend);
                }
            }
            messages.removeAll(toRemove);
        }
    }

    @RequiredArgsConstructor
    @Getter
    @EqualsAndHashCode
    @ToString
    public static class TellMessage {

        private final String sendTo;
        private final String message;
        private final String channel;
        private final String from;
    }
}
