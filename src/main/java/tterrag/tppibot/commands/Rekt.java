package tterrag.tppibot.commands;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.events.DisconnectEvent;
import tterrag.tppibot.annotations.Subscribe;
import tterrag.tppibot.config.Config;
import tterrag.tppibot.runnables.MessageSender;
import tterrag.tppibot.util.IRCUtils;

import java.util.*;

public class Rekt extends Command {

    private final String PREFIX = "\u2610 Not REKT";

    private final String[] REKT_DEFAULT = {
            "\u2611 REKT",
            "\u2611 REKTangle",
            "\u2611 SHREKT",
            "\u2611 REKT-it Ralph",
            "\u2611 Total REKTall",
            "\u2611 The Lord of the REKT",
            "\u2611 Tyrannosaurus REKT",
            "\u2611 9.0 on the REKTer scale",
            "\u2611 Shrexamination",
            "\u2611 ShreX marks the spot",
            "\u2611 A Game of Rekt",
            "\u2611 The Rekt Prince of Bel-Air",
            "\u2611 Star Wars: Episode VI - Return of the Rekt",
            "\u2611 Erektile Dysfunction",
            "\u2611 2001: A Rekt Odyssey",
            "\u2611 Harry Potter: The Half-Rekt Prince"
    };

    private ArrayList<String> rektEntries = new ArrayList<>();

    private Config rektConfig;

    public Rekt() {
        super("rekt");

        rektConfig = new Config("rektEntries.json");

        if (Strings.isNullOrEmpty(rektConfig.getText())) {
            rektEntries.addAll(Arrays.asList(REKT_DEFAULT));
            rektConfig.writeJsonToFile(rektEntries);
        } else {
            rektEntries = new Gson().fromJson(rektConfig.getText(), new TypeToken<ArrayList<String>>() {}.getType());
        }
    }

    @Override
    public void onCommand(PircBotX bot, User user, Channel channel, List<String> lines, String... args) {
        Random random = new Random();

        if (args.length == 0) {
            lines.add(String.format("%s %s", PREFIX, rektEntries.get(random.nextInt(rektEntries.size()))));
            return;
        }

        if (IRCUtils.isUserAboveOrEqualTo(channel, PermLevel.TRUSTED, user)) {
            if (args.length == 1 && args[0].equals("list")) {
                String ret = "";
                for (String entry : rektEntries)
                    ret += (ret.length() > 0 ? ", " : "") + entry;

                MessageSender.INSTANCE.enqueueNotice(bot, user.getNick(), ret);
                return;
            } else if (args.length >= 2) {
                String rektString = "";

                for (int i = 1; i < args.length; i++)
                    rektString += (rektString.length() > 0 ? " " : "") + args[i];

                switch (args[0]) {
                    case "add": {
                        if (!rektEntries.contains(rektString)) {
                            rektEntries.add(rektString);
                            lines.add(String.format("Successfully added entry \"%s\"", rektString));
                        } else {
                            lines.add(String.format("Could not add entry \"%s\"", rektString));
                        }
                        return;
                    }
                    case "del": {
                        if (rektEntries.contains(rektString)) {
                            rektEntries.remove(rektString);
                            lines.add(String.format("Successfully removed entry \"%s\"", rektString));
                        } else {
                            lines.add(String.format("Could not remove entry \"%s\"", rektString));
                        }
                        return;
                    }
                    default: return;
                }
            }
        }

        MessageSender.INSTANCE.enqueueNotice(bot, user.getNick(), String.format("You have no permission, you must be at least: %s", PermLevel.TRUSTED));
    }

    @Override
    public String getDesc() {
        return "Returns the state in which the user is.";
    }

    @Override
    public boolean shouldReceiveEvents() {
        return true;
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent<PircBotX> event) {
        rektConfig.writeJsonToFile(rektEntries);
    }
}
