package github.metalshark.discordsrvextras.listeners;

import github.metalshark.discordsrvextras.DiscordSRVExtras;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerQuitListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        if (player == null) return;

        final UUID uuid = player.getUniqueId();
        final DiscordSRVExtras plugin = DiscordSRVExtras.getPlugin();
        final String name = plugin.getPlayerName(uuid);

        final String playerName = player.getName();
        if (name == playerName) return;

        String message = event.getQuitMessage();
        message = message.replace(playerName, name);
        event.setQuitMessage(message);
    }

}
