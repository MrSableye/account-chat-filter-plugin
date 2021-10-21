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
	name = "Account Chat Filter"
)
public class AccountChatFilterPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private AccountChatFilterConfig config;

	private Set<AccountIconID> filteredAccountTypes = new HashSet<>();

	@Provides
	final AccountChatFilterConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(AccountChatFilterConfig.class);
	}

	private void addOrRemove(final AccountIconID accountIconID, final boolean isAdd)
	{
		if (isAdd)
		{
			filteredAccountTypes.add(accountIconID);
		}
		else
		{
			filteredAccountTypes.remove(accountIconID);
		}
	}

	private void updateFilteredAccounts()
	{
		addOrRemove(AccountIconID.IRONMAN, config.filterIronmen());
		addOrRemove(AccountIconID.HARDCORE_IRONMAN, config.filterHardcoreIronmen());
		addOrRemove(AccountIconID.ULTIMATE_IRONMAN, config.filterUltimateIronmen());
		addOrRemove(AccountIconID.GROUP_IRONMAN, config.filterGroupIronmen());
		addOrRemove(AccountIconID.HARDCORE_GROUP_IRONMAN, config.filterHardcoreGroupIronmen());
	}

	@Subscribe
	public void onConfigChanged(final ConfigChanged event)
	{
		if (!"accountChatFilter".equals(event.getGroup()))
		{
			return;
		}

		updateFilteredAccounts();

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

		boolean isFilteredAccountType = filteredAccountTypes.stream().anyMatch(
				(accountIconID -> name.contains(accountIconID.toString()))
		);

		if (isFilteredAccountType)
		{
			return true;
		}

		if (config.filterNormalAccounts())
		{
			return Arrays.stream(AccountIconID.values()).noneMatch(
					(accountIconID -> name.contains(accountIconID.toString()))
			);
		}

		return false;
	}
}