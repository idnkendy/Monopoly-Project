package cards;

import java.util.Random;

public class ChanceCardFactory {
    private static final Random random = new Random();

    public static ChanceCard getRandomCard() {
        switch (random.nextInt(7)) {
            case 0: return new ChanceMoneyReward();
            case 1: return new ChancePayFine();
            case 2: return new ChanceAdvanceToGo();
            case 3: return new ChanceGoToTravel();
            case 4: return new ChanceGoBack();
            case 5: return new ChanceGoToJail();
            case 6: return new ChanceGoForward();
            default: return new ChanceMoneyReward();
        }
    }
}