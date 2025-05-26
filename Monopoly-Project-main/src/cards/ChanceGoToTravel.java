package cards;

import core.GameEngine;
import core.GameEventListener;
import model.Player;

public class ChanceGoToTravel implements ChanceCard {
    @Override
    public String getDescription() {
        return "Go to Travel square";
    }

    @Override
    public void apply(Player player, GameEventListener ui, GameEngine engine) {
        player.setPosition(24);
        ui.updateLog("Chance: Go to Travel square.", java.awt.Color.BLUE);
        engine.handleSpecialSquares(engine.getProperties().get(24));
    }
}
