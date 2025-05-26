package cards;

import core.GameEngine;
import core.GameEventListener;
import model.Player;

public class ChanceAdvanceToGo implements ChanceCard {
    @Override
    public String getDescription() {
        return "Advance to GO and collect $300";
    }

    @Override
    public void apply(Player player, GameEventListener ui, GameEngine engine) {
        player.setPosition(0);
        player.addMoney(300);
        ui.updateLog("Chance: Advance to GO. Collect $300", java.awt.Color.GREEN);
        engine.handleSpecialSquares(engine.getProperties().get(0));
    }
}