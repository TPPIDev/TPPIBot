package tterrag.tppibot.commands;

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;

import tterrag.tppibot.Main;

public class EditCommand extends Command
{
    public EditCommand()
    {
        super("editcmd", PermLevel.OP);
    }

    @Override
    public boolean onCommand(String channel, String user, String... args)
    {
        if (args.length < 2)
        {
            sendNotice(user, "This requires 2 args: [command] and [edit args]");
            return false;
        }
        
        for (Command c : Main.getBot().getCommands())
        {
            String cmdName = args[0];
                    
            if (c.getName().equalsIgnoreCase(cmdName))
            {
                args = ArrayUtils.remove(args, 0);
                
                sendNotice(user, "Editing command " + c.getName() + " with args " + Arrays.deepToString(args));
                c.editCommand(args);
            }
        }
        
        return true;
    }
}
