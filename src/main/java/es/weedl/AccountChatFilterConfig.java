package es.weedl;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("accountChatFilter")
public interface AccountChatFilterConfig extends Config
{
	@ConfigSection(
			name = "General",
			description = "General settings",
			position = 10
	)
	String generalSection = "general";

	@ConfigSection(
			name = "Filtered Account Types",
			description = "Account types to filter",
			position = 20
	)
	String accountTypesSection = "accountTypes";

	@ConfigItem(
			keyName = "filterSelf",
			name = "Filter Self",
			description = "Whether or not to filter yourself if you match a filtered group",
			position = 0,
			section = generalSection
	)
	default boolean filterSelf() { return false; }

	@ConfigItem(
			keyName = "filterFriends",
			name = "Filter Friends",
			description = "Whether or not to filter your friends if they match a filtered group",
			position = 1,
			section = generalSection
	)
	default boolean filterFriends() { return false; }

	@ConfigItem(
			keyName = "filterFriendsChat",
			name = "Filter Friends Chat Members",
			description = "Whether or not to filter members of your current friends chat if they match a filtered group",
			position = 2,
			section = generalSection
	)
	default boolean filterFriendsChat() { return true; }

	@ConfigItem(
			keyName = "filterClanChat",
			name = "Filter Chat Chat Members",
			description = "Whether or not to filter members of your current clan chat if they match a filtered group",
			position = 3,
			section = generalSection
	)
	default boolean filterClanChat() { return true; }

	@ConfigItem(
			keyName = "onlyFilterIcons",
			name = "Only Filter Icons",
			description = "Removes the specified account icons instead of the user's message (toggling will not re-add icons)",
			position = 4,
			section = generalSection
	)
	default boolean onlyFilterIcons() { return false; }

	@ConfigItem(
		keyName = "filterNormalAccounts",
		name = "Normal Accounts",
		description = "Filters normal accounts",
		position = 20,
		section = accountTypesSection
	)
	default boolean filterNormalAccounts() { return false; }

	@ConfigItem(
		keyName = "filterIronmen",
		name = "Ironmen",
		description = "Filters standard ironman accounts",
		position = 21,
		section = accountTypesSection
	)
	default boolean filterIronmen() { return false; }

	@ConfigItem(
		keyName = "filterHardcoreIronmen",
		name = "Hardcore Ironmen",
		description = "Filters hardcore ironman accounts",
		position = 22,
		section = accountTypesSection
	)
	default boolean filterHardcoreIronmen() { return false; }

	@ConfigItem(
		keyName = "filterUltimateIronmen",
		name = "Ultimate Ironmen",
		description = "Filters ultimate ironman accounts",
		position = 23,
		section = accountTypesSection
	)
	default boolean filterUltimateIronmen() { return false; }

	@ConfigItem(
		keyName = "filterGroupIronmen",
		name = "Group Ironmen",
		description = "Filters group ironman accounts",
		position = 24,
		section = accountTypesSection
	)
	default boolean filterGroupIronmen() { return false; }

	@ConfigItem(
		keyName = "filterHardcoreGroupIronmen",
		name = "Hardcore Group Ironmen",
		description = "Filters hardcore group ironman accounts",
		position = 25,
		section = accountTypesSection
	)
	default boolean filterHardcoreGroupIronmen() { return false; }

	@ConfigItem(
			keyName = "filterLeagueAccount",
			name = "League Accounts",
			description = "Filters league accounts",
			position = 26,
			section = accountTypesSection
	)
	default boolean filterLeague() { return false; }
}
