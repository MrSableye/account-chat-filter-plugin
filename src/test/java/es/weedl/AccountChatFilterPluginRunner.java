package es.weedl;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class AccountChatFilterPluginRunner
{
    public static void main(String[] args) throws Exception
    {
        ExternalPluginManager.loadBuiltin(AccountChatFilterPlugin.class);
        RuneLite.main(args);
    }
}
