package net.exsar.votesystem.features;

import lombok.Getter;
import net.exsar.votesystem.features.objects.PlayerVoteData;
import net.exsar.votesystem.features.objects.PlayerVoteStreakData;
import net.exsar.votesystem.utils.DatabaseManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class VoteManager {

    @Getter
    private static HashMap<UUID, PlayerVoteData> dataHashMap;

    public VoteManager() {
        dataHashMap = new HashMap<>();
        initialize();
    }

    public static void initialize() {
        try (PreparedStatement statement = DatabaseManager.getConnection().getConnection().prepareStatement("SELECT * FROM players"); ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                UUID uuid = UUID.fromString(resultSet.getString("player"));
                int token = resultSet.getInt("tokens");
                int votes = resultSet.getInt("votes");
                PlayerVoteData data = new PlayerVoteData(token, votes);

                dataHashMap.put(uuid, data);
            }
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }

    public static PlayerVoteData getData(OfflinePlayer player) {
        return dataHashMap.get(player.getUniqueId());
    }

    public static void saveData(OfflinePlayer player) {
        PlayerVoteData data = dataHashMap.get(player.getUniqueId());
        try (PreparedStatement statement = DatabaseManager.getConnection().getConnection().prepareStatement("INSERT INTO players (player, tokens, vote) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE tokens = ?, vote = ?")) {
            statement.setString(1, player.getUniqueId().toString());
            statement.setInt(2, data.getToken());
            statement.setInt(3, data.getVotes());
            statement.setInt(4, data.getToken());
            statement.setInt(5, data.getVotes());
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }

}
