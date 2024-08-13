import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.Random;

public class WitchStorePanel extends JPanel {
    private JButton[] buttons;
    private mining game;
    private String[] itemNames = {
        "Fortune Potion",
        "Shield Potion",
        "Glorious Potion"
    };
    private String[] itemDescriptions = {
        "Increases money earned by 5% for the next 3 minutes.",
        "Shields you from colliding with bombs for 3 minutes.",
        "Grants more money the less time you use to complete a round. Lasts 1 minute."
    };

    private int[] itemPrices = {
        200, // Fortune Potion
        100, // Shield Potion
        500  // Glorious Potion
    };

    private JLabel witchLabel;
    private JTextArea descriptionArea;
    private Timer typingTimer;
    private Timer randomMessageTimer;
    private String currentText = "";
    private String targetText = "";
    private final String[] randomMessages = {
        "Looking for a bit of magic?",
        "These potions can make a real difference!",
        "Choose wisely, each potion has its own power."
    };
    private Random random = new Random();

    public WitchStorePanel(mining game) {
        this.game = game;
        setLayout(new BorderLayout());
        setBackground(Color.DARK_GRAY); // Set dark background color
        setPreferredSize(new Dimension(1000, 700)); // Increase the preferred size for better visibility

        // Load the Witch image
        BufferedImage witchImage = loadImage("src/images/npc1.jpg");
        if (witchImage == null) {
            witchLabel = new JLabel("Witch Image Not Found");
        } else {
            Image scaledImage = witchImage.getScaledInstance(150, 150, Image.SCALE_SMOOTH); // Increase image size
            ImageIcon witchIcon = new ImageIcon(scaledImage);
            witchLabel = new JLabel(witchIcon);
        }

        // Panel for Witch Image and Title
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.setBackground(Color.DARK_GRAY); // Set dark background color
        JLabel titleLabel = new JLabel("Witch Store", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE); // Set text color to white
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Increase title font size
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(witchLabel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // Description area setup
        descriptionArea = new JTextArea(3, 30); // Adjust height and width to match oldMinerStorePanel
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setEditable(false);
        descriptionArea.setOpaque(true);
        descriptionArea.setForeground(Color.BLACK); // Set text color to black
        descriptionArea.setBackground(Color.LIGHT_GRAY); // Set background color to light gray
        descriptionArea.setBorder(BorderFactory.createLineBorder(Color.WHITE)); // Set border color to white
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 18)); // Set font size to match oldMinerStorePanel
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBackground(Color.DARK_GRAY); // Set background color to dark gray
        scrollPane.setPreferredSize(new Dimension(800, 150)); // Adjust size to match oldMinerStorePanel
        add(scrollPane, BorderLayout.CENTER);

        // Button panel setup
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(itemNames.length, 1, 10, 10)); // Added gaps between buttons
        buttonPanel.setBackground(Color.DARK_GRAY); // Set dark background color
        buttons = new JButton[itemNames.length];

        Font buttonFont = new Font("Arial", Font.BOLD, 20); // Increased font size for buttons
        Dimension buttonSize = new Dimension(800, 60); // Increased button size

        for (int i = 0; i < itemNames.length; i++) {
            buttons[i] = new JButton(itemNames[i] + " - " + itemPrices[i] + " Money"); // Initialize with price
            buttons[i].setToolTipText(itemDescriptions[i]);
            buttons[i].setFont(buttonFont); // Set button font
            buttons[i].setForeground(Color.BLACK); // Set text color to black
            buttons[i].setBackground(Color.GRAY); // Set button background color to gray
            buttons[i].setOpaque(true);
            buttons[i].setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED)); // Set raised bevel border for 3D look
            buttons[i].setPreferredSize(buttonSize); // Set preferred button size
            buttons[i].addActionListener(new ButtonClickListener(i));
            buttons[i].addMouseListener(new ItemHoverListener(i));
            buttons[i].addMouseListener(new ButtonHoverListener()); // Add the hover listener here
            buttonPanel.add(buttons[i]);
        }

        add(buttonPanel, BorderLayout.SOUTH);

        // Timer for random messages
        randomMessageTimer = new Timer(3000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentText.isEmpty()) {
                    displayRandomMessage();
                }
            }
        });
        randomMessageTimer.start();
    }

    private BufferedImage loadImage(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void startTypingEffect(String text) {
        if (typingTimer != null && typingTimer.isRunning()) {
            typingTimer.stop();
        }
        currentText = "";
        targetText = text;
        typingTimer = new Timer(20, new ActionListener() {
            private int index = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (index < targetText.length()) {
                    currentText += targetText.charAt(index);
                    descriptionArea.setText(currentText);
                    index++;
                } else {
                    typingTimer.stop();
                }
            }
        });
        typingTimer.start();
    }

    private void displayRandomMessage() {
        String message = randomMessages[random.nextInt(randomMessages.length)];
        descriptionArea.setText(message);
    }

    private class ButtonClickListener implements ActionListener {
        private int index;

        public ButtonClickListener(int index) {
            this.index = index;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            SwingUtilities.invokeLater(() -> {
                if (index == 0) { // Fortune Potion
                    if (game.getMoney() >= itemPrices[0]) {
                        game.setFortunePotion(true);
                        game.decreaseMoney(itemPrices[0]);
                        showMessage("Purchase Completed!", "You have bought the Fortune Potion.");
                    } else {
                        showMessage("Insufficient Money", "You don't have enough money for the Fortune Potion.");
                    }
                } else if (index == 1) { // Shield Potion
                    if (game.getMoney() >= itemPrices[1]) {
                        game.setShieldPotion(true);
                        game.decreaseMoney(itemPrices[1]);
                        showMessage("Purchase Completed!", "You have bought the Shield Potion.");
                    } else {
                        showMessage("Insufficient Money", "You don't have enough money for the Shield Potion.");
                    }
                } else if (index == 2) { // Glorious Potion
                    if (game.getMoney() >= itemPrices[2]) {
                        game.setGloriousPotion(true);
                        game.decreaseMoney(itemPrices[2]);
                        showMessage("Purchase Completed!", "You have bought the Glorious Potion.");
                    } else {
                        showMessage("Insufficient Money", "You don't have enough money for the Glorious Potion.");
                    }
                }
            });
        }

        private void showMessage(String title, String message) {
            JOptionPane.showMessageDialog(WitchStorePanel.this, message, title, JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private class ItemHoverListener extends MouseAdapter {
        private int index;

        public ItemHoverListener(int index) {
            this.index = index;
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            startTypingEffect(itemDescriptions[index]);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            currentText = "";
            descriptionArea.setText("");
        }
    }

    private class ButtonHoverListener extends MouseAdapter {
        @Override
        public void mouseEntered(MouseEvent e) {
            JButton button = (JButton) e.getSource();
            button.setBackground(Color.LIGHT_GRAY); // Change button background color on hover
        }

        @Override
        public void mouseExited(MouseEvent e) {
            JButton button = (JButton) e.getSource();
            button.setBackground(Color.GRAY); // Revert button background color
        }
    }
}