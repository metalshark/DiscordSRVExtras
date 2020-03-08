package github.metalshark.discordsrvextras.listeners;

import github.metalshark.discordsrvextras.DiscordSRVExtras;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class PlayerJoinListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        final DiscordSRVExtras plugin = DiscordSRVExtras.getPlugin();
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
            plugin.refreshPlayer(player);
        }, DiscordSRVExtras.DELAY);

        final UUID uuid = player.getUniqueId();
        final String name = plugin.getPlayerName(uuid);
        final String playerName = player.getName();

        if (name == playerName) return;
        String message = event.getJoinMessage();
        message = message.replace(playerName, name);
        event.setJoinMessage(message);
    }

}
