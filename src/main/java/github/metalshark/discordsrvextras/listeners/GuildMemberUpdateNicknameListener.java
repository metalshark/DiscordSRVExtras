package github.metalshark.discordsrvextras.listeners;

import github.metalshark.discordsrvextras.DiscordSRVExtras;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;

public class GuildMemberUpdateNicknameListener extends ListenerAdapter {

    public void onGuildMemberUpdateNickname(GuildMemberUpdateNicknameEvent event) {
        DiscordSRVExtras plugin = DiscordSRVExtras.getPlugin();
        Member member = event.getMember();
        if (member != null) {
            plugin.refreshMember(member);
        }
    }

}
