package net.exsar.votesystem.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class ItemBuilder {

    private final  ItemStack itemStack;
    private final ItemMeta itemMeta;

    public ItemBuilder(Material material) {
        this.itemStack = new ItemStack(material);
        this.itemMeta = itemStack.getItemMeta();
    }

    public ItemBuilder displayName(String displayName) {
        itemMeta.displayName(Component.text(displayName));
        return this;
    }
    public ItemBuilder lore(String... lore) {
        this.itemMeta.setLore(Arrays.asList(lore));
        return this;
    }
    public ItemBuilder flag(ItemFlag... flags) {
        itemMeta.addItemFlags(flags);
        return this;
    }
    public ItemBuilder durability(int durability) {
        itemStack.setDurability((short) durability);
        return this;
    }
    public ItemBuilder addEnchant(Enchantment enchantment, int level) {
        itemMeta.addEnchant(enchantment, level, true);
        return this;
    }

    public ItemStack build() {
        this.itemStack.setItemMeta(this.itemMeta);
        return this.itemStack;
    }

}
