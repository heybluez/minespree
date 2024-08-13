import javax.swing.*;
import java.awt.*;

public class menu extends JPanel {
    private JPanel cards;
    private CardLayout cardLayout;

    private mining miningPanel; // Reference to mining panel
    private oldMinerStorePanel oldMinerStorePanel; // Reference to Old Miner Store panel
    private WitchStorePanel witchStorePanel; // Reference to Witch Store panel

    public menu() {
        cardLayout = new CardLayout();
        setLayout(cardLayout);

        MenuPanel menuPanel = new MenuPanel(
            e -> showGamePanel(),
            e -> showGuideDialog()
        );

        cards = new JPanel(cardLayout);
        add(cards);

        miningPanel = new mining(800,800, Color.BLACK); // Initialize mining panel
        miningPanel.addKeyListener(miningPanel); // Add KeyListener to miningPanel

        oldMinerStorePanel = new oldMinerStorePanel(miningPanel); // Initialize Old Miner Store panel
        witchStorePanel = new WitchStorePanel(miningPanel); // Initialize Witch Store panel

        cards.add(menuPanel, "menu");
        cards.add(miningPanel, "game"); // Add mining panel with identifier "game"
        cards.add(oldMinerStorePanel, "oldMinerStore"); // Add Old Miner Store panel with identifier "oldMinerStore"
        cards.add(witchStorePanel, "witchStore"); // Add Witch Store panel with identifier "witchStore"
    }

    private void showGamePanel() {
        cardLayout.show(cards, "game");
        miningPanel.requestFocusInWindow(); // Ensure mining panel has focus for key events
    }

    public void showOldMinerStore() {
        cardLayout.show(cards, "oldMinerStore");
    }

    public void showWitchStore() {
        cardLayout.show(cards, "witchStore");
    }

    private void showGuideDialog() {
        JOptionPane.showMessageDialog(this,
            "Use arrow keys to move around.\nCollect gemstones to earn money.\nAvoid bombs!\nReach the exit for more levels!",
            "Game Guide",
            JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Mining Game");
            frame.setSize(810, 840);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);

            menu main_menu = new menu();
            frame.add(main_menu);

            frame.setVisible(true);
        });
    }
}