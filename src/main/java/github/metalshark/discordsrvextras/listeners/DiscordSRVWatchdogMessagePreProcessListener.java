package github.metalshark.discordsrvextras.listeners;

import github.metalshark.discordsrvextras.DiscordSRVExtras;
import github.scarsz.discordsrv.api.ListenerPriority;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.WatchdogMessagePreProcessEvent;

public class DiscordSRVWatchdogMessagePreProcessListener {

    @Subscribe(priority = ListenerPriority.MONITOR)
    public void onWatchdogMessagePreProcess(WatchdogMessagePreProcessEvent event) {
        DiscordSRVExtras plugin = DiscordSRVExtras.getPlugin();
        String message = event.getMessage();
        message = message.replace("%guildowner%", plugin.getConfig().getString("ServerAdmin"));
        event.setMessage(message);
    }

}
