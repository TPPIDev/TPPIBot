package tterrag.tppibot.commands;

import org.apache.commons.lang3.StringUtils;
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
    public Command editCommand(String... params)
    {
        AddCommand.commandsAdded.remove(this);

        if (params.length > 0 && params[0].startsWith("-permLevel="))
        {
            this.setPermLevel(PermLevel.valueOf(params[0].substring(10)));
        }
        else
        {
            this.message = StringUtils.join(params, " ");
        }
        
        AddCommand.commandsAdded.add(this);
        return this;
    }

    @Override
    public String getDesc()
    {
        return "A custom command that was added by 'addcmd'. Output text is: \"" + this.message + ".\"";
    }
}
