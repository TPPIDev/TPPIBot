package tterrag.tppibot.commands;

import org.pircbotx.hooks.events.MessageEvent;

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
    public boolean onCommand(MessageEvent<?> event, String... args)
    {
        IRCUtils.sendMessageForUser(event.getChannel(), event.getUser(), message, args);
        return true;
    }

    @Override
    public String getDesc()
    {
        return "A custom command that was added by 'addcmd'. Output text is: \"" + this.message + ".\"";
    }
}
