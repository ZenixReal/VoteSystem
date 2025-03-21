package net.exsar.votesystem.features.manager;

import net.exsar.votesystem.features.objects.PlayerData;
import net.exsar.votesystem.features.objects.VoteSiteData;
import net.exsar.votesystem.utils.ChatUtils;
import net.exsar.votesystem.utils.DatabaseManager;
import org.bukkit.OfflinePlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

public class VoteStreakManager {

    private final OfflinePlayer player;
    public VoteStreakManager(OfflinePlayer player) {
        this.player = player;
    }

    public void addVoteStreak() {
        PlayerData data = VoteManager.getData(player);
        VoteSiteData voteSiteData = VoteManager.getVoteSite(player);
        VoteManager voteManager = new VoteManager(player);
        boolean isStreakBroken = data.checkIfVoteStreakBroken(System.currentTimeMillis());

        if(!voteManager.isRegistered())
            return;

        if(voteSiteData.isFirst_site() && voteSiteData.isSecond_site()) {
            if(isStreakBroken) {
                if(data.getSaver() > 0) {
                    data.removeSaver();
                    if(player.isOnline()) {
                        ChatUtils.sendMessage(
                                Objects.requireNonNull(player.getPlayer()),
                                ChatUtils.ChatType.INFO,
                                "Dein Vote-Streak ist geblieben. Für den Vote-Schutz wurde dir ein Vote-Schutzpunkt entfernt."
                        );
                    }
                    data.addStreak();
                } else {
                    data.resetStreak();
                }
            } else {
                data.addStreak();
            }

            voteManager.update(data);
            rewardPlayer(player);
        }
    }

    private void rewardPlayer(OfflinePlayer player) {
        PlayerData data = VoteManager.getData(player);
        int streak = data.getStreak();

        switch (streak) {
            case 10:
                if(player.isOnline()) {
                    ChatUtils.sendMessage(Objects.requireNonNull(player.getPlayer()), ChatUtils.ChatType.SUCCESS, "500 €");
                }
                break;
            case 20:
                if(player.isOnline()) {
                    ChatUtils.sendMessage(Objects.requireNonNull(player.getPlayer()), ChatUtils.ChatType.SUCCESS, "VIP Premium 7 Tage");
                }
                break;

            case 30:
                if(player.isOnline()) {
                    ChatUtils.sendMessage(Objects.requireNonNull(player.getPlayer()), ChatUtils.ChatType.SUCCESS, "VIP Pro 1 Monat");
                }
                break;
        }
    }

}
