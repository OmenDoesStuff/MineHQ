package net.frozenorb.qlib.tab;

import net.minecraft.server.v1_7_R4.PlayerConnection;
import org.bukkit.entity.Player;
import net.frozenorb.qlib.tab.reflection.ReflectionConstants;

public enum ClientVersion {

	v1_7, v1_8;

	public static ClientVersion getVersion(Player player) {
		Object handle = ReflectionConstants.GET_HANDLE_METHOD.invoke(player);
		Object connection = ReflectionConstants.PLAYER_CONNECTION.get(handle);
		Object manager = ReflectionConstants.NETWORK_MANAGER.get(connection);
		Object version = ReflectionConstants.VERSION_METHOD.invoke(manager);
		if (version instanceof Integer) {
			return (int) version > 5 ? v1_8 : v1_7;
		}
		return v1_7;
	}

}
