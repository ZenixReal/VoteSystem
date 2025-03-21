package net.exsar.votesystem.features.manager;

import net.exsar.votesystem.features.objects.PlayerData;
import org.bukkit.OfflinePlayer;

public class TokenManager {

    private final OfflinePlayer player;
    private final VoteManager voteManager;

    public TokenManager(OfflinePlayer player) {
        this.player = player;
        this.voteManager = new VoteManager(player);
    }

    public int get() {
        return VoteManager.getData(player).getTokens();
    }

    public void add(int token) {
        PlayerData data = VoteManager.getData(player);
        data.setTokens(data.getTokens() + token);
        voteManager.update(data);
    }

    public void remove(int token) {
        PlayerData data = VoteManager.getData(player);
        data.setTokens(data.getTokens() - token);
        voteManager.update(data);
    }

    public void set(int token) {
        PlayerData data = VoteManager.getData(player);
        data.setTokens(token);
        voteManager.update(data);
    }

    public boolean check(int token) {
        PlayerData data = VoteManager.getData(player);
        return data.getTokens() >= token;
    }
}
