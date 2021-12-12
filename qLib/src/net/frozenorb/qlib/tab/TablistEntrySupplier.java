package net.frozenorb.qlib.tab;

import org.bukkit.entity.Player;

import com.google.common.collect.Table;

public interface TablistEntrySupplier {

	Table<Integer, Integer, TabEntry> getEntries(Player player);

	String getHeader(Player player);

	String getFooter(Player player);

}
