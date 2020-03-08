package github.metalshark.discordsrvextras.listeners;

import github.metalshark.discordsrvextras.DiscordSRVExtras;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;

public class GuildMemberRoleAddListener extends ListenerAdapter {

    public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event) {
        DiscordSRVExtras plugin = DiscordSRVExtras.getPlugin();
        Member member = event.getMember();
        if (member == null) return;
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.refreshMember(member) );
    }

}
