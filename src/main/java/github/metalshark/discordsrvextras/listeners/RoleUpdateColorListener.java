package github.metalshark.discordsrvextras.listeners;

import github.metalshark.discordsrvextras.DiscordSRVExtras;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import github.scarsz.discordsrv.dependencies.jda.api.events.role.update.RoleUpdateColorEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;

public class RoleUpdateColorListener extends ListenerAdapter {

    public void onRoleUpdateColor(RoleUpdateColorEvent event) {
        final DiscordSRVExtras plugin = DiscordSRVExtras.getPlugin();
        final Role role = event.getRole();
        plugin.refreshRole(role);
    }

}
