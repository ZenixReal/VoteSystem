package net.exsar.votesystem.utils;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;

public class SkullDatabase {
    public static ItemStack build(Skulls skull, String name, String... lore) {
        return new SkullBuilder(skull.getLink())
                .displayName(name)
                .lore(lore).build();
    }

    public static ItemStack build(Skulls skull, String name, int amount, String... lore) {
        return new SkullBuilder(skull.getLink())
                .displayName(name)
                .amount(amount)
                .lore(lore).build();
    }

    @Getter
    public enum Skulls {
        WHITE_PLUS("http://textures.minecraft.net/texture/60b55f74681c68283a1c1ce51f1c83b52e2971c91ee34efcb598df3990a7e7");

        private final String link;

        Skulls(final String link) {
            this.link = link;
        }
    }
}
