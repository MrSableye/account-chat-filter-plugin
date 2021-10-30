package es.weedl;

import com.google.inject.Provides;
import java.util.*;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.clan.ClanChannel;
import net.runelite.api.events.OverheadTextChanged;
import net.runelite.api.events.ScriptCallbackEvent;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.Text;

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

	private final Set<AccountIconID> filteredAccountTypes = new HashSet<>();

	private final Map<Integer, String> originalNames = new HashMap<>();

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

	protected void updateFilteredAccounts()
	{
		addOrRemove(AccountIconID.IRONMAN, config.filterIronmen());
		addOrRemove(AccountIconID.HARDCORE_IRONMAN, config.filterHardcoreIronmen());
		addOrRemove(AccountIconID.ULTIMATE_IRONMAN, config.filterUltimateIronmen());
		addOrRemove(AccountIconID.GROUP_IRONMAN, config.filterGroupIronmen());
		addOrRemove(AccountIconID.HARDCORE_GROUP_IRONMAN, config.filterHardcoreGroupIronmen());
		addOrRemove(AccountIconID.LEAGUE, config.filterLeague());
	}

	@Subscribe
	public void onConfigChanged(final ConfigChanged event)
	{
		if (!"accountChatFilter".equals(event.getGroup()))
		{
			return;
		}

		updateFilteredAccounts();

		if (!config.onlyFilterIcons())
		{
			resetNames();
		}

		//Refresh chat after config change to reflect current rules
		client.refreshChat();
	}

	private void resetNames()
	{
		for (final Integer messageId : originalNames.keySet())
		{
			final String originalName = originalNames.get(messageId);
			final MessageNode messageNode = client.getMessages().get(messageId);
			messageNode.setName(originalName);
		}
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
		final String name = originalNames.getOrDefault(messageId, messageNode.getName());

		boolean shouldFilter = shouldFilter(name);

		if (shouldFilter)
		{
			if (config.onlyFilterIcons())
			{
				originalNames.put(messageId, name);
				messageNode.setName(filterIcons(name));
				client.refreshChat();
			}
			else
			{
				boolean blockMessage = isBlockableMessageType(chatMessageType);

				if (blockMessage)
				{
					// Block the message
					intStack[intStackSize - 3] = 0;
				}
			}
		}
		else if (originalNames.containsKey(messageId))
		{
			messageNode.setName(originalNames.remove(messageId));
			client.refreshChat();
		}
	}

	private boolean isBlockableMessageType(final ChatMessageType chatMessageType)
	{
		switch (chatMessageType) {
			case PUBLICCHAT:
			case MODCHAT:
			case AUTOTYPER:
			case PRIVATECHAT:
			case MODPRIVATECHAT:
			case FRIENDSCHAT:
			case CLAN_CHAT:
			case CLAN_GUEST_CHAT:
				return true;
		}

		return false;
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

	protected String filterIcons(final String name)
	{
		String filteredName = name;
		for (final AccountIconID accountIconID : filteredAccountTypes)
		{
			filteredName = filteredName.replace(accountIconID.toString(), "");
		}
		return filteredName;
	}

	private boolean isSelf(final String name)
	{
		return Text.standardize(name).equals(Text.standardize(client.getLocalPlayer().getName()));
	}

	private boolean isFriend(final String name)
	{
		return client.isFriended(name, false);
	}

	private boolean isFriendsChatMember(final String name)
	{
		final FriendsChatManager friendsChatManager = client.getFriendsChatManager();
		return friendsChatManager != null && friendsChatManager.findByName(name) != null;
	}

	private boolean isClanChatMember(final String name)
	{
		ClanChannel clanChannel = client.getClanChannel();
		if (clanChannel != null && clanChannel.findMember(name) != null)
		{
			return true;
		}

		clanChannel = client.getGuestClanChannel();
		return clanChannel != null && clanChannel.findMember(name) != null;
	}

	private boolean shouldSkipFilters(final String name)
	{
		if (!config.filterSelf() && isSelf(name))
		{
			return true;
		}
		else if (!config.filterFriends() && isFriend(name))
		{
			return true;
		}
		else if (!config.filterFriendsChat() && isFriendsChatMember(name))
		{
			return true;
		}
		else if (!config.filterClanChat() && isClanChatMember(name))
		{
			return true;
		}

		return false;
	}

	private boolean isFilteredAccountType(final String name)
	{
		return filteredAccountTypes.stream().anyMatch(
				(accountIconID -> name.contains(accountIconID.toString()))
		);
	}

	private boolean isNormalAccount(final String name)
	{
		return Arrays.stream(AccountIconID.values()).noneMatch(
				(accountIconID -> name.contains(accountIconID.toString()))
		);
	}

	protected boolean shouldFilter(final String name)
	{
		if (shouldSkipFilters(Text.removeTags(name)))
		{
			return false;
		}

		if (isFilteredAccountType(name))
		{
			return true;
		}

		return config.filterNormalAccounts() && isNormalAccount(name);
	}
}
