package net.exsar.votesystem.features.objects;


import lombok.Getter;
import net.exsar.votesystem.utils.NumberUtils;

@Getter
public enum Items {

    /*
     * 1 Token umgerechnet = 70 € Ingame-Geld
     * unique = einmalig kaufbar.
     * cost = pro Item / (15 Tickets = 1 Token, 1 Monat VIP Premium = 1.500 Tokens)
     */


    SUPER_PICKAXE(1300, true, true),
    ONE_MONTH_VIP_PREMIUM(1500, true, false),
    RANDOM_EFFECT_PACKAGE(250, false, false),
    X15_FLY_TICKETS(2, false, false),
    STREAK_SAVER(75, false, false),
    GOLDEN_APPLE(2, false, true),
    ENCHANTED_GOLDEN_APPLE(20, false, true);

    private final int cost;
    private final boolean unique;
    private final boolean physical;
    Items(int cost, boolean unique, boolean physical) {
        this.cost = cost;
        this.unique = unique;
        this.physical = physical;
    }

    public String formattedCost() {
        return NumberUtils.format(getCost());
    }
}