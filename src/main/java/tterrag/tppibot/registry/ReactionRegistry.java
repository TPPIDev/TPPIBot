package tterrag.tppibot.registry;

import java.util.ArrayList;
import java.util.List;

import tterrag.tppibot.interfaces.IReaction;

public class ReactionRegistry
{
    private static ArrayList<IReaction> reactions = new ArrayList<IReaction>();

    public static void registerReaction(IReaction r)
    {
        reactions.add(r);
    }

    public static List<IReaction> getReactions()
    {
        return reactions;
    }
}
