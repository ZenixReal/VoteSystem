package net.exsar.votesystem.commands;

import net.exsar.votesystem.VoteSystem;
import net.exsar.votesystem.features.VoteInventories;
import net.exsar.votesystem.features.manager.*;
import net.exsar.votesystem.features.objects.*;
import net.exsar.votesystem.utils.ChatUtils;
import net.exsar.votesystem.utils.Command;
import net.exsar.votesystem.utils.NumberUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.*;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
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
                    ChatUtils.sendMessage(player, "Du hast bereits gevotet.");
                    return;
                }
                if(!voteSiteData.isFirst_site()) {
                    site1 = Component.text("§a§l[LINK 1]")
                            .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/vote set 1"))
                            .hoverEvent(HoverEvent.showText(Component.text("§7§oKlicke, um auf die 1. Seite abzustimmen.")));
                } else {
                    site1 = Component.text("§c§l§m[LINK 1]")
                            .hoverEvent(HoverEvent.showText(Component.text("§7§oDu kannst auf diese Seite nicht mehr voten.")));
                }
                if(!voteSiteData.isSecond_site()) {
                    site2 = Component.text("§a§l[LINK 2]")
                            .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/vote set 2"))
                            .hoverEvent(HoverEvent.showText(Component.text("§7§oKlicke, um auf die 2. Seite abzustimmen.")));
                } else {
                    site2 = Component.text("§c§l§m[LINK 2]")
                            .hoverEvent(HoverEvent.showText(Component.text("§7§oDu kannst auf diese Seite nicht mehr voten.")));
                }
                player.sendMessage(
                        Component.text("    ")
                                .append(site1)
                                .append(Component.text("    "))
                                .append(site2)
                );
            } else if(args.length == 1) {
                if(args[0].equalsIgnoreCase("shop")) {
                    VoteInventories.openShop(player);
                }
            } else if(args.length == 2) {
                switch (args[0]) {
                    case "info":
                        Player target = Bukkit.getPlayer(args[1]);
                        if (!player.hasPermission("votesystem.view")) {
                            ChatUtils.sendMessage(player, "§cDu hast dazu keine Berechtigung.");
                            return;
                        }
                        if (target == null || !target.isOnline()) {
                            ChatUtils.sendMessage(player, "§cDer Spieler ist nicht Online.");
                            return;
                        }
                        PlayerData data = VoteManager.getData(target);
                        VoteSiteData viewSiteData = VoteManager.getVoteSite(target);
                        if(voteSiteData == null) {
                            VoteSiteData newData = new VoteSiteData(false, false);
                            VoteManager.getVoteSiteDataHashMap().put(target.getUniqueId(), newData);
                            viewSiteData = newData;
                        }
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
                        break;
                    case "set":
                        TokenManager tokenManager = new TokenManager(player);
                        PlayerData playerData = VoteManager.getData(player);
                        VoteManager voteManager = new VoteManager(player);
                        VoteStreakManager voteStreakManager = new VoteStreakManager(player);
                        if(args[1].equalsIgnoreCase("1")) {
                            if(!voteSiteData.isFirst_site()) {
                                ChatUtils.sendMessage(player, "Vielen Dank fürs Voten! Als dank erhältst du " + voteManager.getStreakToken() + " Vote-Token!");
                                tokenManager.add(voteManager.getStreakToken());
                                voteSiteData.setFirst_site(true);
                                playerData.setVote(playerData.getVote() + 1);
                                voteManager.update(playerData);
                                if(voteSiteData.isSecond_site()) {
                                    voteStreakManager.addVoteStreak();
                                }
                                VoteManager.getVoteSiteDataHashMap().put(player.getUniqueId(), voteSiteData);
                            } else {
                                ChatUtils.sendMessage(player, "Du hast bereits gevotet.");
                            }
                        } else if(args[1].equalsIgnoreCase("2")) {
                            if(!voteSiteData.isSecond_site()) {
                                ChatUtils.sendMessage(player, "Vielen Dank fürs Voten! Als dank erhältst du " + voteManager.getStreakToken() + " Vote-Token!");
                                tokenManager.add(voteManager.getStreakToken());
                                voteSiteData.setSecond_site(true);
                                playerData.setVote(playerData.getVote() + 1);
                                voteManager.update(playerData);
                                if(voteSiteData.isFirst_site()) {
                                    voteStreakManager.addVoteStreak();
                                }
                                VoteManager.getVoteSiteDataHashMap().put(player.getUniqueId(), voteSiteData);
                            } else {
                                ChatUtils.sendMessage(player, "Du hast bereits gevotet.");
                            }
                        }
                        break;
                }
            }
        }
    }

    @Override
    public List<String> complete(CommandSender sender, String prefix, String[] args) {
        List<String> list = new ArrayList<>();
        if(args.length == 1) {
            list.add("shop");
            if(sender.hasPermission("votesystem.view")) {
                list.add("info");
            }
        }
        return list.stream()
                .filter(l -> l.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                .collect(Collectors.toList());
    }
}
