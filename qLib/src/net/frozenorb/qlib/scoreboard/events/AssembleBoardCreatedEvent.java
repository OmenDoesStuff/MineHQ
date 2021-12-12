package net.frozenorb.qlib.scoreboard.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import net.frozenorb.qlib.scoreboard.AssembleBoard;

@Getter @Setter
public class AssembleBoardCreatedEvent extends Event {

    @Getter public static HandlerList handlerList = new HandlerList();
    private boolean cancelled = false;
    private final AssembleBoard board;
    private Player player;

    public AssembleBoardCreatedEvent(AssembleBoard board, Player player) {
        this.board = board;
        this.player = player;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
