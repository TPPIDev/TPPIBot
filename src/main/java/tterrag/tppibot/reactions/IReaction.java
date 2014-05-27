package tterrag.tppibot.reactions;

import org.pircbotx.hooks.events.MessageEvent;

public interface IReaction
{
    public void onMessage(MessageEvent<?> event);
}
