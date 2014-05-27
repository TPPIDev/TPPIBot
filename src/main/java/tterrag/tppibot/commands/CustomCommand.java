package tterrag.tppibot.commands;

import org.pircbotx.Channel;
import org.pircbotx.User;

import tterrag.tppibot.util.IRCUtils;

public class CustomCommand extends Command
{
    private String message;
    
    public CustomCommand(String ident, PermLevel perms, String message)
    {
        super(ident, perms);
        this.message = message;
    }

    @Override
    public boolean onCommand(Channel channel, User user, String... args)
    {
        IRCUtils.sendMessageForUser(channel, user, message, args);
        return true;
    }
}
