package net.exsar.votesystem.utils;

import net.exsar.votesystem.VoteSystem;
import org.bukkit.command.CommandSender;

public class ChatUtils {

    public static void sendMessage(CommandSender player, String message) {
        player.sendMessage(VoteSystem.getPrefix() + message);
    }
}
