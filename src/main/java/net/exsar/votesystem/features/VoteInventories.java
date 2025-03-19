package net.exsar.votesystem.features;

import lombok.Getter;
import net.exsar.votesystem.features.manager.VoteManager;
import net.exsar.votesystem.features.objects.*;
import net.exsar.votesystem.utils.*;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class VoteInventories {

    @Getter
    private static HashMap<UUID, Integer> amountOfItems = new HashMap<>();
    @Getter
    private static List<UUID> amountInventory = new ArrayList<>();
    @Getter
    private static HashMap<UUID, Items> currentItem = new HashMap<>();

    public static void openShop(Player player) {
        PlayerData data = VoteManager.getData(player);
        ItemStack walls = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();

        Inventory inventory = Bukkit.createInventory(null, 5 * 9, Component.text("§c§lVOTE-SHOP"));
        for (int i = 0; i<9; i++) inventory.setItem(i, walls);
        inventory.setItem(9, walls);
        inventory.setItem(17, walls);
        inventory.setItem(18, walls);
        inventory.setItem(26, walls);
        inventory.setItem(27, walls);
        inventory.setItem(35, walls);
        for (int i = 36; i<45; i++) inventory.setItem(i, walls);
        inventory.setItem(8, new SkullBuilder("http://textures.minecraft.net/texture/16439d2e306b225516aa9a6d007a7e75edd2d5015d113b42f44be62a517e574f")
                .displayName("§9§lINFO")
                .lore(
                        "§7- Tokens: §f" + NumberUtils.format(data.getTokens()),
                        "§7- Zuletzt Streak aufgebaut: §f" + NumberUtils.timeFromLong(data.getLastVote()),
                        "§7- Aktueller Streak: §f" + NumberUtils.format(data.getStreak()),
                        "§7- Streak-Schutzpunkt: §f" + NumberUtils.format(data.getSaver()),
                        "§7- Votes: §f" + NumberUtils.format(data.getVote())
                )
                .build());

        inventory.addItem(
                new ItemBuilder(Material.DIAMOND_PICKAXE)
                        .displayName("§c§lSuperwerkzeug | Weihnachten 2023")
                        .lore(
                                "§r",
                                "§7Preis: §f" + Items.SUPER_PICKAXE.formattedCost() + " Tokens",
                                "§e⚠ §cNur einmalig kaufbar."
                        )
                        .addEnchant(Enchantment.MENDING, 1)
                        .addEnchant(Enchantment.UNBREAKING, 3)
                        .addEnchant(Enchantment.EFFICIENCY, 5)
                        .addEnchant(Enchantment.FORTUNE, 4)
                        .build()
        );
        inventory.addItem(
                new ItemBuilder(Material.DIAMOND)
                        .displayName("§b§l1 Monat VIP Premium")
                        .lore(
                                "§r",
                                "§7Preis: §f" + Items.ONE_MONTH_VIP_PREMIUM.formattedCost() + " Tokens",
                                "§e⚠ §cNur einmalig kaufbar."
                        )
                        .build()
        );
        inventory.addItem(
                new ItemBuilder(Material.PAPER)
                        .displayName("§6Streak Schutzpunkt")
                        .lore(
                                "§r",
                                "§7Es ist nicht schlimm, wenn du ein Tag nicht votest.",
                                "§7Dieser Schutzpunkt beschützt für ein Tag dein Vote-Streak.",
                                "§7Preis: §f" + Items.STREAK_SAVER.formattedCost() + " Tokens"
                        )
                        .build()
        );

        inventory.addItem(
                new ItemBuilder(Material.ENCHANTED_GOLDEN_APPLE)
                        .displayName("§5OP Gap")
                        .lore(
                                "§r",
                                "§7Preis: §f" + Items.ENCHANTED_GOLDEN_APPLE.formattedCost() + " Tokens / OP Gap"
                        )
                        .build()
        );
        inventory.addItem(
                new ItemBuilder(Material.GOLDEN_APPLE)
                        .displayName("§5Goldener Apfel")
                        .lore(
                                "§r",
                                "§7Preis: §f" + Items.GOLDEN_APPLE.formattedCost() + " Tokens / Goldener Apfel"
                        )
                        .build()
        );
        inventory.addItem(
                new ItemBuilder(Material.FIREWORK_ROCKET)
                        .displayName("§5§lEffektpaket (Stufe 1)")
                        .lore(
                                "§r",
                                "§7Kaufe dir jetzt einen zufälligen Effektpaket.",
                                "§7Preis: §f" + Items.RANDOM_EFFECT_PACKAGE.formattedCost() + " Tokens"
                        )
                        .flag(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP)
                        .build()
        );
        inventory.addItem(
                new ItemBuilder(Material.PAPER)
                        .displayName("§b§l15x Farmwelt-Flugtickets")
                        .lore(
                                "",
                                "§7Hole dir 15 Flugtickets für Farmwelt.",
                                "§7Preis: §f" + Items.X15_FLY_TICKETS.formattedCost() + " Tokens")
                        .build()
        );
        InventoryTool.fillEmptySlots(inventory);
        player.openInventory(inventory);
    }

    public static void openAmount(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 27, Component.text("§c§lVOTE-SHOP §7> Anzahl"));
        if(!amountOfItems.containsKey(player.getUniqueId())) amountOfItems.put(player.getUniqueId(), 1);
        int amount = amountOfItems.get(player.getUniqueId());
        Items items = VoteInventories.getCurrentItem().get(player.getUniqueId());
        ItemStack itemStack = new ItemStack(Material.BARRIER);
        for(int i = 0; i < inventory.getSize(); i++) inventory.setItem(i, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build());
        if(items.equals(Items.GOLDEN_APPLE)) {
            itemStack = new ItemBuilder(Material.GOLDEN_APPLE).build();
        } else if(items.equals(Items.ENCHANTED_GOLDEN_APPLE)) {
            itemStack = new ItemBuilder(Material.ENCHANTED_GOLDEN_APPLE).build();
        }
        itemStack.setAmount(Math.min(amount, 64));

        inventory.setItem(1, SkullDatabase.build(SkullDatabase.Skulls.WHITE_PLUS, "§c§l- 64 §f§cStück", 64, "§7Anzahl: §f§l" + amount + " §8(Maximum: §f§l2.304§8)"));
        inventory.setItem(2, SkullDatabase.build(SkullDatabase.Skulls.WHITE_PLUS, "§c§l- 16 §f§cStück", 16, "§7Anzahl: §f§l" + amount + " §8(Maximum: §f§l2.304§8)"));
        inventory.setItem(3, SkullDatabase.build(SkullDatabase.Skulls.WHITE_PLUS, "§c§l- 1 §f§cStück", 1, "§7Anzahl: §f§l" + amount + " §8(Maximum: §f§l2.304§8)"));
        inventory.setItem(4, itemStack);
        inventory.setItem(5, SkullDatabase.build(SkullDatabase.Skulls.WHITE_PLUS, "§a§l+ 1 §f§aStück", 1, "§7Anzahl: §f§l" + amount + " §8(Maximum: §f§l2.304§8)"));
        inventory.setItem(6, SkullDatabase.build(SkullDatabase.Skulls.WHITE_PLUS, "§a§l+ 16 §f§aStück", 16, "§7Anzahl: §f§l" + amount + " §8(Maximum: §f§l2.304§8)"));
        inventory.setItem(7, SkullDatabase.build(SkullDatabase.Skulls.WHITE_PLUS, "§a§l+ 64 §f§aStück", 64, "§7Anzahl: §f§l" + amount + " §8(Maximum: §f§l2.304§8)"));

        inventory.setItem(18, new ItemBuilder(Material.BARRIER).displayName("§c§lEingabe beenden").build());
        inventory.setItem(26, new ItemBuilder(Material.EMERALD_BLOCK).displayName("§a§lEingabe bestätigen").lore("§7Anzahl: §f§l" + amount).build());

        player.openInventory(inventory);
    }

}
