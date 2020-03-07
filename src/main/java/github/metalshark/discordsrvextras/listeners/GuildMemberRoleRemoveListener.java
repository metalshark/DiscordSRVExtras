package github.metalshark.discordsrvextras.listeners;

import github.metalshark.discordsrvextras.DiscordSRVExtras;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;

public class GuildMemberRoleRemoveListener extends ListenerAdapter {

    public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent event) {
        DiscordSRVExtras plugin = DiscordSRVExtras.getPlugin();
        Member member = event.getMember();
        if (member != null) {
            plugin.refreshMember(member);
        }
    }

}
