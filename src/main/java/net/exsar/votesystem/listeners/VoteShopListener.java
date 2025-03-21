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
                ItemManager itemManager = new ItemManager();
                String displayName = ((net.kyori.adventure.text.TextComponent) Objects.requireNonNull(event.getCurrentItem().getItemMeta().displayName())).content();

                switch (displayName) {
                    case "§c§lSuperwerkzeug":
                        itemManager.addItem(player, Items.SUPER_PICKAXE);
                        break;
                    case "§5§lEffektpaket (Stufe 1)":
                        itemManager.addItem(player, Items.RANDOM_EFFECT_PACKAGE);
                        break;
                    case "§b§l15x Farmwelt-Flugtickets":
                        itemManager.addItem(player, Items.X15_FLY_TICKETS);
                        break;
                    case "§b§l1 Monat VIP Premium":
                        itemManager.addItem(player, Items.ONE_MONTH_VIP_PREMIUM);
                        break;
                    case "§6Streak Schutzpunkt":
                        itemManager.addItem(player, Items.STREAK_SAVER);
                        break;
                    case "§5OP Gap":
                        itemManager.addItem(player, Items.ENCHANTED_GOLDEN_APPLE);
                        break;
                    case "§5Goldener Apfel":
                        itemManager.addItem(player, Items.GOLDEN_APPLE);
                        break;
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
                String displayName = ((net.kyori.adventure.text.TextComponent) Objects.requireNonNull(event.getCurrentItem().getItemMeta().displayName())).content();

                switch (displayName) {
                    case "§c§l- 64 §f§cStück":
                        if(currentAmount > 64) {
                            VoteInventories.getAmountOfItems().put(player.getUniqueId(), currentAmount-64);
                        } else {
                            VoteInventories.getAmountOfItems().put(player.getUniqueId(), 1);
                        }
                        break;

                    case "§c§l- 16 §f§cStück":
                        if(currentAmount > 16) {
                            VoteInventories.getAmountOfItems().put(player.getUniqueId(), currentAmount-16);
                        } else {
                            VoteInventories.getAmountOfItems().put(player.getUniqueId(), 1);
                        }
                        break;

                    case "§c§l- 1 §f§cStück":
                        if(currentAmount > 1) {
                            VoteInventories.getAmountOfItems().put(player.getUniqueId(), currentAmount-1);
                        }
                        break;

                    case "§a§l+ 1 §f§cStück":
                        if (currentAmount < 2304) {
                            VoteInventories.getAmountOfItems().put(player.getUniqueId(), currentAmount + 1);
                        }
                        break;

                    case "§a§l+ 16 §f§cStück":
                        VoteInventories.getAmountOfItems().put(player.getUniqueId(), Math.min(currentAmount + 16, 2304));
                        break;

                    case "§a§l+ 64 §f§cStück":
                        VoteInventories.getAmountOfItems().put(player.getUniqueId(), Math.min(currentAmount + 64, 2304));
                        break;

                    case "§a§lEingabe bestätigen":
                        int available = getAvailableSpace(player, item);
                        int cost = (items.getCost() * currentAmount);
                        TokenManager tokenManager = new TokenManager(player);
                        ItemStack give = new ItemStack(item.getType(), currentAmount);
                        if(!tokenManager.check(cost)) {
                            ChatUtils.sendMessage(player, ChatUtils.ChatType.WARNING, "Du hast dafür zu wenig Tokens.");
                            return;
                        }

                        if(available >= currentAmount) {
                            player.closeInventory();

                            tokenManager.remove(cost);
                            VoteInventories.getAmountOfItems().remove(player.getUniqueId());
                            player.getInventory().addItem(give);
                        } else {
                            ChatUtils.sendMessage(player, ChatUtils.ChatType.WARNING, "Du hast nicht genügend Platz.");
                        }
                        break;
                }

                if(!displayName.equalsIgnoreCase("§a§lEingabe bestätigen")) {
                    VoteInventories.getAmountInventory().add(player.getUniqueId());
                    VoteInventories.openAmount(player);
                }
            }
        } catch (NullPointerException ignored) {}
    }

    private int getAvailableSpace(Player player, ItemStack itemStack) {
        int availableSpace = 0;
        int maxStackSize = itemStack.getMaxStackSize();

        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null || item.isEmpty()) {
                availableSpace += maxStackSize;
            } else if (item.getType() == itemStack.getType() && item.getAmount() < maxStackSize) {
                availableSpace += maxStackSize - item.getAmount();
            }
        }

        return availableSpace;
    }
}
