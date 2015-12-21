package tterrag.tppibot.commands;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

public class UUID extends Command {

    public UUID() {
        super("uuid");
    }

    @Override
    public void onCommand(PircBotX bot, User user, Channel channel, List<String> lines, String... args) {
        if (args.length > 0) {
            try {
                URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + args[0]);
                BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                String uuid;

                if ((uuid = reader.readLine()) != null) {
                    String[] cleaned = getInfo(uuid);
                    lines.add(cleaned[1] + " ==> " + formatUUID(cleaned[0]));
                } else {
                    lines.add("Unable to find any matching names.");
                }

                return;
            } catch (IOException e) {
                lines.add("The Mojang API is currently unavailable.");
                return;
            }
        }

        lines.add("This command requires at least one arg.");
    }

    @Override
    public String getDesc() {
        return "Returns the Minecraft UUID for the given Username.";
    }

    private String[] getInfo(String input) {
        String cleaned = input.replace("{", "").replace("}", "").replace("\"", ""); // Get rid of the JSON formatting
        String[] cleanSplit = cleaned.split(","); // Split at the separator between id and name

        String[] ret = new String[cleanSplit.length];
        ret[0] = cleanSplit[0].replace("id:", "");
        ret[1] = cleanSplit[1].replace("name:", "");

        return ret;
    }

    private String formatUUID(String input) {
        String[] sections = new String[5];
        sections[0] = input.substring(0, 8);
        sections[1] = input.substring(8, 12);
        sections[2] = input.substring(12, 16);
        sections[3] = input.substring(16, 20);
        sections[4] = input.substring(20, 32);

        return sections[0] + "-" + sections[1] + "-" + sections[2] + "-" + sections[3] + "-" + sections[4];
    }
}
