package tterrag.tppibot.runnables;

import java.util.LinkedList;
import java.util.Queue;

import org.pircbotx.PircBotX;

import tterrag.tppibot.Main;
import tterrag.tppibot.util.Message;
import tterrag.tppibot.util.ThreadUtils;

public class MessageSender implements Runnable
{
    public static final MessageSender instance = new MessageSender();
    
    public final Queue<Message> toSend = new LinkedList<Message>();
    
    @Override
    public void run()
    {
        while (!Main.bot.isConnected())
        {
            ThreadUtils.sleep(1000);
        }
        
        while (true)
        {
            ThreadUtils.sleep(100);
            Message send = toSend.poll();
            if (send != null)
            {
                send.send();
            }
        }
    }
    
    public void enqueue(PircBotX bot, String to, String message)
    {
        this.toSend.add(new Message(bot, to, message));
    }
    
    public void enqueueNotice(PircBotX bot, String to, String message)
    {
        this.toSend.add(new Message(bot, to, message).setNotice());
    }
    
    public void enqueueAction(PircBotX bot, String to, String message)
    {
        this.toSend.add(new Message(bot, to, message).setAction());
    }
}
