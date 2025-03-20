package net.exsar.votesystem.features.manager;

import net.exsar.votesystem.features.objects.PlayerData;
import org.bukkit.OfflinePlayer;

public class TokenManager {

    private final OfflinePlayer player;

    public TokenManager(OfflinePlayer player) {
        this.player = player;
    }

    public int get() {
        return VoteManager.getData(player).getTokens();
    }

    public void add(int token) {
        PlayerData data = VoteManager.getData(player);
        data.setTokens(data.getTokens() + token);
        VoteManager.update(player, data);
    }

    public void remove(int token) {
        PlayerData data = VoteManager.getData(player);
        data.setTokens(data.getTokens() - token);
        VoteManager.update(player, data);
    }

    public void set(int token) {
        PlayerData data = VoteManager.getData(player);
        data.setTokens(token);
        VoteManager.update(player, data);
    }

    public boolean check(int token) {
        PlayerData data = VoteManager.getData(player);
        return data.getTokens() >= token;
    }
}
