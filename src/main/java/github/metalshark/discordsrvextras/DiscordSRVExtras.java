package github.metalshark.discordsrvextras;

import github.metalshark.discordsrvextras.listeners.*;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import github.scarsz.discordsrv.util.DiscordUtil;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.data.DataMutateResult;
import net.luckperms.api.model.data.NodeMap;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class DiscordSRVExtras extends JavaPlugin {

    static final public long DELAY = 5;

    final private List<ChatColor> COLORS = Collections.unmodifiableList(Arrays.asList(
        ChatColor.BLACK,
        ChatColor.DARK_BLUE,
        ChatColor.DARK_GREEN,
        ChatColor.DARK_AQUA,
        ChatColor.DARK_RED,
        ChatColor.DARK_PURPLE,
        ChatColor.GOLD,
        ChatColor.GRAY,
        ChatColor.DARK_GRAY,
        ChatColor.BLUE,
        ChatColor.GREEN,
        ChatColor.AQUA,
        ChatColor.RED,
        ChatColor.LIGHT_PURPLE,
        ChatColor.YELLOW,
        ChatColor.WHITE
    ));

    private final DiscordSRVReadyListener discordSRVReadyListener = new DiscordSRVReadyListener();
    private final DiscordSRVWatchdogMessagePreProcessListener discordSRVWatchdogMessagePreProcessListener = new DiscordSRVWatchdogMessagePreProcessListener();

    private final ConcurrentHashMap<UUID, String> playerNames = new ConcurrentHashMap<>();

    private Scoreboard scoreboard;
    private Map<String, Team> teams = new TreeMap<>();

    private LuckPerms luckPerms;

    public static DiscordSRVExtras getPlugin() {
        return getPlugin(DiscordSRVExtras.class);
    }

    public void changePlayerName(UUID uuid, String name, String nameColor) {
        final int MAX_NAME_LENGTH = 16;
        if (name.length() > MAX_NAME_LENGTH) name = name.substring(0, MAX_NAME_LENGTH);
        final String finalName = name;
        playerNames.put(uuid, finalName);

        Bukkit.getScheduler().runTask(this, () -> {

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

            String playerName = offlinePlayer.getName();
            if (playerName == null) return;

            for (Map.Entry<String, Team> entry : teams.entrySet()) {
                final String teamColour = entry.getKey();
                final Team team = entry.getValue();
                if (team.hasEntry(playerName)) {
                    if (!teamColour.equals(nameColor)) {
                        team.removeEntry(playerName);
                    }
                } else if (teamColour.equals(nameColor)) {
                    team.addEntry(playerName);
                }
            }

            Player player = Bukkit.getPlayer(uuid);
            if (player == null) return;
            player.setCustomName(finalName);
            player.setDisplayName(finalName);
            player.setPlayerListName(finalName);

        });
    }

    public String getPlayerName(UUID uuid) {
        if (playerNames.containsKey(uuid)) {
            return playerNames.get(uuid);
        }

        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            return player.getName();
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        return offlinePlayer.getName();
    }

    public void refreshMember(Member member) {
        if (member == null) return;

        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {

            UUID uuid = DiscordSRV.getPlugin().getAccountLinkManager().getUuid(member.getId());
            if (uuid == null) return;

            final String name = member.getEffectiveName();
            final Role topRole = DiscordUtil.getTopRole(member);
            final String nameColor = DiscordUtil.convertRoleToMinecraftColor(topRole);
            changePlayerName(uuid, name, nameColor);

            if (luckPerms == null) return;

            ConfigurationSection rolesToGroupsConfig = getConfig().getConfigurationSection("RolesToGroups");
            if (rolesToGroupsConfig == null) return;

            final Map<String, Object> rolesToGroups = rolesToGroupsConfig.getValues(false);
            UserManager userManager = luckPerms.getUserManager();
            CompletableFuture<net.luckperms.api.model.user.User> userFuture = userManager.loadUser(uuid);

            userFuture.thenAcceptAsync(user -> {
                String primaryGroup = user.getPrimaryGroup().toLowerCase();
                String topGroupName = null;
                for (Role role : member.getRoles()) {
                    String roleId = role.getId();
                    if (!rolesToGroups.containsKey(roleId)) continue;
                    topGroupName = ((String) rolesToGroups.get(roleId)).toLowerCase();
                    break;
                }

                if (topGroupName == null) return;
                if (topGroupName.equalsIgnoreCase(primaryGroup)) return;

                getLogger().info("Change primary group of " + user.getUsername() + " from \"" + primaryGroup + "\" to \"" + topGroupName + "\"");
                NodeMap userData = user.data();
                DataMutateResult result;

                result = userData.remove(Node.builder("group." + primaryGroup).build());
                if (result == DataMutateResult.SUCCESS) {
                    getLogger().info("Removed " + user.getUsername() + " from group " + primaryGroup);
                } else {
                    getLogger().warning("Unable to remove " + user.getUsername() + " from group " + primaryGroup + " received " + result.name());
                }

                result = userData.add(Node.builder("group." + topGroupName).build());
                if (result == DataMutateResult.SUCCESS) {
                    getLogger().info(user.getUsername() + " added to group " + topGroupName);
                } else {
                    getLogger().warning("Unable to add " + user.getUsername() + " to group " + topGroupName + " received " + result.name());
                }

                result = user.setPrimaryGroup(topGroupName);
                if (result == DataMutateResult.SUCCESS) {
                    getLogger().info("Set primary group for " + user.getUsername() + " to " + topGroupName);
                } else {
                    getLogger().warning("Unable to set primary group for " + user.getUsername() + " to " + topGroupName + " received " + result.name());
                }

                for (Role role : member.getRoles()) {
                    String roleId = role.getId();
                    if (!rolesToGroups.containsKey(roleId)) continue;

                    final String groupName = ((String) rolesToGroups.get(roleId)).toLowerCase();
                    if (topGroupName.equalsIgnoreCase(groupName)) continue;

                    result = userData.remove(Node.builder("group." + groupName).build());
                    if (result == DataMutateResult.SUCCESS) {
                        getLogger().info("Removed " + user.getUsername() + " from group " + groupName);
                    } else {
                        getLogger().warning("Unable to remove " + user.getUsername() + " from group " + groupName + " received " + result.name());
                    }
                }

                userManager.saveUser(user);
            });

        });
    }

    public void refreshPlayer(Player player) {
        if (player == null) return;

        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {

            final DiscordSRV discordSRV = DiscordSRV.getPlugin();
            if (!DiscordSRV.isReady) return;

            final JDA jda = discordSRV.getJda();

            final UUID uuid = player.getUniqueId();
            final String discordId = DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(uuid);
            if (discordId == null) return;

            final User user = jda.getUserById(discordId);
            if (user == null) return;

            final Member member = discordSRV.getMainGuild().getMember(user);
            if (member == null) return;

            refreshMember(member);

        });
    }

    public void refreshRole(Role role) {
        final DiscordSRV discordSRV = DiscordSRV.getPlugin();
        final JDA jda = discordSRV.getJda();

        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {

            discordSRV.getAccountLinkManager().getLinkedAccounts().forEach(
                (discordId, uuid) -> {
                    final User user = jda.getUserById(discordId);
                    if (user == null) return;

                    final Member member = discordSRV.getMainGuild().getMember(user);
                    if (member == null) return;

                    if (!member.getRoles().contains(role)) return;
                    Bukkit.getScheduler().runTaskLaterAsynchronously(discordSRV, () -> refreshMember(member), DiscordSRVExtras.DELAY);
                }
            );

        });
    }

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();

        if (scoreboardManager == null) {
            getLogger().warning("Unable to get scoreboard manager using Bukkit.getScoreboardManager()");
            return;
        }
        scoreboard = scoreboardManager.getMainScoreboard();

        COLORS.forEach(chatColor -> {
            final String teamName = chatColor.name();
            Team team = scoreboard.getTeam(teamName);
            if (team == null) {
                team = scoreboard.registerNewTeam(teamName);
            }
            final String colorString = chatColor.toString();
            team.setColor(chatColor);
            teams.put(colorString, team);
        });
        teams = Collections.unmodifiableMap(teams);

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            luckPerms = provider.getProvider();
        }

        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(), this);

        DiscordSRV.api.subscribe(discordSRVReadyListener);
        DiscordSRV.api.subscribe(discordSRVWatchdogMessagePreProcessListener);
    }

    @Override
    public void onDisable() {
        DiscordSRV.api.unsubscribe(discordSRVReadyListener);
        DiscordSRV.api.unsubscribe(discordSRVWatchdogMessagePreProcessListener);
    }

}