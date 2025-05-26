package ui;

import audio.AudioUtil;
import core.GameEngine;
import core.GameEventListener;
import core.PropertyFactory;
import model.Player;
import model.Property;
import model.PropertyType;

import java.awt.geom.RoundRectangle2D;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class MonopolyGame extends JFrame implements GameEventListener {
    private static final int NUM_SQUARES = 32;
    private static final int BOARD_SIZE = 700;
    private static final int SQUARE_SIZE = BOARD_SIZE / 9;
    private static final int MAX_PLAYERS = 4;
    private static final int STARTING_MONEY = 3000;
    private static final int GO_MONEY = 300;

    private ArrayList<Player> players;
    private ArrayList<Property> properties;
    private int currentPlayerIndex;
    private JPanel boardPanel;
    private JTextPane gameLog;
    private JButton rollDiceButton;
    private JButton endTurnButton;
    private JButton buyPropertyButton;
    private JButton upgradeHouseButton;
    private JLabel playerInfoLabel;
    private Random random;
    private BufferedImage[] houseImages;
    private BufferedImage hotelImage;
    private BufferedImage[] diceImages;
    private int currentDice1 = -1;
    private int currentDice2 = -1;
    private boolean showDiceAnimation = false;
    private GameEngine gameEngine;

public MonopolyGame() {
        super("Monopoly Game");
        random = new Random();
        initializeGame();
        setupUI();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(BOARD_SIZE + 300, BOARD_SIZE + 100);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
        AudioUtil.playBackgroundMusic("../assets/theme_song.wav");
    }

    private void initializeGame() {
        players = new ArrayList<>();
        String[] playerColors = {"Red", "Blue", "Green", "Yellow"};
        for (int i = 0; i < MAX_PLAYERS; i++) {
            players.add(new Player("Player " + (i + 1), playerColors[i], STARTING_MONEY));
        }

        try {
            players.get(0).setAvatar(ImageIO.read(new File("../assets/dragon.png")));
            players.get(1).setAvatar(ImageIO.read(new File("../assets/motor.png")));
            players.get(2).setAvatar(ImageIO.read(new File("../assets/fish.png")));
            players.get(3).setAvatar(ImageIO.read(new File("../assets/bird.png")));
            houseImages = new BufferedImage[4];
            for (int i = 0; i < 4; i++) {
                houseImages[i] = ImageIO.read(new File("../assets/houses/house" + (i + 1) + ".png"));
            }
            hotelImage = ImageIO.read(new File("../assets/houses/hotel.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        properties = PropertyFactory.createProperties();

        gameEngine = new GameEngine(this, players, properties);

        currentPlayerIndex = 0;
    }

    private void setupUI() {
        setLayout(new BorderLayout());
        
        // Create the game board
        boardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBoard(g);
            }
        };
        boardPanel.setPreferredSize(new Dimension(BOARD_SIZE, BOARD_SIZE));
        boardPanel.setBackground(new Color(245, 245, 255));
        
        // Create control panel
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BorderLayout());
        controlPanel.setPreferredSize(new Dimension(280, BOARD_SIZE));
        controlPanel.setMaximumSize(new Dimension(280, BOARD_SIZE));
        controlPanel.setMinimumSize(new Dimension(280, BOARD_SIZE));

        controlPanel.setBackground(new Color(230, 240, 255));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        
        // Game log
        gameLog = new JTextPane();
        gameLog.setEditable(false);
        gameLog.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gameLog.setBackground(new Color(245, 245, 255));
        
        JScrollPane scrollPane = new JScrollPane(gameLog);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Game Log"));
        scrollPane.setPreferredSize(new Dimension(260, 300)); // Chiều rộng & cao cố định
        scrollPane.setMaximumSize(new Dimension(260, 300));
        scrollPane.setMinimumSize(new Dimension(260, 300));
        controlPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);
        Color mainColor = new Color(76, 175, 80);
        Font buttonFont = new Font("Segoe UI", Font.BOLD, 16);
        
        //Update house
        upgradeHouseButton = new JButton("Upgrade House");
        upgradeHouseButton.setBackground(new Color(255, 87, 34));
        upgradeHouseButton.setForeground(Color.WHITE);
        upgradeHouseButton.setFont(buttonFont);
        upgradeHouseButton.setFocusPainted(false);
        upgradeHouseButton.setBorder(new RoundedBorder(12));
        upgradeHouseButton.setEnabled(false);

        //Setup DiceButton
        rollDiceButton = new JButton("Roll Dice");
        rollDiceButton.setBackground(mainColor);
        rollDiceButton.setForeground(Color.WHITE);
        rollDiceButton.setFont(buttonFont);
        rollDiceButton.setFocusPainted(false);
        rollDiceButton.setBorder(new RoundedBorder(12));
        
        //Setup EndButton
        endTurnButton = new JButton("End Turn");
        endTurnButton.setBackground(new Color(33, 150, 243));
        endTurnButton.setForeground(Color.WHITE);
        endTurnButton.setFont(buttonFont);
        endTurnButton.setFocusPainted(false);
        endTurnButton.setBorder(new RoundedBorder(12));
        
        //Setup BuyPropertyButton
        buyPropertyButton = new JButton("Buy Property");
        buyPropertyButton.setBackground(new Color(255, 193, 7));
        buyPropertyButton.setForeground(Color.BLACK);
        buyPropertyButton.setFont(buttonFont);
        buyPropertyButton.setFocusPainted(false);
        buyPropertyButton.setBorder(new RoundedBorder(12));
        
        //Info Player
        playerInfoLabel = new JLabel();
        playerInfoLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        playerInfoLabel.setForeground(new Color(44, 62, 80));
        playerInfoLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        Dimension fullWidth = new Dimension(Short.MAX_VALUE, 50); // Chiều ngang tối đa, cao 50px
        rollDiceButton.setMaximumSize(fullWidth);
        buyPropertyButton.setMaximumSize(fullWidth);
        upgradeHouseButton.setMaximumSize(fullWidth);
        endTurnButton.setMaximumSize(fullWidth);
        
        buttonPanel.add(playerInfoLabel);
        buttonPanel.add(rollDiceButton);
        buttonPanel.add(buyPropertyButton);
        buttonPanel.add(upgradeHouseButton);
        buttonPanel.add(endTurnButton);
        
        rollDiceButton.addActionListener(e -> {
            rollDiceButton.setEnabled(false);
            int dice1 = random.nextInt(6) + 1;
            int dice2 = random.nextInt(6) + 1;

            setDiceResult(dice1, dice2); // hiện xúc xắc
            updateLog("Rolling the dice...", Color.BLUE);

            animateDiceRoll(dice1, dice2, () -> {
            hideDice(); // tắt xúc xắc sau animation
            gameEngine.handleDiceResult(dice1, dice2); // Gọi xử lý với số đã roll
            });
        });

        endTurnButton.addActionListener(e -> {
            AudioUtil.playSound("../assets/click_endturn.wav");
            gameEngine.endTurn();
        });

        buyPropertyButton.addActionListener(e -> {
            AudioUtil.playSound("../assets/click_buy.wav");
            gameEngine.buyProperty();
        });
        upgradeHouseButton.addActionListener(e -> {
            gameEngine.upgradeHouse();
        });

        // Initial button states
        endTurnButton.setEnabled(false);
        buyPropertyButton.setEnabled(false);
        
        controlPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add panels to frame
        add(boardPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.EAST);
        
        // Initialize game log
        if (players != null && !players.isEmpty()) {
            updateLog("Game started! " + players.get(currentPlayerIndex).getName() + "'s turn.", new Color(33, 150, 243));
        } else {
            updateLog("Game started!", new Color(33, 150, 243));
        }
        updatePlayerInfo();
    }

    

    private void drawBoard(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        GradientPaint gp = new GradientPaint(0, 0, new Color(245, 245, 255), 0, getHeight(), new Color(200, 220, 255)); //tạo nền game
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        g2d.setColor(new Color(60, 60, 60));
        g2d.setStroke(new BasicStroke(4f));
        //g2d.draw(new RoundRectangle2D.Double(10, 10, BOARD_SIZE, BOARD_SIZE, 32, 32));

        g2d.setColor(new Color(44, 62, 80));
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 44));
        FontMetrics fm = g2d.getFontMetrics();
        String title = "MONOPOLY";
        int textWidth = fm.stringWidth(title);
        g2d.drawString(title, (BOARD_SIZE - textWidth) / 2, BOARD_SIZE / 2);
        drawProperties(g2d);
        
        // Draw players
        drawPlayers(g2d);

        //Draw dice
        if (showDiceAnimation && currentDice1 > 0 && currentDice2 > 0) {
        try {
            BufferedImage diceImg1 = ImageIO.read(new File("../assets/dice" + currentDice1 + ".png"));
            BufferedImage diceImg2 = ImageIO.read(new File("../assets/dice" + currentDice2 + ".png"));
            int size = 50;
            g2d.drawImage(diceImg1, BOARD_SIZE / 2 - size - 10, BOARD_SIZE / 2 + 80, size, size, null);
            g2d.drawImage(diceImg2, BOARD_SIZE / 2 + 10, BOARD_SIZE / 2 + 80, size, size, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
}
    }
    
private void drawProperties(Graphics2D g2d) {
    // Bottom row (0-8) - từ phải sang trái
    for (int i = 0; i <= 8; i++) {
        int x = BOARD_SIZE - SQUARE_SIZE - 4 - (i * SQUARE_SIZE);
        int y = BOARD_SIZE - SQUARE_SIZE;
        drawProperty(g2d, properties.get(i), x, y, 0);
    }
    
    // Left column (9-16) - từ dưới lên trên
    for (int i = 9; i <= 16; i++) {
        int x = 2;
        int y = BOARD_SIZE - SQUARE_SIZE - ((i - 8) * SQUARE_SIZE);
        drawProperty(g2d, properties.get(i), x, y, 1);
    }
    
    // Top row (17-24) - từ trái sang phải
    for (int i = 17; i <= 24; i++) {
        int x = (i - 16) * SQUARE_SIZE +2;
        int y = 6;
        drawProperty(g2d, properties.get(i), x, y, 2);
    }
    
    // Right column (25-31) - từ trên xuống dưới
    for (int i = 25; i <= 31; i++) {
        int x = BOARD_SIZE - SQUARE_SIZE - 4;
        int y = (i - 24) * SQUARE_SIZE + 4;
        drawProperty(g2d, properties.get(i), x, y, 3);
    }
}
    
    private void drawProperty(Graphics2D g2d, Property property, int x, int y, int orientation) {
        g2d.setColor(Color.BLACK);
        g2d.draw(new RoundRectangle2D.Double(x, y, SQUARE_SIZE, SQUARE_SIZE, 16, 16));
        if (!property.getColor().isEmpty() && !property.getColor().equals("white") && !property.getColor().equals("black")) {
            g2d.setColor(getColorForProperty(property.getColor()));
            g2d.fill(new RoundRectangle2D.Double(x, y, SQUARE_SIZE, SQUARE_SIZE, 16, 16));
        }
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
        if (property.getType() == PropertyType.GO) {
            g2d.setFont(new Font("Impact", Font.BOLD, 16));
            g2d.setColor(new Color(44, 62, 80));
            g2d.drawString("START", x + SQUARE_SIZE /2 -20 , y + SQUARE_SIZE/2 + 5);
        } else if (property.getType() == PropertyType.JAIL) {
            g2d.setColor(new Color(33, 150, 243));
            g2d.drawString("DUNGEON", x + 8, y + SQUARE_SIZE / 2 + 5);
        } else if (property.getType() == PropertyType.TRAVEL) {
            g2d.setFont(new Font("Felix Titling", Font.BOLD, 14));
            g2d.setColor(new Color(33, 150, 243));
            g2d.drawString("TRAVEL", x + 10, y + SQUARE_SIZE / 2 + 5);
        } else if (property.getType() == PropertyType.CHANCE) {
            g2d.setFont(new Font("Stencil", Font.PLAIN, 14));
            g2d.setColor(new Color(33, 150, 243));
            //g2d.drawString("?", x + SQUARE_SIZE / 2 - 3, y + SQUARE_SIZE / 2 + 5);
            g2d.drawString("CHANCE", x + 10, y + SQUARE_SIZE / 2 + 5);
        } else if (property.getType() == PropertyType.TAX) {
            g2d.setFont(new Font("Arial Black", Font.BOLD, 16));
            g2d.setColor(new Color(33, 150, 243));
            g2d.drawString("TAX", x + 20, y + SQUARE_SIZE / 2 + 5);
        } 
        else if (property.getType() == PropertyType.EVENT) {
            g2d.setFont(new Font("Jokerman", Font.BOLD, 16)); // Font viết tay
            g2d.setColor(new Color(33, 150, 243));
            g2d.drawString("EVENT", x + 10, y + SQUARE_SIZE / 2 + 5);
        }else {
            g2d.setColor(new Color(44, 62, 80));
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 8));
            String name = property.getName();
            if (name.length() > 12) {
                name = name.substring(0, 10) + "...";
            }
            g2d.drawString(name, x + 2, y + SQUARE_SIZE / 4);
            if (property.getCost() > 0) {
                g2d.drawString("$" + property.getCost(), x + 2, y + SQUARE_SIZE - 5);
            }
            // if (property.getOwner() != null) {
            //     g2d.setColor(getPlayerColor(property.getOwner().getColor()));
            //     g2d.fillOval(x + SQUARE_SIZE / 2 - 5, y + SQUARE_SIZE / 2 - 5, 10, 10);
            // }
        }
        g2d.setColor(Color.GRAY);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 8));
        g2d.drawString(String.valueOf(property.getPosition()), x + SQUARE_SIZE - 12, y + 8);

        // Hiển thị số nhà hoặc khách sạn
        int level = property.getHouseLevel();
        if (level > 0) {
            BufferedImage houseImg = null;
            if (level <= 4) {
                houseImg = houseImages[level - 1];
                } else if (level == 5) {
                    houseImg = hotelImage;
                }
            int imgSize = 20;
            if (houseImg != null) {
                g2d.drawImage(houseImg, x + SQUARE_SIZE - imgSize - 4, y + 4, imgSize, imgSize, null);
            }
            int centerX = x + SQUARE_SIZE - imgSize - 4 + imgSize / 2;
            int centerY = y + 4 + imgSize / 2;
            int radius = imgSize + 6; // kích thước viền ngoài
    
            if (property.getOwner() != null) {
                g2d.setColor(getPlayerColor(property.getOwner().getColor()));
            } else {
                g2d.setColor(new Color(200, 200, 200)); // fallback nếu chưa có owner
            }
            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval(centerX - radius / 2, centerY - radius / 2, radius, radius);
        }
    }
    
    private void drawPlayers(Graphics2D g2d) {
    int size = SQUARE_SIZE / 2;

    // Mỗi player có thể ở vị trí bất kỳ, nhưng vẽ theo vị trí trong cùng 1 ô
    for (int i = 0; i < players.size(); i++) {
        Player player = players.get(i);
        int position = player.getPosition();
        Property property = properties.get(position);

        // Tính toạ độ của ô hiện tại
        int squareX = 0, squareY = 0;

        if (position <= 8) {
            squareX = BOARD_SIZE - (position + 1) * SQUARE_SIZE;
            squareY = BOARD_SIZE - SQUARE_SIZE;
        } else if (position <= 16) {
            squareX = 0;
            squareY = BOARD_SIZE - (position - 8 + 1) * SQUARE_SIZE;
        } else if (position <= 24) {
            squareX = (position - 16) * SQUARE_SIZE;
            squareY = 0;
        } else {
            squareX = BOARD_SIZE - SQUARE_SIZE;
            squareY = (position - 24) * SQUARE_SIZE;
        }

        // Offset từng người chơi trong 1 ô
        int offsetX = (i % 2) * size;
        int offsetY = (i / 2) * size;

        int x = squareX + offsetX + 4;
        int y = squareY + offsetY + 4;

        // Highlight người chơi hiện tại
        if (i == currentPlayerIndex) {
            g2d.setColor(new Color(255, 215, 0));
            g2d.fillOval(x - 2, y - 2, size + 4, size + 4);
        }

        // Vẽ avatar
        Image avatar = player.getAvatar();
        if (avatar != null) {
            g2d.drawImage(avatar, x, y, size - 4, size - 4, null);
        } else {
            g2d.setColor(getPlayerColor(player.getColor()));
            g2d.fillOval(x, y, size - 4, size - 4);
        }

        // Số thứ tự
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 9));
        g2d.drawString(String.valueOf(i + 1), x + 4, y + 10);

    }
}

    
    private Color getColorForProperty(String colorName) {
        switch (colorName.toLowerCase()) {
            case "brown": return new Color(205, 133, 63);
            case "light-blue": return new Color(170, 224, 250);
            case "pink": return new Color(255, 105, 180);
            case "orange": return new Color(255, 165, 0);
            case "red": return new Color(255, 0, 0);
            case "yellow": return new Color(255, 255, 0);
            case "green": return new Color(102, 204, 102);
            case "dark-blue": return new Color(100, 149, 237);
            case "black": return new Color(0, 0, 0);
            case "white": return new Color(255, 255, 255);
            default: return Color.GRAY;
        }
    }
    
    private Color getPlayerColor(String colorName) {
        switch (colorName.toLowerCase()) {
            case "red": return Color.RED;
            case "blue": return Color.BLUE;
            case "green": return Color.GREEN;
            case "yellow": return Color.YELLOW;
            default: return Color.BLACK;
        }
    }

    public void animateDiceRoll(int finalDice1, int finalDice2, Runnable onFinish) {
    Timer diceTimer = new Timer(100, null); // quay 10 lần mỗi 100ms
    final int[] ticks = {0};
    AudioUtil.playSound("../assets/dice_roll.wav");
    new Timer(300, e -> {
        ((Timer) e.getSource()).stop();
        AudioUtil.playSound("../assets/dice_roll.wav");
    }).start();


    diceTimer.addActionListener(e -> {
        // Quay xúc xắc ngẫu nhiên
        currentDice1 = random.nextInt(6) + 1;
        currentDice2 = random.nextInt(6) + 1;
        showDiceAnimation = true;
        boardPanel.repaint();

        ticks[0]++;
        if (ticks[0] >= 10) {
            ((Timer) e.getSource()).stop();

            // Gán kết quả thật để hiển thị
            currentDice1 = finalDice1;
            currentDice2 = finalDice2;
            boardPanel.repaint();

            // Delay thêm 1.5s để người chơi nhìn rõ kết quả
            new Timer(1500, evt -> {
                ((Timer) evt.getSource()).stop();
                showDiceAnimation = false;
                boardPanel.repaint();
                onFinish.run(); // Tiếp tục logic game
            }).start();
        }
    });

    diceTimer.start();
}

    @Override
    public void updateLog(String message, Color color) {
        StyledDocument doc = gameLog.getStyledDocument();
        Style style = gameLog.addStyle("Style", null);
        StyleConstants.setForeground(style, color);
        try {
            doc.insertString(doc.getLength(), message + "\n", style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        gameLog.setCaretPosition(doc.getLength());
    }

    @Override
    public void updatePlayerInfo() {
        if (players == null || players.isEmpty()) return;
        Player currentPlayer = players.get(currentPlayerIndex);
        StringBuilder info = new StringBuilder();
        info.append("Current Player: ").append(currentPlayer.getName())
            .append(" ($").append(currentPlayer.getMoney()).append(")");
        playerInfoLabel.setText("<html>" + info.toString().replace("\n", "<br>") + "</html>");
    }

    @Override
    public void repaintBoard() {
        boardPanel.repaint();
    }

    @Override
public void showTravelDialog(Player player) {
    List<Property> availableDestinations = properties.stream()
        .filter(p -> {
            // Loại bỏ các ô không hợp lệ
            boolean isRestrictedType = p.getType() == PropertyType.EVENT ||
                                       p.getType() == PropertyType.JAIL ||
                                       p.getType() == PropertyType.TRAVEL ||
                                       p.getType() == PropertyType.TAX;
            boolean isOwnedByOthers = p.getOwner() != null && p.getOwner() != player;

            return !isRestrictedType && !isOwnedByOthers;
        })
        .collect(Collectors.toList());


    if (availableDestinations.isEmpty()) {
        updateLog("No valid teleport destinations available.", Color.RED);
        return;
    }

    // Tạo danh sách hiển thị "pos - name"
    String[] destinationOptions = availableDestinations.stream()
        .map(p -> p.getPosition() + " - " + p.getName())
        .toArray(String[]::new);

    String choice = (String) JOptionPane.showInputDialog(
        this,
        player.getName() + ", choose your teleport destination:",
        "Travel",
        JOptionPane.PLAIN_MESSAGE,
        null,
        destinationOptions,
        destinationOptions[0]
    );

    if (choice != null) {
        try {
            int pos = Integer.parseInt(choice.split(" - ")[0]);
            player.setPosition(pos);

            updateLog(player.getName() + " teleported to position " + pos, Color.MAGENTA);
            repaintBoard();

            Property landed = properties.get(pos);
            gameEngine.handleSpecialSquares(landed);

            // Cho phép mua nếu là đất hợp lệ
            if (landed.getOwner() == null && landed.getCost() > 0) {
                buyPropertyButton.setEnabled(true);
                updateLog(player.getName() + " can buy " + landed.getName(), Color.BLACK);
            }

            endTurnButton.setEnabled(true);

        } catch (NumberFormatException e) {
            updateLog("Invalid teleport destination.", Color.RED);
        }
    } else {
        updateLog(player.getName() + " skipped teleportation.", Color.GRAY);
    }
}


    public JButton getRollDiceButton() { return rollDiceButton; }
    public JButton getEndTurnButton() { return endTurnButton; }
    public JButton getBuyPropertyButton() { return buyPropertyButton; }
    public JButton getUpgradeHouseButton() { return upgradeHouseButton; }
    public List<Player> getPlayers() { return players; }
    public List<Property> getProperties() { return properties; }
    public int getCurrentPlayerIndex() { return currentPlayerIndex; }
    public void setCurrentPlayerIndex(int index) { this.currentPlayerIndex = index; }
    public BufferedImage[] getHouseImages() { return houseImages; }
    public BufferedImage getHotelImage() { return hotelImage; }
    public Random getRandom() { return random; }
    public void setDiceResult(int d1, int d2) {
        this.currentDice1 = d1;
        this.currentDice2 = d2;
        this.showDiceAnimation = true;
        repaintBoard();
    }
    public void hideDice() {
        this.showDiceAnimation = false;
        repaintBoard();
    }
}

