package github.metalshark.discordsrvextras.listeners;

import github.metalshark.discordsrvextras.DiscordSRVExtras;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import github.scarsz.discordsrv.dependencies.jda.api.events.role.update.RoleUpdatePositionEvent;
import org.bukkit.Bukkit;

public class RoleUpdatePositionListener extends ListenerAdapter {

    public void onRoleUpdatePosition(RoleUpdatePositionEvent event) {
        final DiscordSRVExtras plugin = DiscordSRVExtras.getPlugin();
        final Role role = event.getRole();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.refreshRole(role) );
    }

}
