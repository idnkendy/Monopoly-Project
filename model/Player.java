package model;

import java.awt.Image;

public class Player {
    private String name;
    private String color;
    private int money;
    private int position;
    private boolean inDungeon = false;
    private int turnsInDungeon = 0;
    private boolean travelChoice = false;
    private Image avatar;

    public Player(String name, String color, int startingMoney) {
        this.name = name;
        this.color = color;
        this.money = startingMoney;
        this.position = 0;
    }

    public String getName() { return name; }
    public String getColor() { return color; }
    public int getMoney() { return money; }
    public int getPosition() { return position; }
    public boolean isInDungeon() { return inDungeon; }
    public int getTurnsInDungeon() { return turnsInDungeon; }
    public boolean hasTravelChoice() { return travelChoice; }
    public Image getAvatar() { return avatar; }

    public void setPosition(int position) { this.position = position; }
    public void addMoney(int amount) { money += amount; }
    public void subtractMoney(int amount) { money -= amount; }
    public void setAvatar(Image avatar) { this.avatar = avatar; }
    public void setTravelChoice(boolean travelChoice) { this.travelChoice = travelChoice; }
    public void setTurnsInDungeon(int turns) {
        this.inDungeon = true;
        this.turnsInDungeon = turns;
    }
    public void decrementTurnsInDungeon() {
        turnsInDungeon--;
        if (turnsInDungeon <= 0) inDungeon = false;
    }
}