package core;

import model.Player;

import javax.swing.*;
import java.awt.*;

public interface GameEventListener {
    void updateLog(String message, Color color);
    void updatePlayerInfo();
    void repaintBoard();
    void showTravelDialog(Player player);

    JButton getRollDiceButton();
    JButton getEndTurnButton();
    JButton getBuyPropertyButton();
    JButton getUpgradeHouseButton();

    void setDiceResult(int d1, int d2);
    void hideDice();
    void animateDiceRoll(int d1, int d2, Runnable onFinish); // nếu dùng

    void setCurrentPlayerIndex(int index);
}
