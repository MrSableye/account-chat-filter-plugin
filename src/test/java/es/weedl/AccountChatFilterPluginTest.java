package es.weedl;

import com.google.inject.Guice;
import com.google.inject.testing.fieldbinder.Bind;
import javax.inject.Inject;
import com.google.inject.testing.fieldbinder.BoundFieldModule;
import net.runelite.api.*;
import net.runelite.api.clan.ClanChannel;
import net.runelite.api.clan.ClanChannelMember;
import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AccountChatFilterPluginTest
{
	private static final String TEST_ACCOUNT_NAME_1 = "Zezima";
	private static final String TEST_ACCOUNT_NAME_2 = "Kate Micucci";

	@Inject
	private AccountChatFilterPlugin accountChatFilterPlugin;

	@Mock
	@Bind
	private Client client;

	@Mock
	@Bind
	private AccountChatFilterConfig accountChatFilterConfig;

	@Before
	public void before()
	{
		Guice.createInjector(BoundFieldModule.of(this)).injectMembers(this);
	}

	@Test
	public void filtersNormalAccountWhenEnabled()
	{
		mockLocalPlayer(TEST_ACCOUNT_NAME_2);
		when(accountChatFilterConfig.filterNormalAccounts()).thenReturn(true);
		accountChatFilterPlugin.updateFilteredAccounts();

		assertTrue(accountChatFilterPlugin.shouldFilter(TEST_ACCOUNT_NAME_1));
	}

	@Test
	public void filtersIronmenWhenEnabled()
	{
		mockLocalPlayer(TEST_ACCOUNT_NAME_2);
		when(accountChatFilterConfig.filterIronmen()).thenReturn(true);
		accountChatFilterPlugin.updateFilteredAccounts();

		assertTrue(accountChatFilterPlugin.shouldFilter(getTestName(AccountIconID.IRONMAN, TEST_ACCOUNT_NAME_1)));
	}

	@Test
	public void filtersHardcoreIronmenWhenEnabled()
	{
		mockLocalPlayer(TEST_ACCOUNT_NAME_2);
		when(accountChatFilterConfig.filterHardcoreIronmen()).thenReturn(true);
		accountChatFilterPlugin.updateFilteredAccounts();

		assertTrue(accountChatFilterPlugin.shouldFilter(getTestName(AccountIconID.HARDCORE_IRONMAN, TEST_ACCOUNT_NAME_1)));
	}

	@Test
	public void filtersUltimateIronmenWhenEnabled()
	{
		mockLocalPlayer(TEST_ACCOUNT_NAME_2);
		when(accountChatFilterConfig.filterUltimateIronmen()).thenReturn(true);
		accountChatFilterPlugin.updateFilteredAccounts();

		assertTrue(accountChatFilterPlugin.shouldFilter(getTestName(AccountIconID.ULTIMATE_IRONMAN, TEST_ACCOUNT_NAME_1)));
	}

	@Test
	public void filtersGroupIronmenWhenEnabled()
	{
		mockLocalPlayer(TEST_ACCOUNT_NAME_2);
		when(accountChatFilterConfig.filterGroupIronmen()).thenReturn(true);
		accountChatFilterPlugin.updateFilteredAccounts();

		assertTrue(accountChatFilterPlugin.shouldFilter(getTestName(AccountIconID.GROUP_IRONMAN, TEST_ACCOUNT_NAME_1)));
	}

	@Test
	public void filtersHardcoreGroupIronmenWhenEnabled()
	{
		mockLocalPlayer(TEST_ACCOUNT_NAME_2);
		when(accountChatFilterConfig.filterHardcoreGroupIronmen()).thenReturn(true);
		accountChatFilterPlugin.updateFilteredAccounts();

		assertTrue(accountChatFilterPlugin.shouldFilter(getTestName(AccountIconID.HARDCORE_GROUP_IRONMAN, TEST_ACCOUNT_NAME_1)));
	}

	@Test
	public void filtersLeagueAccountWhenEnabled()
	{
		mockLocalPlayer(TEST_ACCOUNT_NAME_2);
		when(accountChatFilterConfig.filterLeague()).thenReturn(true);
		accountChatFilterPlugin.updateFilteredAccounts();

		assertTrue(accountChatFilterPlugin.shouldFilter(getTestName(AccountIconID.LEAGUE, TEST_ACCOUNT_NAME_1)));
	}

	@Test
	public void skipsSelfWhenDisabled()
	{
		mockLocalPlayer(TEST_ACCOUNT_NAME_1);
		when(accountChatFilterConfig.filterSelf()).thenReturn(false);
		when(accountChatFilterConfig.filterIronmen()).thenReturn(true);
		accountChatFilterPlugin.updateFilteredAccounts();

		assertFalse(accountChatFilterPlugin.shouldFilter(getTestName(AccountIconID.IRONMAN, TEST_ACCOUNT_NAME_1)));
	}

	@Test
	public void skipsFriendWhenDisabled()
	{
		mockLocalPlayer(TEST_ACCOUNT_NAME_2);
		mockIsFriend(TEST_ACCOUNT_NAME_1, true);
		when(accountChatFilterConfig.filterFriends()).thenReturn(false);
		when(accountChatFilterConfig.filterIronmen()).thenReturn(true);
		accountChatFilterPlugin.updateFilteredAccounts();

		assertFalse(accountChatFilterPlugin.shouldFilter(getTestName(AccountIconID.IRONMAN, TEST_ACCOUNT_NAME_1)));
	}

	@Test
	public void skipsFriendsChatWhenDisabled()
	{
		mockLocalPlayer(TEST_ACCOUNT_NAME_2);
		mockIsFriendChatMember(TEST_ACCOUNT_NAME_1);
		when(accountChatFilterConfig.filterFriendsChat()).thenReturn(false);
		when(accountChatFilterConfig.filterIronmen()).thenReturn(true);
		accountChatFilterPlugin.updateFilteredAccounts();

		assertFalse(accountChatFilterPlugin.shouldFilter(getTestName(AccountIconID.IRONMAN, TEST_ACCOUNT_NAME_1)));
	}

	@Test
	public void skipsClanChatWhenDisabled()
	{
		mockLocalPlayer(TEST_ACCOUNT_NAME_2);
		mockIsClanChatMember(TEST_ACCOUNT_NAME_1, false);
		when(accountChatFilterConfig.filterClanChat()).thenReturn(false);
		when(accountChatFilterConfig.filterIronmen()).thenReturn(true);
		accountChatFilterPlugin.updateFilteredAccounts();

		assertFalse(accountChatFilterPlugin.shouldFilter(getTestName(AccountIconID.IRONMAN, TEST_ACCOUNT_NAME_1)));
	}

	@Test
	public void skipsGuestClanChatWhenDisabled()
	{
		mockLocalPlayer(TEST_ACCOUNT_NAME_2);
		mockIsClanChatMember(TEST_ACCOUNT_NAME_1, true);
		when(accountChatFilterConfig.filterClanChat()).thenReturn(false);
		when(accountChatFilterConfig.filterIronmen()).thenReturn(true);
		accountChatFilterPlugin.updateFilteredAccounts();

		assertFalse(accountChatFilterPlugin.shouldFilter(getTestName(AccountIconID.IRONMAN, TEST_ACCOUNT_NAME_1)));
	}

	private void mockLocalPlayer(final String playerName)
	{
		final Player player = mock(Player.class);
		when(player.getName()).thenReturn(playerName);
		when(client.getLocalPlayer()).thenReturn(player);
	}

	private void mockIsFriend(final String playerName, final boolean isFriend)
	{
		when(client.isFriended(eq(" " + playerName), eq(false))).thenReturn(isFriend);
	}

	private void mockIsFriendChatMember(final String playerName)
	{
		final FriendsChatManager friendsChatManager = mock(FriendsChatManager.class);
		final FriendsChatMember friendsChatMember = mock(FriendsChatMember.class);
		when(friendsChatManager.findByName(eq(" " + playerName))).thenReturn(friendsChatMember);
		when(client.getFriendsChatManager()).thenReturn(friendsChatManager);
	}

	private void mockIsClanChatMember(final String playerName, final boolean isGuestClanChannelMember)
	{
		final ClanChannelMember clanChannelMember = mock(ClanChannelMember.class);
		final ClanChannel clanChannel = mock(ClanChannel.class);
		when(clanChannel.findMember(eq(" " + playerName))).thenReturn(clanChannelMember);

		if (isGuestClanChannelMember)
		{
			when(client.getClanChannel()).thenReturn(null);
			when(client.getGuestClanChannel()).thenReturn(clanChannel);
		}
		else
		{
			when(client.getClanChannel()).thenReturn(clanChannel);
		}
	}

	private String getTestName(final AccountIconID accountIconID, final String playerName)
	{
		return accountIconID + " " + playerName;
	}
}