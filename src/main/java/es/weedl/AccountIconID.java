package es.weedl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.IconID;

@RequiredArgsConstructor
@Getter
public enum AccountIconID {
    IRONMAN(IconID.IRONMAN.getIndex()),
    ULTIMATE_IRONMAN(IconID.ULTIMATE_IRONMAN.getIndex()),
    HARDCORE_IRONMAN(IconID.HARDCORE_IRONMAN.getIndex()),
    GROUP_IRONMAN(41),
    HARDCORE_GROUP_IRONMAN(42),
    UNRANKED_GROUP_IRONMAN(43),
    LEAGUE(IconID.LEAGUE.getIndex());

    private final int index;

    @Override
    public String toString()
    {
        return "<img=" + String.valueOf(this.index) + ">";
    }
}