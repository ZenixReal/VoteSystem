package net.exsar.votesystem.features.manager;

import lombok.Getter;
import net.exsar.votesystem.features.objects.PlayerData;
import net.exsar.votesystem.features.objects.VoteSiteData;
import net.exsar.votesystem.utils.DatabaseManager;
import org.bukkit.OfflinePlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class VoteManager {

    @Getter
    private static HashMap<UUID, VoteSiteData> voteSiteDataHashMap = new HashMap<>();
    @Getter
    private static HashMap<UUID, PlayerData> dataHashMap = new HashMap<>();

    private final OfflinePlayer player;
    public VoteManager(OfflinePlayer player) {
        this.player = player;
    }

    public int getStreakToken() {
        int streak = getData(player).getStreak();
        if(streak >= 120) return 4;
        if(streak >= 60) return 2;

        return 1;
    }

    public static void initialize() {
        try (PreparedStatement statement = DatabaseManager.getConnection().getConnection().prepareStatement("SELECT * FROM players");
             ResultSet resultSet = statement.executeQuery()) {
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

    public void register() {
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

    public boolean isRegistered() {
        try (PreparedStatement statement = DatabaseManager.getConnection().getConnection()
                .prepareStatement("SELECT * FROM players WHERE player = ?")){
            statement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void update(PlayerData data) {
        String query = "UPDATE players SET tokens = ?, vote = ?, last_vote_time = ?, streak = ?, saver = ? WHERE player = ?";

        try (Connection connection = DatabaseManager.getConnection().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, data.getTokens());
            statement.setInt(2, data.getVote());
            statement.setLong(3, data.getLastVote());
            statement.setInt(4, data.getStreak());
            statement.setInt(5, data.getSaver());
            statement.setString(6, player.getUniqueId().toString());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) System.out.println("Warnung: Kein Datensatz wurde aktualisiert für Spieler: " + player.getUniqueId());
            dataHashMap.put(player.getUniqueId(), data);
        } catch (SQLException exception) {
            System.err.println("Fehler bei der Datenbankabfrage: " + exception.getMessage());
            throw new RuntimeException("Fehler bei der Aktualisierung der Spieler-Daten", exception);
        }
    }

    public static VoteSiteData getVoteSite(OfflinePlayer target) {
        return voteSiteDataHashMap.get(target.getUniqueId());
    }

    public static PlayerData getData(OfflinePlayer target) {
        return dataHashMap.get(target.getUniqueId());
    }
}