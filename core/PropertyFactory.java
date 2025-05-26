package core;

import model.*;
import java.util.*;

public class PropertyFactory {
    public static ArrayList<Property> createProperties() {
        String[] names = {
            "Start", "GRANADA", "SEVILLE", "MADRID", "CIRCUS", "HONGKONG", "BEIJING", "SHANGHAI", "DUNGEON",
            "VENICE", "MILAN", "ROME", "CHANCE", "HAMBURG", "CIRCUS", "BERLIN", "EVENT",
            "LONDON", "CIRCUS", "SYDNEY", "CHANCE",
            "CHICAGO", "LAS VEGAS", "NEW YORK", "TRAVEL",
            "CIRCUS", "LYON", "PARIS", "CHANCE", "OSAKA", "TAX", "TOKYO"
        };
        int[] costs = {
            0, 50, 57, 63, 50, 66, 76, 83, 0,
            140, 150, 160, 0, 170, 50, 180, 0,
            233, 50, 240, 0, 253, 270, 283, 0,
            50, 316, 333, 0, 433, 0, 500
        };
        String[] colors = {
            "", "brown", "brown", "brown", "", "light-blue", "light-blue", "light-blue", "",
            "pink", "pink", "pink", "", "orange", "", "orange", "",
            "red", "", "red", "", "yellow", "yellow", "yellow", "",
            "", "green", "green", "", "dark-blue", "", "dark-blue"
        };

        ArrayList<Property> list = new ArrayList<>();
        for (int i = 0; i < names.length; i++) {
            PropertyType type;
            if (i == 0) type = PropertyType.GO;
            else if (i == 8) type = PropertyType.JAIL;
            else if (i == 24) type = PropertyType.TRAVEL;
            else if (i == 30) type = PropertyType.TAX;
            else if (i == 12 || i == 20 || i == 28) type = PropertyType.CHANCE;
            else if (i == 16) type = PropertyType.EVENT;
            else if (i == 4 || i == 14 || i == 18 || i == 25) type = PropertyType.CIRCUS;
            else type = PropertyType.NORMAL;
            list.add(new Property(i, names[i], costs[i], colors[i], type));
        }
        return list;
    }
}