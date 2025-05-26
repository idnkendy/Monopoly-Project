package core;

import audio.*;
import model.*;
import ui.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameEngine {
    private final GameEventListener ui;
    private final List<Player> players;
    private final List<Property> properties;
    private int currentPlayerIndex;
    private final Random random;

    public GameEngine(GameEventListener ui, List<Player> players, List<Property> properties) {
        this.ui = ui;
        this.players = players;
        this.properties = properties;
        this.currentPlayerIndex = 0;
        this.random = new Random();
    }

public void rollDice() {
    Player currentPlayer = players.get(currentPlayerIndex);
    ui.getRollDiceButton().setEnabled(false);
    
    if (currentPlayer.isInDungeon()) {
        ui.updateLog(currentPlayer.getName() + " is in Dungeon. Wait " + currentPlayer.getTurnsInDungeon() + " more turn(s).", Color.BLACK);
        ui.getRollDiceButton().setEnabled(false);
        ui.getEndTurnButton().setEnabled(true);
        return;
    }

    if (currentPlayer.hasTravelChoice()) {
        currentPlayer.setTravelChoice(false);
        ui.showTravelDialog(currentPlayer);
        ui.getRollDiceButton().setEnabled(true);
        ui.getEndTurnButton().setEnabled(false);
        ui.getBuyPropertyButton().setEnabled(false);
        ui.repaintBoard();
        return;
    }

    int dice1 = random.nextInt(6) + 1;
    int dice2 = random.nextInt(6) + 1;
    int total = dice1 + dice2;

    ui.updateLog(currentPlayer.getName() + " is rolling the dice...", Color.BLACK);
    ui.setDiceResult(dice1, dice2);  // Hiển thị xúc xắc trên UI

    ((MonopolyGame) ui).animateDiceRoll(dice1, dice2, () -> {
        ui.updateLog(currentPlayer.getName() + " rolled " + dice1 + " + " + dice2 + " = " + total, Color.BLACK);
        movePlayer(currentPlayer, total);
    });

}

public void handleDiceResult(int dice1, int dice2) {
    Player currentPlayer = players.get(currentPlayerIndex);
    int total = dice1 + dice2;

    ui.updateLog(currentPlayer.getName() + " rolled " + dice1 + " + " + dice2 + " = " + total, Color.BLACK);

    movePlayer(currentPlayer, total);
}

private int calculateRent(Property property) {

    if (property.getType() == PropertyType.CIRCUS) {
        Player owner = property.getOwner();
        long count = properties.stream()
            .filter(p -> p.getOwner() == owner && p.getType() == PropertyType.CIRCUS)
            .count();

        switch ((int) count) {
            case 1: return 25;
            case 2: return 50;
            case 3: return 100;
            case 4: return 200;
        }
    }

     // Các loại đất thường
    int base = property.getCost() / 2;
    int rent = base + property.getHouseLevel() * (base / 2);

    if (property.isDoubleRent()) rent *= 2;
    if (isMonopoly(property.getColor(), property.getOwner())) rent *= 2;

    return rent;
}



private void movePlayer(Player currentPlayer, int steps) {
    Timer timer = new Timer(200, null);
    final int[] step = {0};

    timer.addActionListener(e -> {
        if (step[0] < steps) {
            int oldPos = currentPlayer.getPosition();
            int newPos = (oldPos + 1) % properties.size();

            if (newPos == 0) {
                currentPlayer.addMoney(300);
                ui.updateLog(currentPlayer.getName() + " passed GO and collected $300", Color.BLACK);
            }

            currentPlayer.setPosition(newPos);
            ui.repaintBoard();
            AudioUtil.playSound("../assets/footstep.wav");
            step[0]++;
        } else {
            ((Timer) e.getSource()).stop();
            Property landed = properties.get(currentPlayer.getPosition());
            ui.updateLog(currentPlayer.getName() + " landed on " + landed.getName(), Color.BLACK);
            handleSpecialSquares(landed);

            if (landed.getOwner() != null && landed.getOwner() != currentPlayer) {
                int rent = calculateRent(landed);
                boolean paid = askPlayerToSellAssetsUntilEnough(currentPlayer, rent, landed.getOwner());
                if (paid) {
                    landed.getOwner().addMoney(rent);
                    ui.updateLog(currentPlayer.getName() + " paid $" + rent + " in rent to " + landed.getOwner().getName(), Color.RED);
                    ui.updatePlayerInfo();
                }
            }


            if (canBuyProperty(landed)) {
                ui.getBuyPropertyButton().setEnabled(true);
                ui.updateLog(currentPlayer.getName() + " can buy " + landed.getName() + " for $" + landed.getCost(), Color.BLACK);
            }

            else if (landed.getOwner() == currentPlayer && landed.getHouseLevel() < 5) {
                ui.getUpgradeHouseButton().setEnabled(true);
                ui.updateLog(currentPlayer.getName() + " can upgrade " + landed.getName(), Color.BLACK);
            }


            ui.getRollDiceButton().setEnabled(false);
            ui.getEndTurnButton().setEnabled(true);
            ui.updatePlayerInfo();
            ui.repaintBoard();
        }
    });

    timer.start();
}

public void postMoveActions(Player player) {
    Property landed = properties.get(player.getPosition());
    handleSpecialSquares(landed);  // Gọi trước để xử lý JAIL, TAX, EVENT, ...

    if (landed.getOwner() == null && landed.getCost() > 0 &&
        (landed.getType() == PropertyType.NORMAL || landed.getType() == PropertyType.CIRCUS)) {
        ui.getBuyPropertyButton().setEnabled(true);
        ui.updateLog(player.getName() + " can buy " + landed.getName() + " for $" + landed.getCost(), Color.BLACK);
    } 
    else if (landed.getOwner() == player && landed.getHouseLevel() < 5) {
        ui.getUpgradeHouseButton().setEnabled(true);
        ui.updateLog(player.getName() + " can upgrade " + landed.getName(), Color.BLACK);
    } 
    else if (landed.getOwner() != null && landed.getOwner() != player) {
        int rent = calculateRent(landed);
        boolean paid = askPlayerToSellAssetsUntilEnough(player, rent, landed.getOwner());
        if (paid) {
            landed.getOwner().addMoney(rent);
            ui.updateLog(player.getName() + " paid $" + rent + " to " + landed.getOwner().getName(), Color.RED);
        }
    }

    ui.updatePlayerInfo();
    ui.repaintBoard();
}


    public void endTurn() {

        ui.getRollDiceButton().setEnabled(true);
        ui.getEndTurnButton().setEnabled(false);
        ui.getBuyPropertyButton().setEnabled(false);
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        setCurrentPlayerIndex(currentPlayerIndex);
        ui.setCurrentPlayerIndex(currentPlayerIndex);
        Player nextPlayer = players.get(currentPlayerIndex);
        if (nextPlayer.isInDungeon()) {
            nextPlayer.decrementTurnsInDungeon();

            ui.updateLog(nextPlayer.getName() + " is still in Dungeon. Turns left: " + nextPlayer.getTurnsInDungeon() + 1, Color.BLACK);
            ui.getRollDiceButton().setEnabled(false);
            ui.getEndTurnButton().setEnabled(true);

            ui.updatePlayerInfo();
            ui.repaintBoard();
        return;
        }

        ui.updateLog(nextPlayer.getName() + "'s turn", Color.BLACK);
        ui.updatePlayerInfo();
        ui.repaintBoard();

        if (nextPlayer.hasTravelChoice()) {
        nextPlayer.setTravelChoice(false);
        ui.showTravelDialog(nextPlayer);

        // Khóa endTurn và unlock rolldice sau dịch chuyển
        ui.getRollDiceButton().setEnabled(true);
        ui.getEndTurnButton().setEnabled(false);
        return;
    }
    }

    public void buyProperty() {
        Player currentPlayer = players.get(currentPlayerIndex);
        Property p = properties.get(currentPlayer.getPosition());
        if (canBuyProperty(p)) {
            currentPlayer.subtractMoney(p.getCost());
            p.setOwner(currentPlayer);
            p.upgradeHouse();
            ui.updateLog(currentPlayer.getName() + " bought " + p.getName() + " for $" + p.getCost(), Color.BLACK);
            ui.updateLog("House built at level " + p.getHouseLevel(), Color.BLACK);
            ui.getBuyPropertyButton().setEnabled(false);
            ui.repaintBoard();
            ui.updatePlayerInfo();

             //  Kiểm tra nếu người chơi vừa đủ 4 Circus
            if (checkCircusVictory(currentPlayer)) {
                ui.updateLog(currentPlayer.getName() + " owns all 4 Circus and wins!", Color.RED);
                JOptionPane.showMessageDialog(null, currentPlayer.getName() + " wins the game!");
                System.exit(0);
            }

            // Kiểm tra nếu người chơi có 3 nhóm màu monopoly
            if (checkMonopolyVictory(currentPlayer)) {
                ui.updateLog(currentPlayer.getName() + " owns 3 Monopoly color sets and wins!", Color.RED);
                JOptionPane.showMessageDialog(null, currentPlayer.getName() + " wins the game!");
                System.exit(0);
            }
        }
    }

private boolean checkMonopolyVictory(Player player) {
    long monopolyCount = properties.stream()
        .filter(p -> p.getOwner() == player)
        .map(Property::getColor)
        .distinct()
        .filter(color -> isMonopoly(color, player))
        .count();

    return monopolyCount >= 3;
}

private boolean isMonopoly(String color, Player owner) {
    return properties.stream()
        .filter(p -> p.getColor().equalsIgnoreCase(color))
        .allMatch(p -> p.getOwner() == owner);
}

private boolean checkCircusVictory(Player player) {
    long count = properties.stream()
        .filter(p -> p.getOwner() == player && p.getType() == PropertyType.CIRCUS)
        .count();

    return count >= 4;
}



    public void upgradeHouse() {
        ui.getUpgradeHouseButton().setEnabled(false);
        Player currentPlayer = players.get(currentPlayerIndex);
        Property p = properties.get(currentPlayer.getPosition());
        if (p.getOwner() == currentPlayer && p.getHouseLevel() < 5) {
            int upgradeCost = p.getCost() / 2;
            if (currentPlayer.getMoney() >= upgradeCost) {
                currentPlayer.subtractMoney(upgradeCost);
                p.upgradeHouse();
                ui.updateLog(currentPlayer.getName() + " upgraded " + p.getName() + " to level " + p.getHouseLevel(), Color.BLACK);
                ui.repaintBoard();
                ui.updatePlayerInfo();
            } else {
                ui.updateLog("Not enough money to upgrade!", Color.BLACK);
            }
        }
    }

    private boolean canBuyProperty(Property p) {
        Player currentPlayer = players.get(currentPlayerIndex);
        return p.getOwner() == null && p.getCost() > 0 && currentPlayer.getMoney() >= p.getCost()
                && (p.getType() == PropertyType.NORMAL || p.getType() == PropertyType.CIRCUS);
    }

    public void handleSpecialSquares(Property p) {
        Player currentPlayer = players.get(currentPlayerIndex);
        switch (p.getType()) {
            case JAIL:
                currentPlayer.setPosition(8);
                currentPlayer.setTurnsInDungeon(3);
                ui.updateLog(currentPlayer.getName() + " is sent to Dungeon for 3 turns!", Color.BLACK);
                break;
            case TAX:
                int totalValue = 0;
                for (Property prop : properties) {
                    if (prop.getOwner() == currentPlayer) {
                        totalValue += prop.getCost();
                    }
                }
                int tax = totalValue / 10;
                askPlayerToSellAssetsUntilEnough(currentPlayer, tax, null);
                ui.updateLog(currentPlayer.getName() + " paid $" + tax + " in taxes.", Color.BLACK);
                break;
            case TRAVEL:
                currentPlayer.setTravelChoice(true);
                ui.updateLog(currentPlayer.getName() + " landed on TRAVEL and can teleport next turn!", Color.BLACK);
                break;
            case CHANCE:
                handleChanceCard(currentPlayer);
                break;
            case EVENT:
                handleEventSquare(currentPlayer);
                break;
            default:
                break;
        }
    }

    private void handleEventSquare(Player player) {
        List<Property> owned = getOwnedProperties(player);
        if (owned.isEmpty()) {
            ui.updateLog(player.getName() + " has no properties for event.", Color.BLACK);
            return;
        }
        String[] options = owned.stream().map(Property::getName).toArray(String[]::new);
        String selected = (String) JOptionPane.showInputDialog(null, "Choose a property to double rent:", "EVENT", JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        if (selected != null) {
            owned.stream().filter(p -> p.getName().equals(selected)).findFirst().ifPresent(p -> {
                p.setDoubleRent(true);
                ui.updateLog(player.getName() + " activated x2 rent on " + p.getName(), Color.BLACK);
            });
        }
    }

    private void handleChanceCard(Player player) {
    cards.ChanceCard card = cards.ChanceCardFactory.getRandomCard();
    card.apply(player, ui , this);
    Property landed = properties.get(player.getPosition());
    handleSpecialSquares(landed);
}


    public boolean askPlayerToSellAssetsUntilEnough(Player player, int amountNeeded, Player creditor) {
        while (player.getMoney() < amountNeeded) {
            List<Property> owned = getOwnedProperties(player);
            if (owned.isEmpty()) {
                ui.updateLog(player.getName() + " has no properties left and is bankrupt!", Color.BLACK);
                if (creditor != null) {
                    creditor.addMoney(player.getMoney());
                    ui.updateLog(creditor.getName() + " receives remaining money.", Color.BLACK);
                }
                players.remove(player);
                checkGameEnd();
                return false;
            }
            String[] options = owned.stream().map(Property::getName).toArray(String[]::new);
            String selected = (String) JOptionPane.showInputDialog(null, "Choose property to sell:", "Bankruptcy", JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
            if (selected == null) {
                players.remove(player);
                checkGameEnd();
                return false;
            }
            Property toSell = owned.stream().filter(p -> p.getName().equals(selected)).findFirst().orElse(null);
            if (toSell != null) {
                player.addMoney(toSell.getCost());
                toSell.setOwner(null);
                toSell.setHouseLevel(0);
                ui.updateLog(player.getName() + " sold " + toSell.getName() + " for $" + toSell.getCost(), Color.BLACK);
                ui.repaintBoard();
            }
        }
        player.subtractMoney(amountNeeded);
        return true;
    }

    private void checkGameEnd() {
        long remaining = players.stream().filter(p -> p.getMoney() > 0).count();
        if (remaining <= 1 && players.size() > 1) {
            Player winner = players.stream().filter(p -> p.getMoney() > 0).findFirst().orElse(null);
            if (winner != null) {
                ui.updateLog("Game Over! " + winner.getName() + " wins!", Color.BLACK);
                JOptionPane.showMessageDialog(null, winner.getName() + " wins the game!");
                System.exit(0);
            }
        }
    }

    private List<Property> getOwnedProperties(Player player) {
        List<Property> list = new ArrayList<>();
        for (Property p : properties) {
            if (p.getOwner() == player) list.add(p);
        }
        return list;
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public void setCurrentPlayerIndex(int index) {
        this.currentPlayerIndex = index;
    }

    public List<Property> getProperties() {
    return properties;
}

}

