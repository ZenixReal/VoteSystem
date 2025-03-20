package net.exsar.votesystem.commands;

import net.exsar.votesystem.VoteSystem;
import net.exsar.votesystem.features.manager.TokenManager;
import net.exsar.votesystem.utils.ChatUtils;
import net.exsar.votesystem.utils.Command;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TokensAdminCommand extends Command<VoteSystem> {

    public TokensAdminCommand(String name, VoteSystem plugin) {
        super(name, plugin);
    }

    @Override
    public void execute(CommandSender sender, String prefix, String[] args) {
        if(!sender.hasPermission("votesystem.edittoken")) {
            ChatUtils.sendMessage(sender, ChatUtils.ChatType.ERROR, "Du hast dazu keine Berechtigung.");
            return;
        }
        if(args.length == 3) {
            if(args[2].startsWith("-")) {
                ChatUtils.sendMessage(sender, ChatUtils.ChatType.WARNING, "Es muss eine positive Zahl sein.");
                return;
            }
            try {
                OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                int amount = Integer.parseInt(args[2]);
                TokenManager tokenManager = new TokenManager(player);
                if(!player.hasPlayedBefore()) {
                    ChatUtils.sendMessage(sender, ChatUtils.ChatType.WARNING, "Dieser Spieler war zuvor noch nie auf dem Server gewesen.");
                    return;
                }
                switch (args[0]) {
                    case "add":
                        tokenManager.add(amount);
                        ChatUtils.sendMessage(sender, ChatUtils.ChatType.SUCCESS, "Du hast " + player.getName() + " Tokens i.H.v " + amount + " hinzugefügt.");
                        break;
                    case "remove":
                        if(amount > tokenManager.get()) {
                            ChatUtils.sendMessage(sender, ChatUtils.ChatType.WARNING, "Der Spieler besitzt nicht so viele Tokens, um abgezogen werden zu können.");
                            return;
                        }
                        tokenManager.remove(amount);
                        ChatUtils.sendMessage(sender, ChatUtils.ChatType.SUCCESS, "Du hast " + player.getName() + " Tokens i.H.v " + amount + " entfernt.");
                        break;
                    case "set":
                        tokenManager.set(amount);
                        ChatUtils.sendMessage(sender, ChatUtils.ChatType.SUCCESS, "Du hast " + player.getName() + " Tokens i.H.v " + amount + " gesetzt.");
                        break;
                    default:
                        ChatUtils.sendMessage(sender, ChatUtils.ChatType.WARNING, "/" + prefix + " set/add/remove " + player.getName() + " " + amount);
                        break;
                }
            } catch (NumberFormatException ignored) {
                ChatUtils.sendMessage(sender, ChatUtils.ChatType.WARNING, "Es muss eine Zahl sein.");
            }
        } else {
            ChatUtils.sendMessage(sender, ChatUtils.ChatType.WARNING, "/" + prefix + " set/add/remove <SPIELERNAME> <ANZAHL>");
        }
    }

    @Override
    public List<String> complete(CommandSender sender, String prefix, String[] args) {
        List<String> list = new ArrayList<>();
        if(sender.hasPermission("votesystem.edittoken")) {
            if (args.length==1) {
                list.addAll(Arrays.asList("add", "remove", "set"));
            } else if (args.length==2) {
                for(OfflinePlayer player : Bukkit.getOfflinePlayers()) {
                    list.add(player.getName());
                }
            } else if(args.length == 3) {
                list.add("ANZAHL");
            }
        }
        return list.stream()
                .filter(l -> l.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                .collect(Collectors.toList());
    }
}