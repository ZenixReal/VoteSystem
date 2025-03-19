package net.exsar.votesystem.features.objects;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VoteSiteData {

    private boolean first_site;
    private boolean second_site;

    public VoteSiteData(boolean first_site, boolean second_site) {
        this.first_site = first_site;
        this.second_site = second_site;
    }
}