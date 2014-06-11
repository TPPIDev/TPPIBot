package tterrag.tppibot.runnables;

import java.util.Scanner;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import tterrag.tppibot.Main;
import tterrag.tppibot.interfaces.ICommand;
import tterrag.tppibot.registry.CommandRegistry;
import tterrag.tppibot.util.ThreadUtils;

public class ConsoleCommands implements Runnable
{
    @Override
    public void run()
    {
        Scanner scan = new Scanner(System.in);

        while (!Main.bot.isConnected())
        {
            ThreadUtils.sleep(1000);
        }

        boolean connected = true;

        while (connected)
        {
            String cmd = scan.nextLine();
            String[] args = StringUtils.split(cmd, ' ');

            if (cmd.toLowerCase().startsWith("say") && args.length > 2)
            {
                Main.bot.sendIRC().message("#" + args[1], StringUtils.join(ArrayUtils.remove(ArrayUtils.remove(args, 0), 0), ' '));
            }
            else
            {
                for (ICommand c : CommandRegistry.getCommands())
                {
                    if (cmd.toLowerCase().startsWith(c.getIdent().toLowerCase()))
                    {
                        c.handleConsoleCommand(ArrayUtils.remove(cmd.split(" "), 0));
                    }
                }
            }

            connected = Main.bot.isConnected();
        }

        scan.close();
    }
}
