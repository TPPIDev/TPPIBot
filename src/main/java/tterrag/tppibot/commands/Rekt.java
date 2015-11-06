package tterrag.tppibot.commands;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

import java.util.List;
import java.util.Random;

public class Rekt extends Command {

    String[] rektList = {
            "\u2610 Not REKT" ,
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

    public Rekt() {
        super("rekt");
    }

    @Override
    public void onCommand(PircBotX bot, User user, Channel channel, List<String> lines, String... args) {
        Random random = new Random();

        lines.add(rektList[random.nextInt(rektList.length)]);
    }

    @Override
    public String getDesc() {
        return "Returns the state in which the user is.";
    }
}
