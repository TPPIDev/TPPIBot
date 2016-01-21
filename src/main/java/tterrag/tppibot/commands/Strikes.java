package tterrag.tppibot.commands;

import java.util.List;
import java.util.Optional;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

import tterrag.tppibot.Main;
import tterrag.tppibot.commands.Mode.BotMode;
import tterrag.tppibot.reactions.CharacterSpam.Strike;
import tterrag.tppibot.registry.PermRegistry;
import tterrag.tppibot.util.IRCUtils;

public class Strikes extends Command {

    public Strikes() {
        super("strikes");
    }

    @Override
    public void onCommand(PircBotX bot, User user, Channel channel, List<String> lines, String... args) {
        Optional<User> target = Optional.empty();
        if (args.length > 0) {
            target = IRCUtils.getUserByNick(channel, args[0]);
            if (!target.isPresent()) {
                lines.add(args[0] + " is not a valid nick in this channel!");
                return;
            }
        } else {
            lines.add("This command requires an argument!");
            return;
        }

        lines.add(target.get().getNick() + " has " + Main.spamFilter.getStrikeCount(target.get()) + " strikes.");
        
        if (IRCUtils.isPermLevelAboveOrEqualTo(PermRegistry.INSTANCE.getPermLevelForUser(channel, user), PermLevel.TRUSTED)) {
            List<Strike> strikes = Main.spamFilter.getStrikes(user);
            BotMode mode = BotMode.NOTICE;
            if (strikes.size() > 5) {
                mode = BotMode.PM;
            }
            for (int i = 0; i < strikes.size(); i++) {
                Strike s = strikes.get(i);
                IRCUtils.modeSensitiveEnqueue(bot, user, channel, (i + 1) + " - Reason: " + s.getReason() + " - Message: " + s.getMessage(), mode);
            }
        }
    }
}
