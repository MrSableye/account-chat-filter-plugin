package es.weedl;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.OverheadTextChanged;
import net.runelite.api.events.ScriptCallbackEvent;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.Text;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@PluginDescriptor(
	name = "Fuck Helmies"
)
public class FuckHelmiesPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private FuckHelmiesConfig config;

	private Set<HelmieIconID> filteredHelmies = new HashSet<>();

	@Provides
	final FuckHelmiesConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(FuckHelmiesConfig.class);
	}

	private void addOrRemove(final HelmieIconID helmieIconID, final boolean isAdd)
	{
		if (isAdd)
		{
			filteredHelmies.add(helmieIconID);
		}
		else
		{
			filteredHelmies.remove(helmieIconID);
		}
	}

	private void updateFilteredHelmies()
	{
		addOrRemove(HelmieIconID.IRONMAN, config.filterIronmen());
		addOrRemove(HelmieIconID.HARDCORE_IRONMAN, config.filterHardcoreIronmen());
		addOrRemove(HelmieIconID.ULTIMATE_IRONMAN, config.filterUltimateIronmen());
		addOrRemove(HelmieIconID.GROUP_IRONMAN, config.filterGroupIronmen());
		addOrRemove(HelmieIconID.HARDCORE_GROUP_IRONMAN, config.filterHardcoreGroupIronmen());
	}

	@Subscribe
	public void onConfigChanged(final ConfigChanged event)
	{
		if (!"fuckHelmies".equals(event.getGroup()))
		{
			return;
		}

		updateFilteredHelmies();

		//Refresh chat after config change to reflect current rules
		client.refreshChat();
	}


	@Subscribe
	public void onScriptCallbackEvent(final ScriptCallbackEvent event)
	{
		if (!"chatFilterCheck".equals(event.getEventName()))
		{
			return;
		}

		int[] intStack = client.getIntStack();
		int intStackSize = client.getIntStackSize();

		final int messageType = intStack[intStackSize - 2];
		final int messageId = intStack[intStackSize - 1];

		final ChatMessageType chatMessageType = ChatMessageType.of(messageType);
		final MessageNode messageNode = client.getMessages().get(messageId);
		final String name = messageNode.getName();
		boolean blockMessage = false;

		// Only filter public chat and private messages
		switch (chatMessageType)
		{
			case PUBLICCHAT:
			case MODCHAT:
			case AUTOTYPER:
			case PRIVATECHAT:
			case MODPRIVATECHAT:
			case FRIENDSCHAT:
			case CLAN_CHAT:
			case CLAN_GUEST_CHAT:
				blockMessage = shouldFilter(name);
				break;
		}

		if (blockMessage)
		{
			// Block the message
			intStack[intStackSize - 3] = 0;
		}
	}

	@Subscribe
	public void onOverheadTextChanged(final OverheadTextChanged event)
	{
		if (!(event.getActor() instanceof Player))
		{
			return;
		}

		String message = event.getOverheadText();

		if (shouldFilter(event.getActor().getName()))
		{
			message = " ";
		}

		event.getActor().setOverheadText(message);
	}

	private boolean shouldSkipFilters(final String name)
	{
		if (Text.standardize(name).equals(Text.standardize(client.getLocalPlayer().getName())))
		{
			return true;
		}

		return client.isFriended(Text.toJagexName(name), false);
	}

	private boolean shouldFilter(final String name)
	{
		if (shouldSkipFilters(name))
		{
			return false;
		}

		boolean isFilteredHelmie = filteredHelmies.stream().anyMatch(
				(helmieIconID -> name.contains(helmieIconID.toString()))
		);

		if (isFilteredHelmie)
		{
			return true;
		}

		if (config.filterBondies())
		{
			return Arrays.stream(HelmieIconID.values()).noneMatch(
					(helmieIconID -> name.contains(helmieIconID.toString()))
			);
		}

		return false;
	}
}
