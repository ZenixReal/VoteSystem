package net.exsar.votesystem.features.manager;

import lombok.Getter;
import net.exsar.votesystem.features.objects.PlayerData;
import net.exsar.votesystem.features.objects.VoteSiteData;
import net.exsar.votesystem.utils.ChatUtils;
import net.exsar.votesystem.utils.DatabaseManager;
import org.bukkit.OfflinePlayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class VoteManager {

    @Getter
    private static HashMap<UUID, VoteSiteData> voteSiteDataHashMap;
    @Getter
    private static HashMap<UUID, PlayerData> dataHashMap;


    public VoteManager() {
        dataHashMap = new HashMap<>();
        voteSiteDataHashMap = new HashMap<>();
        initialize();
    }

    public static int getStreakToken(OfflinePlayer player) {
        int streak = getData(player).getStreak();
        if(streak >= 120) return 4;
        if(streak >= 60) return 2;

        return 1;
    }

    // onUpdate("CREATE TABLE IF NOT EXISTS players (player VARCHAR(255), tokens INTEGER, vote INTEGER, last_vote_time BIGINT, streak INTEGER)");

    private void initialize() {
        try (PreparedStatement statement = DatabaseManager.getConnection().getConnection().prepareStatement("SELECT * FROM players"); ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                UUID uuid = UUID.fromString(resultSet.getString("player"));
                int token = resultSet.getInt("tokens");
                int votes = resultSet.getInt("vote");
                long lastVote = resultSet.getLong("last_vote_time");
                int streak = resultSet.getInt("streak");
                int saver = resultSet.getInt("saver");
                PlayerData data = new PlayerData(
                        lastVote,
                        votes,
                        streak,
                        token,
                        saver
                );
                dataHashMap.put(uuid, data);
            }
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }

    public static void register(OfflinePlayer player) {
        try (
            PreparedStatement statement = DatabaseManager.getConnection().getConnection()
                    .prepareStatement("INSERT INTO players VALUES (?, ?, ?, ?, ?, ?)")
        ) {
            statement.setString(1, player.getUniqueId().toString());
            statement.setInt(2, 0);
            statement.setInt(3, 0);
            statement.setLong(4, -1);
            statement.setInt(5, 0);
            statement.setInt(6, 0);
            statement.executeUpdate();
            PlayerData data = new PlayerData(-1, 0, 0, 0, 0);
            dataHashMap.put(player.getUniqueId(), data);
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }

    public static boolean isRegistered(OfflinePlayer player) {
        try (PreparedStatement statement = DatabaseManager.getConnection().getConnection()
                .prepareStatement("SELECT * FROM players WHERE player = ?")){
            statement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }

    public static void update(OfflinePlayer player, PlayerData data) {
        dataHashMap.put(player.getUniqueId(), data);
        try (PreparedStatement statement = DatabaseManager.getConnection().getConnection()
                .prepareStatement("UPDATE players SET tokens = ?, vote = ?, last_vote_time = ?, streak = ?, saver = ? WHERE player = ?")) {
            statement.setInt(1, data.getTokens());
            statement.setInt(2, data.getVote());
            statement.setLong(3, data.getLastVote());
            statement.setInt(4, data.getStreak());
            statement.setInt(5, data.getSaver());
            statement.setString(6, player.getUniqueId().toString());
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }

    public static void addVoteStreak(OfflinePlayer player) {
        PlayerData data = getData(player);
        boolean isStreakBroken = data.checkIfVoteStreakBroken(System.currentTimeMillis());
        if(!isRegistered(player))
            return;

        if(getVoteSite(player).isFirst_site() && getVoteSite(player).isSecond_site()) {
            if(isStreakBroken) {
                if(data.getSaver() > 0) {
                    data.removeSaver();
                    if(player.isOnline()) {
                        ChatUtils.sendMessage(
                                Objects.requireNonNull(player.getPlayer()),
                                ChatUtils.ChatType.INFO,
                                "Dein Vote-Streak ist geblieben. Dafür wurde dir ein Vote-Schutzpunkt entfernt."
                        );
                    }
                } else {
                    data.resetStreak();
                }
            } else {
                data.addStreak();
            }

            update(player, data);
            rewardPlayer(player);
        }
    }

    private static void rewardPlayer(OfflinePlayer player) {
        PlayerData data = getData(player);
        int streak = data.getStreak();

        switch (streak) {
            case 10:
                if(player.isOnline()) {
                    ChatUtils.sendMessage(player.getPlayer(), ChatUtils.ChatType.SUCCESS, "500 €");
                }
                break;
            case 20:
                if(player.isOnline()) {
                    ChatUtils.sendMessage(player.getPlayer(), ChatUtils.ChatType.SUCCESS, "VIP Premium 7 Tage");
                }
                break;

            case 30:
                if(player.isOnline()) {
                    ChatUtils.sendMessage(player.getPlayer(), ChatUtils.ChatType.SUCCESS, "VIP Pro 1 Monat");
                }
                break;
        }
    }

    public static VoteSiteData getVoteSite(OfflinePlayer player) {
        return voteSiteDataHashMap.get(player.getUniqueId());
    }

    public static PlayerData getData(OfflinePlayer player) {
        return dataHashMap.get(player.getUniqueId());
    }
}