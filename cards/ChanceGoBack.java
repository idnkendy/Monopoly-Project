package cards;

import core.GameEngine;
import core.GameEventListener;
import model.Player;

public class ChanceGoBack implements ChanceCard {
    @Override
    public String getDescription() {
        return "Move back 3 spaces";
    }

    @Override
    public void apply(Player player, GameEventListener ui, GameEngine engine) {
        int newPos = (player.getPosition() - 3 + 32) % 32;
        player.setPosition(newPos);
        ui.updateLog("Chance: Go back 3 spaces.", java.awt.Color.BLUE);
        engine.handleSpecialSquares(engine.getProperties().get(newPos));
        ui.repaintBoard();

        engine.postMoveActions(player);
    }
}
