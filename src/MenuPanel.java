import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class MenuPanel extends JPanel {
    private JButton startButton;
    private JButton guideButton;
    private JLabel titleLabel;
    private JLabel subTitleLabel;

    public MenuPanel(ActionListener startListener, ActionListener guideListener) {
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        // Title label
        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);

        titleLabel = new JLabel("MINESPREE", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);

        subTitleLabel = new JLabel("purely java coded!", SwingConstants.CENTER);
        subTitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subTitleLabel.setForeground(Color.WHITE);
        titlePanel.add(subTitleLabel);

        titlePanel.setBorder(new EmptyBorder(50, 0, 0, 0));
        add(titlePanel, BorderLayout.NORTH);

        // Center panel for images or other content
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        // Example: add image or other components to centerPanel
        // centerPanel.add(new JLabel(new ImageIcon("path_to_your_image.jpg")), BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // Panel for buttons
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 20, 0));

        // Start Game button
        startButton = new JButton("Start Game");
        startButton.setFont(new Font("Arial", Font.BOLD, 16));
        startButton.addActionListener(startListener);
        startButton.setFocusPainted(false);
        buttonPanel.add(startButton);

        // Guide button
        guideButton = new JButton("Guide");
        guideButton.setFont(new Font("Arial", Font.BOLD, 16));
        guideButton.addActionListener(guideListener);
        guideButton.setFocusPainted(false);
        buttonPanel.add(guideButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }
}