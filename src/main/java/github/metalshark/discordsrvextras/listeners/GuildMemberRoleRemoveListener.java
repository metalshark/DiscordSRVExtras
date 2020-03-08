package github.metalshark.discordsrvextras.listeners;

import github.metalshark.discordsrvextras.DiscordSRVExtras;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;

public class GuildMemberRoleRemoveListener extends ListenerAdapter {

    public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent event) {
        DiscordSRVExtras plugin = DiscordSRVExtras.getPlugin();
        Member member = event.getMember();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.refreshMember(member) );
    }

}
