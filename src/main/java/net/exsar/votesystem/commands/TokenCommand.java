package net.exsar.votesystem.commands;

import net.exsar.votesystem.VoteSystem;
import net.exsar.votesystem.features.VoteManager;
import net.exsar.votesystem.features.objects.PlayerVoteData;
import net.exsar.votesystem.utils.ChatUtils;
import net.exsar.votesystem.utils.Command;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class TokenCommand extends Command<VoteSystem> {

    public TokenCommand(String name, VoteSystem plugin) {
        super(name, plugin);
    }

    @Override
    public void execute(CommandSender sender, String prefix, String[] args) {
        if(args.length == 0) {
            if(sender instanceof Player player) {
                ChatUtils.sendMessage(player, ChatUtils.ChatType.INFO, "Du besitzt derzeit §l" + VoteManager.getData(player).getToken() + " Tokens§r§b.");
            }
        } else if(args.length == 1) {
            if (sender.hasPermission("votesystem.tokens.see")) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
                if (!player.hasPlayedBefore()) {
                    ChatUtils.sendMessage(sender, ChatUtils.ChatType.ERROR, "Dieser Spieler war noch nie auf dem Server.");
                    return;
                }
                ChatUtils.sendMessage(sender, ChatUtils.ChatType.INFO, "Der Spieler " + player.getName() + " besitzt derzeit §l" + VoteManager.getData(player).getToken() + " Tokens§r§b.");
            }
        } else if(args.length == 3) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
            int amount = 0;
            PlayerVoteData data = VoteManager.getData(player);
            if(!player.hasPlayedBefore()) {
                ChatUtils.sendMessage(sender, ChatUtils.ChatType.ERROR, "Dieser Spieler war noch nie auf dem Server.");
                return;
            }
            if(args[2].startsWith("-")) {
                ChatUtils.sendMessage(sender, ChatUtils.ChatType.WARNING, "Bitte gebe eine positive Zahl an.");
                return;
            }

            try {
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException ignored) {
                ChatUtils.sendMessage(sender, ChatUtils.ChatType.WARNING, "Bitte gebe eine Zahl an.");
            }
            if(args[0].equalsIgnoreCase("add")) {
                data.setToken(data.getToken() + amount);
                VoteManager.saveData(player);
                ChatUtils.sendMessage(sender, ChatUtils.ChatType.SUCCESS, "Der Spieler hat nun i.H.v " + amount + " Tokens erhalten.");
            } else if(args[0].equalsIgnoreCase("remove")) {
                data.setToken(data.getToken() - amount);
                VoteManager.saveData(player);
                ChatUtils.sendMessage(sender, ChatUtils.ChatType.SUCCESS, "Der Spieler hat nun i.H.v " + amount + " Tokens entfernt bekommen.");
            }
        }
    }

    @Override
    public List<String> complete(CommandSender sender, String prefix, String[] args) {
        return List.of();
    }
}
