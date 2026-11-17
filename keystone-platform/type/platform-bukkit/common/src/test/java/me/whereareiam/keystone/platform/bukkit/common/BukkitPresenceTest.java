package me.whereareiam.keystone.platform.bukkit.common;

import me.whereareiam.keystone.common.integration.Integration;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BukkitPresenceTest {
	@Test
	void aPluginCountsWhenItIsEnabled() {
		Plugin enabled = plugin("PlaceholderAPI", true);
		Plugin disabled = plugin("MiniPlaceholders", false);
		Plugin keystone = server(enabled, disabled);

		assertTrue(BukkitPresence.enabled(keystone, "placeholderapi"));
		assertFalse(BukkitPresence.enabled(keystone, "MiniPlaceholders"));
		assertFalse(BukkitPresence.enabled(keystone, "Intercept"));
	}

	@Test
	void theSharedIntegrationsArePlaceholderApiAndIntercept() {
		assertEquals(List.of("PlaceholderAPI", "Intercept"), BukkitIntegrations.shared().stream().map(Integration::plugin).toList());
	}

	static Plugin server(Plugin... plugins) {
		Plugin keystone = mock(Plugin.class);
		Server server = mock(Server.class);
		PluginManager manager = mock(PluginManager.class);
		when(keystone.getServer()).thenReturn(server);
		when(server.getPluginManager()).thenReturn(manager);
		when(manager.getPlugins()).thenReturn(plugins);

		return keystone;
	}

	private static Plugin plugin(String name, boolean enabled) {
		Plugin plugin = mock(Plugin.class);
		when(plugin.getName()).thenReturn(name);
		when(plugin.isEnabled()).thenReturn(enabled);

		return plugin;
	}
}
