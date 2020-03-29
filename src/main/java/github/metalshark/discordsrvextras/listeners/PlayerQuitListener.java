package github.metalshark.discordsrvextras.listeners;

import github.metalshark.discordsrvextras.DiscordSRVExtras;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerQuitListener implements Listener {

    @SuppressWarnings("unused")
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        final DiscordSRVExtras plugin = DiscordSRVExtras.getPlugin();

        final UUID uuid = player.getUniqueId();
        final String name = plugin.getPlayerName(uuid);

        final String playerName = player.getName();
        if (name.equals(playerName)) return;
        
        String message = event.getQuitMessage();
        message = message.replace(playerName, name);
        event.setQuitMessage(message);
    }

}
