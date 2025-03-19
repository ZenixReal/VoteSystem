package net.exsar.votesystem.commands;

import net.exsar.votesystem.VoteSystem;
import net.exsar.votesystem.features.VoteInventories;
import net.exsar.votesystem.features.manager.TokenManager;
import net.exsar.votesystem.features.manager.VoteManager;
import net.exsar.votesystem.features.objects.PlayerData;
import net.exsar.votesystem.features.objects.VoteSiteData;
import net.exsar.votesystem.utils.ChatUtils;
import net.exsar.votesystem.utils.Command;
import net.exsar.votesystem.utils.DatabaseManager;
import net.exsar.votesystem.utils.NumberUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class VoteCommand extends Command<VoteSystem> {

    public VoteCommand(String name, VoteSystem plugin) {
        super(name, plugin);
    }

    @Override
    public void execute(CommandSender sender, String prefix, String[] args) {
        if(sender instanceof Player player) {
            VoteSiteData voteSiteData = VoteManager.getVoteSite(player);
            if(args.length == 0) {
                TextComponent site1;
                TextComponent site2;
                if(voteSiteData.isFirst_site() && voteSiteData.isSecond_site()) {
                    ChatUtils.sendMessage(player, ChatUtils.ChatType.INFO, "Du hast bereits gevotet.");
                    return;
                }
                if(!voteSiteData.isFirst_site()) {
                    site1 = Component.text("§a§l[LINK 1]")
                            .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/vote set 1 true"))
                            .hoverEvent(HoverEvent.showText(Component.text("§7§oKlicke, um auf die 1. Seite abzustimmen.")));
                } else {
                    site1 = Component.text("§c§l§m[LINK 1]")
                            .hoverEvent(HoverEvent.showText(Component.text("§7§oDu kannst auf diese Seite nicht mehr voten.")));
                }
                if(!voteSiteData.isSecond_site()) {
                    site2 = Component.text("§a§l[LINK 2]")
                            .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/vote set 2 true"))
                            .hoverEvent(HoverEvent.showText(Component.text("§7§oKlicke, um auf die 2. Seite abzustimmen.")));
                } else {
                    site2 = Component.text("§c§l§m[LINK 2]")
                            .hoverEvent(HoverEvent.showText(Component.text("§7§oDu kannst auf diese Seite nicht mehr voten.")));
                }
                player.sendMessage(Component.text("    ").append(site1).append(Component.text("    ")).append(site2));
            } else if(args.length == 1) {
                if(args[0].equalsIgnoreCase("shop")) {
                    VoteInventories.openShop(player);
                }
            } else if(args.length == 2) {
                if(args[0].equalsIgnoreCase("info")) {
                    OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                    if (!player.hasPermission("votesystem.view")) {
                        ChatUtils.sendMessage(player, ChatUtils.ChatType.ERROR, "Du hast dazu keine Berechtigung.");
                        return;
                    }
                    if (!target.hasPlayedBefore()) {
                        ChatUtils.sendMessage(player, ChatUtils.ChatType.ERROR, "Dieser Spieler war zuvor noch nicht auf dem Server gewesen.");
                        return;
                    }
                    PlayerData data = VoteManager.getData(target);
                    VoteSiteData viewSiteData = VoteManager.getVoteSite(target);

                    String first = "§cNein";
                    String second = "§cNein";

                    if (viewSiteData.isFirst_site()) {
                        first = "§aJa";
                    }
                    if (viewSiteData.isSecond_site()) {
                        second = "§aJa";
                    }

                    player.sendMessage("                   §6»  §eVote-Informationen von " + target.getName() + "  §6«");
                    player.sendMessage("§r§e§m                               §r");
                    player.sendMessage("§7 - Tokens: §f" + data.formattedTokens());
                    player.sendMessage("§7 - Vote-Streak: §r" + data.getStreak());
                    player.sendMessage("§7 - Votes: §f" + data.getVote());
                    player.sendMessage("§7 - Vote-Streak Schutzpunkt: §f" + data.getSaver());
                    player.sendMessage("§7 - Zuletzt Vote-Streak aufgebaut: " + NumberUtils.timeFromLong(data.getLastVote()));
                    player.sendMessage("§7 - 1. Seite: §f" + first);
                    player.sendMessage("§7 - 2. Seite: §f" + second);
                }
            } else if(args.length == 3) {
                TokenManager tokenManager = new TokenManager(player);
                PlayerData data = VoteManager.getData(player);
                // "/votes set 2 true"
                if(args[0].equalsIgnoreCase("set")) {
                   if(args[1].equalsIgnoreCase("1")) {
                       boolean toggle = Boolean.parseBoolean(args[2]);
                       if(toggle && !voteSiteData.isFirst_site()) {
                           ChatUtils.sendMessage(player, ChatUtils.ChatType.SUCCESS, "Vielen Dank fürs Voten! Als dank erhältst du " + VoteManager.getStreakToken(player) + " Vote-Token!");
                           tokenManager.add(VoteManager.getStreakToken(player));
                           voteSiteData.setFirst_site(true);
                           data.setVote(data.getVote() + 1);
                           VoteManager.update(player, data);
                           if(voteSiteData.isSecond_site()) {
                               VoteManager.addVoteStreak(player);
                           }
                       } else {
                           voteSiteData.setFirst_site(false);
                           data.setVote(data.getVote() - 1);
                           VoteManager.update(player, data);
                           tokenManager.remove(VoteManager.getStreakToken(player));
                           ChatUtils.sendMessage(player, ChatUtils.ChatType.SUCCESS, "Du hast deine Vote-Seite entfernt.");
                       }
                       VoteManager.getVoteSiteDataHashMap().put(player.getUniqueId(), voteSiteData);
                   } else if(args[1].equalsIgnoreCase("2")) {
                       boolean toggle = Boolean.parseBoolean(args[2]);
                       if(toggle && !voteSiteData.isSecond_site()) {
                           ChatUtils.sendMessage(player, ChatUtils.ChatType.SUCCESS, "Vielen Dank fürs Voten! Als dank erhältst du " + VoteManager.getStreakToken(player) + " Vote-Token!");
                           tokenManager.add(VoteManager.getStreakToken(player));
                           voteSiteData.setSecond_site(true);
                           data.setVote(data.getVote() + 1);
                           VoteManager.update(player, data);
                           if(voteSiteData.isFirst_site()) {
                               VoteManager.addVoteStreak(player);
                           }

                       } else {
                           voteSiteData.setSecond_site(false);
                           data.setVote(data.getVote() - 1);
                           VoteManager.update(player, data);
                           tokenManager.remove(VoteManager.getStreakToken(player));
                           ChatUtils.sendMessage(player, ChatUtils.ChatType.SUCCESS, "Du hast deine Vote-Seite entfernt.");
                       }
                       VoteManager.getVoteSiteDataHashMap().put(player.getUniqueId(), voteSiteData);
                   }
                }
            }
        }
    }

    @Override
    public List<String> complete(CommandSender sender, String prefix, String[] args) {
        List<String> list = new ArrayList<>();
        if(args.length == 1) {
            list.add("shop");
        }
        return list.stream()
                .filter(l -> l.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                .collect(Collectors.toList());
    }
}
