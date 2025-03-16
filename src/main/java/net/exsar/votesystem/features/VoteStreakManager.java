package net.exsar.votesystem.features;

import lombok.Getter;
import net.exsar.votesystem.features.objects.PlayerVoteStreakData;
import net.exsar.votesystem.utils.DatabaseManager;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class VoteStreakManager {

    @Getter
    private HashMap<UUID, PlayerVoteStreakData> playerData;

    public VoteStreakManager() {
        playerData = new HashMap<>();
        initialize();
    }

    private void initialize() {
        try (PreparedStatement statement = DatabaseManager.getConnection().getConnection().prepareStatement("SELECT * FROM vote_streaks"); ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                long lastVote = resultSet.getLong("last_vote_time");
                int streak = resultSet.getInt("streak");
                UUID uuid = UUID.fromString(resultSet.getString("player"));
                PlayerVoteStreakData data = new PlayerVoteStreakData(lastVote, streak);

                playerData.put(uuid, data);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveData(Player player) {
        PlayerVoteStreakData data = playerData.get(player.getUniqueId());
        try (PreparedStatement statement = DatabaseManager.getConnection().getConnection().prepareStatement("INSERT INTO vote_streaks (player, last_vote_time, streak) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE last_vote_time = ?, streak = ?")) {
            statement.setString(1, player.getUniqueId().toString());
            statement.setLong(2, data.getLastVoteTime());
            statement.setInt(3, data.getStreak());
            statement.setLong(4, data.getLastVoteTime());
            statement.setInt(5, data.getStreak());
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void addStreak(Player player) {
        UUID uuid = player.getUniqueId();
        PlayerVoteStreakData data = playerData.get(uuid);
        long currentTime = System.currentTimeMillis();
        boolean streakBroken = data.checkIfVoteStreakBroken(currentTime);

        if (!playerData.containsKey(uuid)) {
            playerData.put(uuid, new PlayerVoteStreakData(currentTime, 1));
            saveData(player);
            return;
        }

        if (streakBroken) {
            data.resetStreak();
        } else {
            data.addStreak();
        }

        playerData.put(uuid, data);
        rewardPlayer(player, data.getStreak());
        saveData(player);
    }

    public void rewardPlayer(Player player, int streak) {
        switch (streak) {
            case 5:
                player.sendMessage("§6§lWir bedanken uns für dein Vote-Streak von 5 Tagen! §7Als Dank schenken wir dir 1.000 €.");
                break;
            case 15:
                player.sendMessage("§6§lWir bedanken uns für dein Vote-Streak von 15 Tagen! §7Als Dank schenken wir dir 1.500 €.");
                break;
            case 20:
                player.sendMessage("§6§lWir bedanken uns für dein Vote-Streak von 20 Tagen! §7Als Dank schenken wir dir 3.500 €.");
                break;
            case 25:
                player.sendMessage("§6§lWir bedanken uns für dein Vote-Streak von 25 Tagen! §7Als Dank schenken wir dir 15 Flugtickets in die Farmwelt.");
                break;
            case 30:
                player.sendMessage("§6§lWir bedanken uns für dein Vote-Streak von 30 Tagen! §7Als Dank schenken wir dir 7 Tage VIP Premium als Gutschein.");
                break;
        }
    }
}
