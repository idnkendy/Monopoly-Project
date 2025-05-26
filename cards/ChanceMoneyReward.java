package cards;

import core.GameEngine;
import core.GameEventListener;
import model.Player;

public class ChanceMoneyReward implements ChanceCard {
    @Override
    public String getDescription() {
        return "Bank pays you dividend of $100";
    }

    @Override
    public void apply(Player player, GameEventListener ui, GameEngine engine) {
        player.addMoney(100);
        ui.updateLog("Chance: Bank pays you dividend of $100", java.awt.Color.BLUE);
    }
}