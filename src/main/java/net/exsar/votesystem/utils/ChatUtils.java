package net.exsar.votesystem.utils;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatUtils {

    public static void sendMessage(Player player, ChatType type, String message, boolean sound) {
        player.sendMessage(type.getPrefix() + message);
        if(sound) {
            player.playSound(player.getLocation(), type.getSound(), 1f, 1f);
        }
    }
    public static void sendMessage(CommandSender player, ChatType type, String message) {
        player.sendMessage(type.getPrefix() + message);
        if (player instanceof Player) {
            ((Player)player).playSound(((Player)player).getLocation(), type.getSound(), 1f, 1f);
        }
    }

    @Getter
    public enum ChatType {
        SUCCESS("§a§l» §2§l✔ §a", "notification_success"),
        INFO("§b§l» §fℹ §b", "notification_info"),
        QUESTION("§b§l» §9§l? §b", "notification_info"),
        ERROR("§c§l» §4§l✖ §c", "notification_error"),
        WARNING("§e§l» §4§l⚠ §e", "notification_warning");

        private final String prefix;
        private final String sound;

        ChatType(String prefix, String sound) {
            this.prefix = prefix;
            this.sound = sound;
        }
    }

}
