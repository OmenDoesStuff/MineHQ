package net.frozenorb.qlib.tab;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TablistUpdateTask implements Runnable {

	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		TablistManager manager = TablistManager.INSTANCE;
		if (manager == null)
			return;
		for(Player player : Bukkit.getOnlinePlayers()) {
			Tablist tablist = manager.getTablist(player);
			if (tablist != null) {
				tablist.hideRealPlayers().update();
			}
		}

	}

}
