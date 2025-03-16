package net.exsar.votesystem.utils;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Command<T extends JavaPlugin> implements CommandExecutor, TabExecutor {

    public static final List<String> players = new ArrayList<>(Collections.singletonList("--players--"));
    public Command(String name, T plugin) {
        plugin.getCommand(name).setExecutor(this);
    }

    public abstract void execute(CommandSender sender, String prefix, String[] args);

    public abstract List<String> complete(CommandSender sender, String prefix, String[] args);

    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        execute(sender, label, args);
        return true;
    }

    public List<String> onTabComplete(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {
        List<String> result = this.complete(commandSender, s, strings);
        if (result == null) {
            return Collections.emptyList();
        } else {
            return result.equals(players) ? null : result;
        }
    }
}
