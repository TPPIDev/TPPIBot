package tterrag.tppibot.commands;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

import tterrag.tppibot.registry.PermRegistry;
import tterrag.tppibot.runnables.MessageSender;
import tterrag.tppibot.util.IRCUtils;

public class Say extends Command {

    public Say() {
        super("say", PermLevel.TRUSTED);
    }

    @Override
    public void onCommand(PircBotX bot, User user, Channel channel, List<String> lines, String... args) {
        if (args.length < 1) {
            lines.add("This command requires at least one arg.");
        }

        String potentialChannel = IRCUtils.fmtChan(args[args.length - 1]);
        Channel sayChan = IRCUtils.getChannelByName(bot, potentialChannel).orElse(null); // meh

        if (sayChan != null) {
            args = ArrayUtils.remove(args, args.length - 1);
        }

        String text = StringUtils.join(args, " ");

        if (sayChan == null || IRCUtils.isPermLevelAboveOrEqualTo(PermRegistry.INSTANCE.getPermLevelForUser(sayChan, user), PermLevel.TRUSTED)) {
            MessageSender.INSTANCE.enqueue(bot, sayChan == null ? channel == null ? user.getNick() : channel.getName() : sayChan.getName(), text);
        } else {
            lines.add("You must be trusted or higher in that channel.");
        }
    }
}
