package net.exsar.votesystem.features.objects;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerVoteStreakData {

    private long lastVoteTime;
    private int streak;

    public PlayerVoteStreakData(long lastVoteTime, int streak) {
        this.lastVoteTime = lastVoteTime;
        this.streak = streak;
    }

    public boolean checkIfVoteStreakBroken(long currentTime) {
        long oneDayInMillis = 24 * 60 * 60 * 1000;
        return currentTime - lastVoteTime > oneDayInMillis;
    }

    public void resetStreak() {
        this.streak = 1;
        this.lastVoteTime = System.currentTimeMillis();
    }

    public void addStreak() {
        this.streak++;
        this.lastVoteTime = System.currentTimeMillis();
    }
}
