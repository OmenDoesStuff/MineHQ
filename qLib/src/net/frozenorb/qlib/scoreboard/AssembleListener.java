package net.frozenorb.qlib.scoreboard;
import lombok.Getter;
import net.frozenorb.qlib.scoreboard.events.AssembleBoardCreateEvent;
import net.frozenorb.qlib.scoreboard.events.AssembleBoardDestroyEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import net.frozenorb.qlib.QLib;

@Getter
public class AssembleListener implements Listener {

	private Assemble assemble;

	public AssembleListener(Assemble assemble) {
		this.assemble = assemble;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		AssembleBoardCreateEvent createEvent = new AssembleBoardCreateEvent(e.getPlayer());

		Bukkit.getPluginManager().callEvent(createEvent);
		if (createEvent.isCancelled()) {
			return;
		}
		Bukkit.getScheduler().runTask(QLib.getInstance(), () -> getAssemble().getBoards().put(e.getPlayer().getUniqueId(), new AssembleBoard(e.getPlayer(), getAssemble())));

	}


	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		AssembleBoardDestroyEvent destroyEvent = new AssembleBoardDestroyEvent(event.getPlayer());

		Bukkit.getPluginManager().callEvent(destroyEvent);
		if (destroyEvent.isCancelled()) {
			return;
		}

		getAssemble().getBoards().remove(event.getPlayer().getUniqueId());
		event.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
	}

}
