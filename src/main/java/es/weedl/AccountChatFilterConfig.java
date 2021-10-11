package es.weedl;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("accountChatFilter")
public interface AccountChatFilterConfig extends Config
{
	@ConfigItem(
		keyName = "filterNormalAccounts",
		name = "Filter Normal Accounts",
		description = "Filters normal accounts",
		position = 0
	)
	default boolean filterNormalAccounts() { return false; }

	@ConfigItem(
		keyName = "filterIronmen",
		name = "Filter Ironmen",
		description = "Filters standard ironman accounts",
		position = 1
	)
	default boolean filterIronmen() { return false; }

	@ConfigItem(
		keyName = "filterHardcoreIronmen",
		name = "Filter Hardcore Ironmen",
		description = "Filters hardcore ironman accounts",
		position = 2
	)
	default boolean filterHardcoreIronmen() { return false; }

	@ConfigItem(
		keyName = "filterUltimateIronmen",
		name = "Filter Ultimate Ironmen",
		description = "Filters ultimate ironman accounts",
		position = 3
	)
	default boolean filterUltimateIronmen() { return false; }

	@ConfigItem(
		keyName = "filterGroupIronmen",
		name = "Filter Group Ironmen",
		description = "Filters group ironman accounts",
		position = 4
	)
	default boolean filterGroupIronmen() { return false; }

	@ConfigItem(
		keyName = "filterHardcoreGroupIronmen",
		name = "Filter Hardcore Group Ironmen",
		description = "Filters hardcore group ironman accounts",
		position = 5
	)
	default boolean filterHardcoreGroupIronmen() { return false; }
}
