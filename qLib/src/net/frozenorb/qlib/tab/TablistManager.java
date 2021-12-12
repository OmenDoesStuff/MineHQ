package net.frozenorb.qlib.tab;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.frozenorb.qlib.QLib;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import lombok.NonNull;


public class TablistManager implements Listener {

	public static TablistManager INSTANCE;

	@Getter
	private final JavaPlugin plugin;

	private final Map<UUID, Tablist> tablists;


	public TablistEntrySupplier supplier;

	private int updateTaskId = -1;

	@SuppressWarnings("deprecation")
	public TablistManager(@NonNull JavaPlugin plugin, @NonNull TablistEntrySupplier supplier, long updateTime) {
		boolean startUpdater = true;
		if (TablistManager.INSTANCE != null) {
			for (int i = 0; i < 7; i++) {
				Bukkit.getLogger().warning("");
			}
			Bukkit.getLogger().warning("WARNING! AN INSTANCE OF TABLISTMANAGER ALREADY EXISTS!");
			Bukkit.getLogger()
					.warning("IT IS RECOMMENDED TO ONLY USE ONE OTHERWISE IT CAN CAUSE FLICKERING AND OTHER ISSUES!");
			for (int i = 0; i < 7; i++) {
				Bukkit.getLogger().warning("");
			}
		}
		updateTime: {
			long remainder = updateTime % 50l;
			if (remainder != 0l) {
				updateTime = updateTime - remainder;
				Bukkit.getLogger().info("FIXING UPDATE TIME TO VALID TICK-COUNT...");
			}
			break updateTime;
		}
		if (updateTime < 50l) {
			startUpdater = false;
			for (int i = 0; i < 7; i++) {
				Bukkit.getLogger().warning("");
			}
			Bukkit.getLogger().warning("WARNING! TABLIST UPDATE TASK NOT STARTED!");
			Bukkit.getLogger().warning("REASON: UPDATE TIME IS TOO SHORT.");
			for (int i = 0; i < 7; i++) {
				Bukkit.getLogger().warning("");
			}
		}
		TablistManager.INSTANCE = this;
		this.tablists = new ConcurrentHashMap<>();
		this.supplier = supplier;
		this.plugin = plugin;
		if (startUpdater) {
			updateTaskId = Bukkit.getScheduler()
					.runTaskTimerAsynchronously(plugin, new TablistUpdateTask(), updateTime / 50, updateTime / 50)
					.getTaskId();
		}
		Bukkit.getPluginManager().registerEvents(this, plugin);
		for(Player player : Bukkit.getOnlinePlayers()) {
			getTablist(player, true);
		}
	}

	public Tablist getTablist(Player player) {
		return getTablist(player, false);
	}

	public Tablist getTablist(Player player, boolean create) {
		UUID uniqueId = player.getUniqueId();
		Tablist tablist = tablists.get(uniqueId);
		if (tablist == null && create) {
			tablists.put(uniqueId, tablist = new Tablist(player));
		}
		return tablist;
	}

	@SuppressWarnings("unused")
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		Bukkit.getScheduler().runTask(QLib.getInstance(), () -> {Tablist tablist = getTablist(player, true);});

	}

	@EventHandler
	public void onDisable(PluginDisableEvent event) {
		if (event.getPlugin() == plugin) {
			tablists.forEach((id, tablist) -> {
				tablist.hideFakePlayers().clear();
			});
			tablists.clear();
			HandlerList.unregisterAll(this);
			if (updateTaskId != -1) {
				Bukkit.getScheduler().cancelTask(updateTaskId);
			}
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		UUID uniqueId = player.getUniqueId();
		Tablist tablist;
		if ((tablist = tablists.remove(uniqueId)) != null) {
			tablist.hideFakePlayers().clear();
		}
	}

}
