package net.exsar.votesystem.features.objects;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerVoteData {

    private int token;
    private int votes;

    public PlayerVoteData(int token, int votes) {
        this.token = token;
        this.votes = votes;
    }
}
