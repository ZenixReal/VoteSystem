package net.exsar.votesystem.utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Map;
import java.util.Objects;

public class InventoryTool {

    public static void fillEmptySlots(Inventory inventory) {
        for(int i = 0; i < inventory.getSize(); i++) {
            if(inventory.getItem(i) == null) {
                inventory.setItem(i, new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE)
                        .displayName("§r")
                        .build());
            }
        }
    }

    public static boolean hasInventorySpace(Player player) {
        Inventory inventory = player.getInventory();

        int freieSlots = inventory.firstEmpty();
        return freieSlots != -1;
    }
}
