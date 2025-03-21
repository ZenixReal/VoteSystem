package net.exsar.votesystem.listeners;

import net.exsar.votesystem.features.manager.VoteManager;
import net.exsar.votesystem.features.objects.VoteSiteData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        VoteManager voteManager = new VoteManager(event.getPlayer());
        UUID uuid = event.getPlayer().getUniqueId();

        if(!voteManager.isRegistered()) { voteManager.register(); }

        if(!VoteManager.getVoteSiteDataHashMap().containsKey(uuid)) {
            VoteSiteData voteSiteData = new VoteSiteData(false, false);
            VoteManager.getVoteSiteDataHashMap().put(uuid, voteSiteData);
        }
    }

}
