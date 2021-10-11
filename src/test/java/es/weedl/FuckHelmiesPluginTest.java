package es.weedl;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class FuckHelmiesPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(FuckHelmiesPlugin.class);
		RuneLite.main(args);
	}
}