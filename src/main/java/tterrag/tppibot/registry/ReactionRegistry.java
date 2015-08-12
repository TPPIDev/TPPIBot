package tterrag.tppibot.registry;

import java.util.ArrayList;
import java.util.List;

import tterrag.tppibot.interfaces.IReaction;

public enum ReactionRegistry
{
    INSTANCE;
    
    private ArrayList<IReaction> reactions = new ArrayList<IReaction>();

    public void registerReaction(IReaction r)
    {
        reactions.add(r);
    }

    public List<IReaction> getReactions()
    {
        return reactions;
    }
}
