package github.metalshark.discordsrvextras.listeners;

import github.metalshark.discordsrvextras.DiscordSRVExtras;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import github.scarsz.discordsrv.dependencies.jda.api.events.role.update.RoleUpdateColorEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;

public class RoleUpdateColorListener extends ListenerAdapter {

    public void onRoleUpdateColor(RoleUpdateColorEvent event) {
        final DiscordSRVExtras plugin = DiscordSRVExtras.getPlugin();
        final Role role = event.getRole();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.refreshRole(role) );
    }

}
