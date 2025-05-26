package cards;

import core.GameEngine;
import core.GameEventListener;
import model.Player;

public class ChancePayFine implements ChanceCard {
    @Override
    public String getDescription() {
        return "Pay $200 taxi fine";
    }

    @Override
    public void apply(Player player, GameEventListener ui, GameEngine engine) {
        engine.askPlayerToSellAssetsUntilEnough(player, 200, null);
        ui.updateLog("Chance: Pay taxi cost $200", java.awt.Color.RED);
    }
}
