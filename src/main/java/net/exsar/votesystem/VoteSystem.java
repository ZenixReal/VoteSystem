package net.exsar.votesystem;

import net.exsar.votesystem.features.VoteManager;
import net.exsar.votesystem.features.VoteStreakManager;
import net.exsar.votesystem.utils.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class VoteSystem extends JavaPlugin {

    private final String prefix = "§8[§9§lVote-System§8]";

    @Override
    public void onEnable() {
        DatabaseManager databaseManager = new DatabaseManager(
                "",
                "",
                "",
                ""
        );
        databaseManager.load();
        new VoteStreakManager(); // Initialisierung des VoteStreaks
        new VoteManager(); // Initialisierung des Token-Systems

        Bukkit.getConsoleSender().sendMessage(prefix + "Plugin erfolgreich aktiviert.");
    }

    @Override
    public void onDisable() {
        DatabaseManager.unload();
        Bukkit.getConsoleSender().sendMessage(prefix + "Plugin erfolgreich deaktiviert.");
    }
}
