package github.metalshark.discordsrvextras.listeners;

import github.metalshark.discordsrvextras.DiscordSRVExtras;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.ListenerPriority;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordReadyEvent;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import github.scarsz.discordsrv.util.DiscordUtil;
import org.bukkit.Bukkit;

public class DiscordSRVReadyListener {

    @Subscribe(priority = ListenerPriority.MONITOR)
    public void onReady(DiscordReadyEvent event) {
        final DiscordSRVExtras plugin = DiscordSRVExtras.getPlugin();
        final DiscordSRV discordSRV = DiscordSRV.getPlugin();

        final JDA jda = discordSRV.getJda();
        jda.addEventListener(new GuildMemberRoleAddListener());
        jda.addEventListener(new GuildMemberRoleRemoveListener());
        jda.addEventListener(new GuildMemberUpdateNicknameListener());
        jda.addEventListener(new RoleUpdateColorListener());
        jda.addEventListener(new RoleUpdatePositionListener());

        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {

            discordSRV.getAccountLinkManager().getLinkedAccounts().forEach(
                (discordId, uuid) -> {
                    final User user = jda.getUserById(discordId);
                    if (user == null) return;

                    final Member member = discordSRV.getMainGuild().getMember(user);
                    if (member == null) return;

                    final String name = member.getEffectiveName();
                    final Role topRole = DiscordUtil.getTopRole(member);
                    final String nameColor = DiscordUtil.convertRoleToMinecraftColor(topRole);
                    plugin.changePlayerName(uuid, name, nameColor);
                }
            );

        }, DiscordSRVExtras.DELAY);
    }

}
