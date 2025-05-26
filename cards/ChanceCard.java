package cards;

import core.GameEngine;
import core.GameEventListener;
import model.Player;

public interface ChanceCard {
    void apply(Player player, GameEventListener ui, GameEngine engine);
    String getDescription();
}
