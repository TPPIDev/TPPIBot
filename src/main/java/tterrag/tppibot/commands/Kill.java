package tterrag.tppibot.commands;

public class Kill extends Command
{
    public Kill()
    {
        super("kill", PermLevel.OP);
    }

    @Override
    public boolean onCommand(String channel, String user, String... args)
    {
        sendMessage(channel, "NOOOOooooo...");
        
        try
        {
            Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        
        System.exit(0);
        return true;
    }
}
