package tterrag.tppibot.reactions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

import tterrag.tppibot.Main;
import tterrag.tppibot.interfaces.IReaction;
import tterrag.tppibot.reactions.CharacterSpam.SpamReasons;
import tterrag.tppibot.util.ThreadUtils;

public class FloodSpam implements IReaction
{
    public static final int EXPIRE_TIME = 10000; // ms
    public static final int MAX_MSGS = 5;

    public static class MessageCount
    {
        private class Message
        {
            private long time;

            private Message(long time)
            {
                this.time = time;
            }
        }

        public final User user;
        public final Channel channel;

        private List<Message> msgs;

        public MessageCount(User user, Channel channel)
        {
            this.user = user;
            this.channel = channel;

            msgs = new ArrayList<Message>();
            msgs.add(new Message(System.currentTimeMillis()));
        }

        public void msg()
        {
            msgs.add(new Message(System.currentTimeMillis()));
        }

        public void tick()
        {
            Iterator<Message> iter = msgs.iterator();
            while (iter.hasNext())
            {
                Message m = iter.next();
                if (System.currentTimeMillis() - m.time > EXPIRE_TIME)
                {
                    iter.remove();
                }
            }
        }

        public boolean isValid()
        {
            return msgs.size() > 1;
        }

        public boolean equals(MessageEvent<?> event)
        {
            return this.user.equals(event.getUser()) && this.channel.equals(event.getChannel());
        }

        public boolean breakinDaLaw()
        {
            return msgs.size() >= MAX_MSGS;
        }
    }

    private List<MessageCount> counts = new ArrayList<MessageCount>();

    private Runnable ticker = new Runnable() {

        @Override
        public void run()
        {
            while (true)
            {
                for (MessageCount c : counts)
                {
                    c.tick();
                }
                ThreadUtils.sleep(1000);
            }
        }
    };

    public FloodSpam()
    {
        Thread thread = new Thread(ticker);
        thread.start();
    }

    @Override
    public void onMessage(MessageEvent<?> event)
    {
        boolean found = false;
        Iterator<MessageCount> iter = counts.iterator();

        if (Main.spamFilter.filtersEnabled(event.getChannel().getName()))
        {
            while (iter.hasNext())
            {
                MessageCount count = iter.next();
                if (count.equals(event))
                {
                    count.msg();
                    found = true;
                    if (count.breakinDaLaw())
                    {
                        timeout(count);
                        iter.remove();
                    }
                    break;
                }
            }

            if (!found)
            {
                counts.add(new MessageCount(event.getUser(), event.getChannel()));
            }
        }
    }

    private void timeout(MessageCount msg)
    {
        Main.spamFilter.finish(Main.spamFilter.timeout(Main.bot, msg.user, msg.channel, SpamReasons.FLOOD) ? msg.user : null);
    }
}
