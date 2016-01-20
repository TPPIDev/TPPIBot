package tterrag.tppibot.reactions;

import static tterrag.tppibot.reactions.CharacterSpam.SpamReasons.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Value;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.events.DisconnectEvent;
import org.pircbotx.hooks.events.MessageEvent;

import tterrag.tppibot.Main;
import tterrag.tppibot.config.Config;
import tterrag.tppibot.interfaces.ICommand.PermLevel;
import tterrag.tppibot.interfaces.IReaction;
import tterrag.tppibot.runnables.MessageSender;
import tterrag.tppibot.util.IRCUtils;
import tterrag.tppibot.util.Logging;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

public class CharacterSpam implements IReaction {

    public enum SpamReasons {
        REPEATS("You had too many repeated characters."),
        SYMBOLS("You had too many non-alphabetic symbols."),
        CAPS("Too much caps!"),
        FLOOD("Too many messages!"),
        CURSE("Banned word.");

        private String text;

        SpamReasons(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }
    
    @Value
    public static class Strike {
        private SpamReasons reason;
        private String message;
    }

    private Map<Character, Integer> repeated;

    private ListMultimap<String, Strike> strikes;
    private Config strikesConfig;

    private static Set<String> blacklistChannels = Sets.newConcurrentHashSet();
    private Config blacklistConfig;

    private static List<Character> whitelistedChars = Arrays.asList(new Character[] { ' ', '.' });

    public CharacterSpam() {
        repeated = new HashMap<Character, Integer>();

        strikesConfig = new Config("spamStrikes.json");
        blacklistConfig = new Config("spamChannelBlacklist.json");

        Gson gson = new GsonBuilder().registerTypeAdapter(new TypeToken<List<Strike>>(){}.getType(), new JsonDeserializer<List<Strike>>() {

            @Override
            public List<Strike> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                List<Strike> ret = new ArrayList<>();
                if (json.isJsonPrimitive()) {
                    for (int i = 0; i < json.getAsInt(); i++) {
                        ret.add(new Strike(null, "Unknown"));
                    }
                } else {
                    JsonArray arr = json.getAsJsonArray();
                    for (int i = 0; i < arr.size(); i++) {
                        ret.add(context.deserialize(arr.get(i), Strike.class));
                    }
                }
                return ret;
            }
        }).registerTypeAdapter(new TypeToken<ListMultimap<String, Strike>>() {}.getType(), new InstanceCreator<ListMultimap<String, Strike>>() {

            @Override
            public ListMultimap<String, Strike> createInstance(Type type) {
                return MultimapBuilder.hashKeys().arrayListValues().build();
            }
        }).create();

        Map<String, List<Strike>> map = gson.fromJson(strikesConfig.getText(), new TypeToken<Map<String, List<Strike>>>() {
        }.getType());

        blacklistChannels = gson.fromJson(blacklistConfig.getText(), new TypeToken<Set<String>>() {
        }.getType());

        strikes = MultimapBuilder.hashKeys().arrayListValues().build();
        if (map != null) {
            map.forEach((s, l) -> strikes.putAll(s, l));
        }
        if (blacklistChannels == null)
            blacklistChannels = Sets.newConcurrentHashSet();
    }

    @Override
    public synchronized void onMessage(MessageEvent<?> event) {
        int symbolCount = 0;
        int caps = 0;

        String msg = event.getMessage();

        if (msg.length() < 12)
            return;

        if (blacklistChannels.contains(event.getChannel().getName().toLowerCase()))
            return;

        for (char c : msg.toCharArray()) {
            if (!((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || whitelistedChars.contains(c))) {
                symbolCount++;
            }

            if (c >= 'A' && c <= 'Z') {
                caps++;
            }

            if (repeated.containsKey(c)) {
                repeated.put(c, repeated.get(c) + 1);
            } else {
                repeated.put(c, 1);
            }
        }

        for (char c : repeated.keySet()) {
            if (repeated.get(c) > msg.length() / 2) {
                Logging.log("too many repeated characters!");
                finish(timeout(event, REPEATS) ? event.getUser() : null, REPEATS, msg);
                return;
            }
        }

        if (symbolCount > msg.length() / 2) {
            Logging.log("too many symbols!");
            finish(timeout(event, SYMBOLS) ? event.getUser() : null, SYMBOLS, msg);
            return;
        }

        // No caps for now
        // if (caps > (double) msg.length() / 1.75d)
        // {
        // Logging.log("caps!");
        // finish(timeout(event, CAPS) ? event.getUser() : null);
        // return;
        // }

        finish(null, null, null);
    }

    public void finish(User user, SpamReasons reason, String message) {
        repeated.clear();

        if (user == null)
            return;

        String hostmask = user.getHostmask();

        strikes.put(hostmask, new Strike(reason, message));
    }

    public boolean timeout(MessageEvent<?> event, SpamReasons reason) {
        return timeout(event.getBot(), event.getUser(), event.getChannel(), reason);
    }

    public boolean timeout(PircBotX bot, User user, Channel channel, SpamReasons reason) {
        if (IRCUtils.userIsOp(channel, bot.getUserBot()) && !IRCUtils.isUserAboveOrEqualTo(channel, PermLevel.TRUSTED, user)) {
            int strikeCount = 0;
            if (strikes.containsKey(user.getHostmask())) {
                strikeCount = strikes.get(user.getHostmask()).size();
            }

            if (reason == SpamReasons.CURSE) {
                MessageSender.INSTANCE.enqueue(bot, channel.getName(), user.getNick() + ", please do not do that! This is strike " + (strikeCount + 1) + ", you will now be timed out for " + 10
                        + " minutes. Reason: " + reason.getText());
                IRCUtils.timeout(bot, user, channel, "" + 10);
                return true;
            }

            if (strikeCount < 3) {
                MessageSender.INSTANCE.enqueue(bot, channel.getName(), user.getNick() + ", please do not do that! This is strike " + (strikeCount + 1) + "! Reason: " + reason.getText());
            } else {
                MessageSender.INSTANCE.enqueue(bot, channel.getName(), user.getNick() + ", please do not do that! This is strike " + (strikeCount + 1) + ", you will now be timed out for "
                        + (5 * (strikeCount - 2)) + " minutes. Reason: " + reason.getText());
                IRCUtils.timeout(bot, user, channel, "" + 5 * (strikeCount - 2));
            }
            return true;
        }
        return false;
    }

    /**
     * @return true if added, false if removed
     */
    public static boolean toggleBlacklistChannel(String channelname) {
        synchronized (blacklistChannels) {
            channelname = channelname.toLowerCase();
            if (blacklistChannels.contains(channelname)) {
                blacklistChannels.remove(channelname);
                return false;
            } else {
                blacklistChannels.add(channelname);
                return true;
            }
        }
    }

    public boolean filtersEnabled(String channelname) {
        synchronized (blacklistChannels) {
            channelname = channelname.toLowerCase();
            return !blacklistChannels.contains(channelname);
        }
    }

    public int getStrikeCount(User user) {
        return strikes.get(user.getHostmask()).size();
    }

    public int removeStrikes(User user, int amnt) {
        String hostmask = user.getHostmask();
        while (--amnt >= 0 && !strikes.get(hostmask).isEmpty()) {
            strikes.get(hostmask).remove(0);
        }
        return getStrikeCount(user);
    }
    
    public List<Strike> getStrikes(User user) {
        return ImmutableList.copyOf(strikes.get(user.getHostmask()));
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent<?> event) {
        String text = Main.gson.toJson(strikes.asMap());
        System.out.println(text);
        System.out.println(strikes);
        strikesConfig.writeTextToFile(text);
        blacklistConfig.writeJsonToFile(blacklistChannels);
    }
}
