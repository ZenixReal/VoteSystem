package net.exsar.votesystem.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.*;
import org.json.simple.*;
import org.json.simple.parser.*;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.*;
import java.util.*;

public class SkullBuilder {
    private final String base64;
    private String displayName;
    private int amount;
    private List<String> lore;

    public SkullBuilder(String base64) {
        this.base64 = base64;
        this.displayName = null;
        this.lore = null;
        this.amount = 1;
    }

    public SkullBuilder displayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public SkullBuilder lore(String... lore) {
        this.lore = Arrays.asList(lore);
        return this;
    }
    public SkullBuilder amount(int amount) {
        this.amount = amount;
        return this;
    }

    public ItemStack build() {
        PlayerProfile profile = getProfile(this.base64);
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        meta.setOwnerProfile(profile);

        if (this.displayName != null) {
            meta.displayName(Component.text(this.displayName));
        }

        if (this.lore != null) {
            meta.setLore(this.lore);
        }
        head.setAmount(amount);

        head.setItemMeta(meta);
        return head;
    }

    private PlayerProfile getProfile(String url) {
        PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
        PlayerTextures textures = profile.getTextures();
        URL urlObject;
        try {
            urlObject = new URL(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid URL", e);
        }
        textures.setSkin(urlObject);
        profile.setTextures(textures);
        return profile;
    }
}
