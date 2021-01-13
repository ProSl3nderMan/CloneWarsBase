package me.prosl3nderman.clonewarsbase.Internal.Player;

import com.google.inject.Injector;
import me.prosl3nderman.clonewarsbase.Internal.Handler;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.UUID;

@Singleton
public class PlayerHandler implements Handler {

    private Injector injector;

    @Inject
    public PlayerHandler(Injector injector) {
        this.injector = injector;
    }

    public HashMap<UUID, Player> players = new HashMap<>();

    @Override
    public void enable() {

    }

    @Override
    public void disable() {
        players.clear();
    }

    public void loadPlayer(org.bukkit.entity.Player bukkitPlayer) {
        Player CWPlayer = injector.getInstance(Player.class);
        CWPlayer.loadPlayerVariables(bukkitPlayer);

        players.put(bukkitPlayer.getUniqueId(), CWPlayer);
    }

    public Player getPlayer(UUID playerUUID) {
        return players.get(playerUUID);
    }
}
