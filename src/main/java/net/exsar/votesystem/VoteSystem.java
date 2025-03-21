package net.exsar.votesystem;

import net.exsar.votesystem.commands.TokensAdminCommand;
import net.exsar.votesystem.commands.VoteCommand;
import net.exsar.votesystem.features.manager.ItemManager;
import net.exsar.votesystem.features.manager.VoteManager;
import net.exsar.votesystem.listeners.PlayerJoinListener;
import net.exsar.votesystem.listeners.VoteShopListener;
import net.exsar.votesystem.utils.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class VoteSystem extends JavaPlugin {

    private final String prefix = "§8[§9§lVote-System§8] §r";

    @Override
    public void onEnable() {
        DatabaseManager databaseManager = new DatabaseManager(
                "",
                "",
                "",
                ""
        );
        databaseManager.load();

        VoteManager.initialize();
        ItemManager.initialize();

        initListeners();
        initCommands();

        Bukkit.getConsoleSender().sendMessage(prefix + "Plugin erfolgreich aktiviert.");
    }

    @Override
    public void onDisable() {
        DatabaseManager.unload();
        Bukkit.getConsoleSender().sendMessage(prefix + "Plugin erfolgreich deaktiviert.");
    }

    private void initCommands() {
        new VoteCommand("vote", this);
        new TokensAdminCommand("tokenadmin", this);
    }

    private void initListeners() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new PlayerJoinListener(), this);
        pluginManager.registerEvents(new VoteShopListener(), this);
    }

}
