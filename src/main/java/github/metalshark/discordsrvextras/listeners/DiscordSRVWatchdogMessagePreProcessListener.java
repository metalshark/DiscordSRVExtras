package github.metalshark.discordsrvextras.listeners;

import github.metalshark.discordsrvextras.DiscordSRVExtras;
import github.scarsz.discordsrv.api.ListenerPriority;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.WatchdogMessagePreProcessEvent;

public class DiscordSRVWatchdogMessagePreProcessListener {

    @Subscribe(priority = ListenerPriority.MONITOR)
    public void onWatchdogMessagePreProcess(WatchdogMessagePreProcessEvent event) {
        DiscordSRVExtras plugin = DiscordSRVExtras.getPlugin();

        final String serverAdmin = plugin.getConfig().getString("ServerAdmin");
        if (serverAdmin == null) return;

        String message = event.getMessage();
        message = message.replace("%guildowner%", serverAdmin);
        event.setMessage(message);
    }

}
