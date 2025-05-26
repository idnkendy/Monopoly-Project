import javax.swing.SwingUtilities;
import ui.MonopolyGame;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MonopolyGame());
    }
}