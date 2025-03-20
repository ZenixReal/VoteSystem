package net.exsar.votesystem.features.manager;

import lombok.Getter;
import net.exsar.votesystem.features.VoteInventories;
import net.exsar.votesystem.features.objects.Items;
import net.exsar.votesystem.features.objects.PlayerData;
import net.exsar.votesystem.utils.ChatUtils;
import net.exsar.votesystem.utils.DatabaseManager;
import net.exsar.votesystem.utils.InventoryTool;
import net.exsar.votesystem.utils.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.*;
import java.util.*;

public class ItemManager {

    @Getter
    private static HashMap<UUID, Items> already_bought;

    public ItemManager() {
        already_bought = new HashMap<>();
        initialize();
    }

    private void initialize() {
        try (
                PreparedStatement statement = DatabaseManager.getConnection().getConnection()
                        .prepareStatement("SELECT * FROM already_bought");
                ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                Items item = Items.valueOf(resultSet.getString("item"));
                UUID uuid = UUID.fromString(resultSet.getString("player"));
                already_bought.put(uuid, item);
            }
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }

    private static void registerUnique(Player player, Items item) {
        try (PreparedStatement statement = DatabaseManager.getConnection().getConnection()
                .prepareStatement("INSERT INTO already_bought VALUES (?, ?)")) {
            statement.setString(1, player.getUniqueId().toString());
            statement.setString(2, item.toString());
            statement.executeUpdate();
            already_bought.put(player.getUniqueId(), item);
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }

    private static boolean hasBought(Player player, Items item) {
        if(already_bought.containsKey(player.getUniqueId())) {
            return already_bought.containsValue(item);
        }
        return false;
    }

    public static void addItem(Player player, Items item) {
        TokenManager tokenManager = new TokenManager(player);
        PlayerData data = VoteManager.getData(player);
        if (!tokenManager.check(item.getCost())) {
            ChatUtils.sendMessage(player, ChatUtils.ChatType.ERROR, "Du hast dafür zu wenig Vote-Tokens.");
            return;
        }

        if(item.isUnique()) {
            if(!hasBought(player, item)) {
                registerUnique(player, item);
            } else {
                ChatUtils.sendMessage(player, ChatUtils.ChatType.ERROR, "Du kannst dies nicht erneut kaufen.");
                return;
            }
        }

        if(item.isPhysical()) {
            if(!InventoryTool.hasInventorySpace(player)) {
                ChatUtils.sendMessage(player, ChatUtils.ChatType.WARNING, "Du hast kein Platz im Inventar.");
                return;
            }
        }
        switch (item) {
            case SUPER_PICKAXE:
                player.closeInventory();
                player.getInventory().addItem(
                        new ItemBuilder(Material.DIAMOND_PICKAXE)
                        .displayName("§c§lSuperwerkzeug")
                        .addEnchant(Enchantment.MENDING, 1)
                        .addEnchant(Enchantment.UNBREAKING, 3)
                        .addEnchant(Enchantment.EFFICIENCY, 5)
                        .addEnchant(Enchantment.FORTUNE, 4)
                        .build());
                tokenManager.remove(item.getCost());
                ChatUtils.sendMessage(player, ChatUtils.ChatType.SUCCESS, "Du hast nun §lSuperwerkzeug §r§aerfolgreich gekauft.");
                break;

            case X15_FLY_TICKETS:
                // Implementierung des Flugtickets.
                player.closeInventory();
                ChatUtils.sendMessage(player, ChatUtils.ChatType.SUCCESS, "Du hast nun §l15x Flugtickets für Farmwelt §r§aerhalten.");
                tokenManager.remove(item.getCost());
                break;

            case RANDOM_EFFECT_PACKAGE:
                // Implementierung des Effektepaketes.
                player.closeInventory();
                ChatUtils.sendMessage(player, ChatUtils.ChatType.SUCCESS, "Du hast nun ein zufälligen Effektpaket erhalten! Siehe über §l'/effekte' §r§anach.");
                tokenManager.remove(item.getCost());
                break;

            case STREAK_SAVER:
                if(data.getStreak() < 60) {
                    ChatUtils.sendMessage(player, ChatUtils.ChatType.WARNING, "Du kannst dies kaufen, wenn du länger als 2 Monate aktiv votest.");
                    return;
                }
                player.closeInventory();
                data.addSaver();
                VoteManager.update(player, data);
                tokenManager.remove(item.getCost());
                ChatUtils.sendMessage(player, ChatUtils.ChatType.SUCCESS, "Du hast nun einen §lVote-Streak Schutzpunkt §r§agekauft.");
                break;
            case GOLDEN_APPLE:
                VoteInventories.getAmountOfItems().put(player.getUniqueId(), 1);
                VoteInventories.getCurrentItem().put(player.getUniqueId(), Items.GOLDEN_APPLE);
                VoteInventories.openAmount(player);
                break;

            case ENCHANTED_GOLDEN_APPLE:
                VoteInventories.getAmountOfItems().put(player.getUniqueId(), 1);
                VoteInventories.getCurrentItem().put(player.getUniqueId(), Items.ENCHANTED_GOLDEN_APPLE);
                VoteInventories.openAmount(player);
                break;

            case ONE_MONTH_VIP_PREMIUM:
                // Implementierung des Rängesystems.
                player.closeInventory();
                tokenManager.remove(item.getCost());
                player.sendMessage(Component.text("1 Monat VIP Premium"));
                break;
        }
    }
}
