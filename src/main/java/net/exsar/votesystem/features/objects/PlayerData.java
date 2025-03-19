package net.exsar.votesystem.features.objects;

import lombok.Getter;
import lombok.Setter;
import net.exsar.votesystem.utils.NumberUtils;

@Getter
@Setter
public class PlayerData {

    private long lastVote;
    private int vote;
    private int streak;
    private int tokens;
    private int saver;

    public PlayerData (long lastVote, int vote, int streak, int tokens, int saver) {
        this.lastVote = lastVote;
        this.vote = vote;
        this.streak = streak;
        this.tokens = tokens;
        this.saver = saver;
    }

    public boolean checkIfVoteStreakBroken(long currentTime) {
        long oneDayInMillis = 24 * 60 * 60 * 1000;
        return currentTime - lastVote > oneDayInMillis;
    }

    public String formattedTokens() {
        return NumberUtils.format(getTokens());
    }

    public void resetStreak() {
        this.streak = 1;
        this.lastVote = System.currentTimeMillis();
    }

    public void addStreak() {
        this.streak++;
        this.lastVote = System.currentTimeMillis();
    }

    public void addSaver() {
        this.saver++;
    }
    public void removeSaver() {
        this.saver--;
    }
}
