package tterrag.tppibot.util;

import org.pircbotx.PircBotX;

import tterrag.tppibot.Main;

public class Message
{    
    public String to, message;
    
    private boolean sent = false;
    private boolean notice = false;
    
    private PircBotX bot;
    
    public Message(PircBotX bot, String to, String message)
    {
        this.bot = bot;
        this.to = to;
        this.message = message;
    }
    
    public Message setNotice()
    {
        this.notice = true;
        return this;
    }
    
    public boolean hasSent()
    {
        return sent;
    }
    
    public void send()
    {
        if (notice)
        {
            Main.bot.sendIRC().notice(to, message);
        }
        else
        {
            bot.sendIRC().message(to, message);
        }
        
        sent = true;
    }
}
