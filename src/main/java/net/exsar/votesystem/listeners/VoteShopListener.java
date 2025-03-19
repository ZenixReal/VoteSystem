package net.exsar.votesystem.listeners;

import lombok.val;
import net.exsar.votesystem.features.VoteInventories;
import net.exsar.votesystem.features.manager.*;
import net.exsar.votesystem.features.objects.Items;
import net.exsar.votesystem.utils.ChatUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class VoteShopListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        try {
            if (event.getView().title().equals(Component.text("§c§lVOTE-SHOP"))) {
                event.setCancelled(true);
                Player player = (Player) event.getWhoClicked();
                if (Objects.equals(event.getCurrentItem().getItemMeta().displayName(), Component.text("§c§lSuperwerkzeug | Weihnachten 2023"))) {
                    ItemManager.addItem(player, Items.SUPER_PICKAXE);
                } else if (Objects.equals(event.getCurrentItem().getItemMeta().displayName(), Component.text("§5§lEffektpaket (Stufe 1)"))) {
                    ItemManager.addItem(player, Items.RANDOM_EFFECT_PACKAGE);
                } else if (Objects.equals(event.getCurrentItem().getItemMeta().displayName(), Component.text("§b§l15x Farmwelt-Flugtickets"))) {
                    ItemManager.addItem(player, Items.X15_FLY_TICKETS);
                } else if(Objects.equals(event.getCurrentItem().getItemMeta().displayName(), Component.text("§b§l1 Monat VIP Premium"))) {
                    ItemManager.addItem(player, Items.ONE_MONTH_VIP_PREMIUM);
                } else if(Objects.equals(event.getCurrentItem().getItemMeta().displayName(), Component.text("§6Streak Schutzpunkt"))) {
                    ItemManager.addItem(player, Items.STREAK_SAVER);
                } else if(Objects.equals(event.getCurrentItem().getItemMeta().displayName(), Component.text("§5OP Gap"))) {
                    ItemManager.addItem(player, Items.ENCHANTED_GOLDEN_APPLE);
                } else if(Objects.equals(event.getCurrentItem().getItemMeta().displayName(), Component.text("§5Goldener Apfel"))) {
                    ItemManager.addItem(player, Items.GOLDEN_APPLE);
                }
            }
        } catch (NullPointerException ignored) {}
    }

    @EventHandler
    public void onAmountInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (event.getView().title().equals(Component.text("§c§lVOTE-SHOP §7> Anzahl"))) {
            if (!VoteInventories.getAmountInventory().contains(player.getUniqueId())) {
                VoteInventories.getAmountOfItems().remove(player.getUniqueId());
                VoteInventories.getCurrentItem().remove(player.getUniqueId());
            }
        }
    }

    @EventHandler
    public void onAmountInventoryClick(InventoryClickEvent event) {
        try {
            if (event.getView().title().equals(Component.text("§c§lVOTE-SHOP §7> Anzahl"))) {
                event.setCancelled(true);
                Player player = (Player) event.getWhoClicked();
                int currentAmount = VoteInventories.getAmountOfItems().get(player.getUniqueId());
                Items items = VoteInventories.getCurrentItem().get(player.getUniqueId());
                ItemStack item = new ItemStack(Material.valueOf(items.name()));
                if(Objects.equals(event.getCurrentItem().getItemMeta().displayName(), Component.text("§c§l- 64 §f§cStück"))) {
                    if(currentAmount > 64) {
                        VoteInventories.getAmountOfItems().put(player.getUniqueId(), currentAmount-64);
                    } else {
                        VoteInventories.getAmountOfItems().put(player.getUniqueId(), 1);
                    }
                    VoteInventories.getAmountInventory().add(player.getUniqueId());
                    VoteInventories.openAmount(player);
                } else if(Objects.equals(event.getCurrentItem().getItemMeta().displayName(), Component.text("§c§l- 16 §f§cStück"))) {
                    if(currentAmount > 16) {
                        VoteInventories.getAmountOfItems().put(player.getUniqueId(), currentAmount-16);
                    } else {
                        VoteInventories.getAmountOfItems().put(player.getUniqueId(), 1);
                    }
                    VoteInventories.getAmountInventory().add(player.getUniqueId());
                    VoteInventories.openAmount(player);
                } else if(Objects.equals(event.getCurrentItem().getItemMeta().displayName(), Component.text("§c§l- 1 §f§cStück"))) {
                    if(currentAmount > 1) {
                        VoteInventories.getAmountOfItems().put(player.getUniqueId(), currentAmount-1);
                    }
                    VoteInventories.getAmountInventory().add(player.getUniqueId());
                    VoteInventories.openAmount(player);
                } else if (Objects.equals(event.getCurrentItem().getItemMeta().displayName(), Component.text("§a§l+ 1 §f§aStück"))) {
                    if (currentAmount < 2304) {  
                        VoteInventories.getAmountOfItems().put(player.getUniqueId(), currentAmount + 1);
                    }
                    VoteInventories.getAmountInventory().add(player.getUniqueId());
                    VoteInventories.openAmount(player);
                } else if (Objects.equals(event.getCurrentItem().getItemMeta().displayName(), Component.text("§a§l+ 16 §f§aStück"))) {
                    VoteInventories.getAmountOfItems().put(player.getUniqueId(), Math.min(currentAmount + 16, 2304));
                    VoteInventories.getAmountInventory().add(player.getUniqueId());
                    VoteInventories.openAmount(player);
                } else if (Objects.equals(event.getCurrentItem().getItemMeta().displayName(), Component.text("§a§l+ 64 §f§aStück"))) {
                    VoteInventories.getAmountOfItems().put(player.getUniqueId(), Math.min(currentAmount + 64, 2304));
                    VoteInventories.getAmountInventory().add(player.getUniqueId());
                    VoteInventories.openAmount(player);
                } else if(Objects.equals(event.getCurrentItem().getItemMeta().displayName(), Component.text("§a§lEingabe bestätigen"))) {
                    int available = getAvailableSpace(player, item);
                    TokenManager tokenManager = new TokenManager(player);
                    ItemStack give = new ItemStack(item.getType(), currentAmount);
                    if(available >= currentAmount) {
                        player.closeInventory();
                        int cost = (items.getCost() * currentAmount);
                        tokenManager.remove(cost);
                        VoteInventories.getAmountOfItems().remove(player.getUniqueId());
                        player.getInventory().addItem(give);
                    } else {
                        ChatUtils.sendMessage(player, ChatUtils.ChatType.WARNING, "Du hast nicht genügend Platz.");
                    }
                }
            }
        } catch (NullPointerException ignored) {}
    }

    private int getAvailableSpace(Player player, ItemStack itemStack) {
        int availableSpace = 0;
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack slot = player.getInventory().getItem(i);
            if (slot == null) {
                availableSpace += itemStack.getMaxStackSize();
            } else if (slot.getType() == itemStack.getType() && slot.getAmount() < itemStack.getMaxStackSize()) {
                availableSpace += itemStack.getMaxStackSize() - slot.getAmount();
            }
        }

        return availableSpace;
    }
}
