package model;

public class Property {
    private final int position;
    private final String name;
    private final int cost;
    private final String color;
    private final PropertyType type;
    private Player owner;
    private int houseLevel = 0;
    private boolean doubleRent = false;

    public Property(int position, String name, int cost, String color, PropertyType type) {
        this.position = position;
        this.name = name;
        this.cost = cost;
        this.color = color;
        this.type = type;
    }

    public int getPosition() { return position; }
    public String getName() { return name; }
    public int getCost() { return cost; }
    public String getColor() { return color; }
    public PropertyType getType() { return type; }

    public Player getOwner() { return owner; }
    public void setOwner(Player owner) { this.owner = owner; }

    public int getHouseLevel() { return houseLevel; }
    public void setHouseLevel(int level) { this.houseLevel = level; }
    public void upgradeHouse() { if (houseLevel < 5) houseLevel++; }

    public boolean isDoubleRent() { return doubleRent; }
    public void setDoubleRent(boolean doubleRent) { this.doubleRent = doubleRent; }
}