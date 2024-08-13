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

public class oldMinerStorePanel extends JPanel {
    private JButton[] buttons;
    private mining game;
    private String[] itemNames = {
        "Handy Pickaxe",
        "Pickaxe Repair Kit",
        "Mining Helmet"
    };
    private String[] itemDescriptions = {
        "Increases money earned from gemstones by 10.",
        "Restores pickaxe durability by 200.",
        "Increases the number of gemstones per round to 2-6."
    };

    private int[] itemPrices = {
        100, // Handy Pickaxe
        200, // Pickaxe Repair Kit
        150  // Mining Helmet
    };

    private JLabel oldMinerLabel;
    private JTextArea descriptionArea;
    private Timer typingTimer;
    private Timer randomMessageTimer;
    private Timer priceUpdateTimer;
    private String currentText = "";
    private String targetText = "";
    private final String[] randomMessages = {
        "Hey little tough guy, buying anything?",
        "Here is a little tip for ya, my handy pickaxe and mining helmet is great for early stage of the game!",
        "My pickaxe repair kit price rises overtime, mind that yo!"
    };
    private Random random = new Random();

    public oldMinerStorePanel(mining game) {
        this.game = game;
        setLayout(new BorderLayout());
        setBackground(Color.DARK_GRAY); // Set dark background color
        setPreferredSize(new Dimension(1000, 700)); // Increase the preferred size for better visibility

        // Load the Old Miner image
        BufferedImage oldMinerImage = loadImage("src/images/npc1.jpg");
        if (oldMinerImage == null) {
            oldMinerLabel = new JLabel("Old Miner Image Not Found");
        } else {
            Image scaledImage = oldMinerImage.getScaledInstance(150, 150, Image.SCALE_SMOOTH); // Increase image size
            ImageIcon oldMinerIcon = new ImageIcon(scaledImage);
            oldMinerLabel = new JLabel(oldMinerIcon);
        }

        // Panel for Old Miner Image and Title
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.setBackground(Color.DARK_GRAY); // Set dark background color
        JLabel titleLabel = new JLabel("Old Miner Store", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE); // Set text color to white
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Increase title font size
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(oldMinerLabel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // Description area setup
        descriptionArea = new JTextArea(3, 30); // Adjust height and width
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setEditable(false);
        descriptionArea.setOpaque(true);
        descriptionArea.setForeground(Color.BLACK); // Set text color to black
        descriptionArea.setBackground(Color.LIGHT_GRAY); // Set background color to light gray
        descriptionArea.setBorder(BorderFactory.createLineBorder(Color.WHITE)); // Set border color to white
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 18)); // Set font size to 18
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBackground(Color.DARK_GRAY); // Set background color to dark gray
        scrollPane.setPreferredSize(new Dimension(800, 150)); // Adjust size for visibility
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

        // Timer for updating Pickaxe Repair Kit price
        priceUpdateTimer = new Timer(1000, new ActionListener() { // Update every second
            @Override
            public void actionPerformed(ActionEvent e) {
                updateRepairKitPrice();
            }
        });
        priceUpdateTimer.start();
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

    private void updateRepairKitPrice() {
        double repairKitPrice = game.getRepairKitPrice();
        buttons[1].setText("Pickaxe Repair Kit - " + String.valueOf((int) Math.round(repairKitPrice)) + " Money");
    }

    private class ButtonClickListener implements ActionListener {
        private int index;

        public ButtonClickListener(int index) {
            this.index = index;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            SwingUtilities.invokeLater(() -> {
                if (index == 0) { // Handy Pickaxe
                    if (!game.hasHandyPickaxe() && game.getMoney() >= itemPrices[0]) {
                        game.setHandyPickaxe(true);
                        game.decreaseMoney(itemPrices[0]);
                        showMessage("Purchase Completed!", "You have bought the Handy Pickaxe.");
                        updateButtonText(0); // Update button text with new price if needed
                    } else if (game.hasHandyPickaxe()) {
                        showMessage("Purchase Not Allowed", "You already own the Handy Pickaxe.");
                    } else {
                        showMessage("Not Enough Money", "You do not have enough money to buy the Handy Pickaxe.");
                    }
                } else if (index == 1) { // Pickaxe Repair Kit
                    double repairKitPrice = game.getRepairKitPrice();
                    if (game.getMoney() >= repairKitPrice) {
                        game.setItemBought(1);
                        game.decreaseMoney((int) repairKitPrice);
                        showMessage("Purchase Completed!", "You have bought the Pickaxe Repair Kit.");
                        updateRepairKitPrice();
                    } else {
                        showMessage("Not Enough Money", "You do not have enough money to buy the Pickaxe Repair Kit.");
                    }
                } else if (index == 2) { // Mining Helmet
                    if (!game.hasMiningHelmet() && game.getMoney() >= itemPrices[2]) {
                        game.setMiningHelmet(true);
                        game.decreaseMoney(itemPrices[2]);
                        showMessage("Purchase Completed!", "You have bought the Mining Helmet.");
                        updateButtonText(2); // Update button text with new price if needed
                    } else if (game.hasMiningHelmet()) {
                        showMessage("Purchase Not Allowed", "You already own the Mining Helmet.");
                    } else {
                        showMessage("Not Enough Money", "You do not have enough money to buy the Mining Helmet.");
                    }
                }
            });
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
            descriptionArea.setText(currentText);
        }
    }

    private class ButtonHoverListener extends MouseAdapter {
        @Override
        public void mouseEntered(MouseEvent e) {
            JButton button = (JButton) e.getSource();
            button.setBackground(Color.DARK_GRAY); // Change background color on hover
        }

        @Override
        public void mouseExited(MouseEvent e) {
            JButton button = (JButton) e.getSource();
            button.setBackground(Color.GRAY); // Restore original background color
        }
    }

    private void showMessage(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateButtonText(int index) {
        buttons[index].setText(itemNames[index] + " - " + itemPrices[index] + " Money");
    }
}