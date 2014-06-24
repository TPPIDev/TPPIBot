package tterrag.tppibot.registry;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.events.DisconnectEvent;

import tterrag.tppibot.annotations.Subscribe;
import tterrag.tppibot.config.Config;
import tterrag.tppibot.interfaces.ICommand.PermLevel;
import tterrag.tppibot.util.IRCUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class PermRegistry
{
    /**
     * {@link Channel} -> ({@link User} -> {@link PermLevel})
     */
    private Map<String, Map<String, PermLevel>> registrar;

    private Set<String> controllers;

    private Config registrarConfig, controllersConfig;

    public static final String[] defaultControllers = new String[] { "tterrag" };

    public PermRegistry()
    {
        registrarConfig = new Config("permRegistry.json");
        controllersConfig = new Config("controllers.json");

        registrar = new Gson().fromJson(registrarConfig.getText(), new TypeToken<Map<String, Map<String, PermLevel>>>() {}.getType());
        controllers = new Gson().fromJson(controllersConfig.getText(), new TypeToken<Set<String>>() {}.getType());

        registrar = registrar == null ? new HashMap<String, Map<String, PermLevel>>() : registrar;
        controllers = controllers == null ? new HashSet<String>() : controllers;

        if (controllers.isEmpty())
            controllers.addAll(Arrays.asList(defaultControllers));
    }

    private static final PermRegistry INSTANCE = new PermRegistry();

    public static PermRegistry instance()
    {
        return INSTANCE;
    }

    public void registerUser(Channel chan, User user, PermLevel level)
    {
        String acct = IRCUtils.getAccount(user);
        
        // controllers are global
        if (level == PermLevel.CONTROLLER)
        {
            controllers.add(acct);
        }
        else
        {
            controllers.remove(acct);
            
            // can't assign op/voice
            if (!ArrayUtils.contains(PermLevel.getSettablePermLevels(), level))
                throw new IllegalArgumentException("Cannot register a user with the level " + level.toString());

            String chanName = chan.getName();

            registrar.put(chanName, register(registrar.get(chanName), acct, level));
        }
    }

    private Map<String, PermLevel> register(Map<String, PermLevel> curChanMap, String acct, PermLevel level)
    {
        if (curChanMap == null)
            curChanMap = new HashMap<String, PermLevel>();

        curChanMap.put(acct, level);
        return curChanMap;
    }

    public PermLevel getPermLevelForUser(Channel chan, User user)
    {
        String acct = IRCUtils.getAccount(user);

        if (controllers.contains(acct))
            return PermLevel.CONTROLLER;

        if (!registrar.containsKey(chan.getName()))
            return PermLevel.DEFAULT;

        PermLevel perm = registrar.get(chan.getName()).get(acct);
        return perm == null ? PermLevel.DEFAULT : perm;
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent<PircBotX> event)
    {
        registrarConfig.writeJsonToFile(registrar);
        controllersConfig.writeJsonToFile(controllers);
    }
}
