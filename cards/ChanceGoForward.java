package cards;

import core.GameEngine;
import core.GameEventListener;
import model.Player;

public class ChanceGoForward implements ChanceCard {
    @Override
    public String getDescription() {
        return "Move forward 3 spaces";
    }

    @Override
    public void apply(Player player, GameEventListener ui, GameEngine engine) {
        int newPos = (player.getPosition() + 3) % 32;
        player.setPosition(newPos);
        ui.updateLog("Chance: Go forward 3 spaces.", java.awt.Color.BLUE);
        engine.handleSpecialSquares(engine.getProperties().get(newPos));
        ui.repaintBoard();

        engine.postMoveActions(player);
    }
}
