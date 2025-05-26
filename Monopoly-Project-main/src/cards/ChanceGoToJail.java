package cards;

import core.GameEngine;
import core.GameEventListener;
import model.Player;

public class ChanceGoToJail implements ChanceCard {
    @Override
    public String getDescription() {
        return "Go to Jail for 3 turns";
    }

    @Override
    public void apply(Player player, GameEventListener ui, GameEngine engine) {
        player.setPosition(8);
        player.setTurnsInDungeon(3);
        ui.updateLog("Chance: Go to Jail!", java.awt.Color.RED);
        engine.handleSpecialSquares(engine.getProperties().get(8));
    }
}
