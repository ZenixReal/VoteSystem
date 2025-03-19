package net.exsar.votesystem.listeners;

import net.exsar.votesystem.features.manager.VoteManager;
import net.exsar.votesystem.features.objects.VoteSiteData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if(!VoteManager.isRegistered(event.getPlayer())) {
            VoteManager.register(event.getPlayer());
        }

        if(!VoteManager.getVoteSiteDataHashMap().containsKey(event.getPlayer().getUniqueId())) {
            VoteSiteData voteSiteData = new VoteSiteData(false, false);
            VoteManager.getVoteSiteDataHashMap().put(event.getPlayer().getUniqueId(), voteSiteData);
        }
    }

}
