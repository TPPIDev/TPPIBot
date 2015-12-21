package tterrag.tppibot.runnables;

import java.util.Scanner;

import org.apache.commons.lang3.ArrayUtils;

import tterrag.tppibot.Main;
import tterrag.tppibot.registry.CommandRegistry;
import tterrag.tppibot.util.ThreadUtils;

public class ConsoleCommands implements Runnable {

    @Override
    public void run() {
        Scanner scan = new Scanner(System.in);

        while (!Main.bot.isConnected()) {
            ThreadUtils.sleep(1000);
        }

        boolean connected = true;

        while (connected) {
            try {
                String cmd = scan.nextLine();

                CommandRegistry.INSTANCE.getCommands().stream().filter(c -> cmd.toLowerCase().startsWith(c.getIdent().toLowerCase()))
                        .forEach(c -> c.handleConsoleCommand(ArrayUtils.remove(cmd.split(" "), 0)));

                connected = Main.bot.isConnected();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

        scan.close();
    }
}
