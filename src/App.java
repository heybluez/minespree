import javax.swing.*;
import java.awt.*;

public class App {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Mining Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(615, 635);
        frame.setLocationRelativeTo(null); // Center on screen

        // Create the mining game instance with appropriate parameters
        mining gamePanel = new mining(800, 800, Color.BLACK);

        // Add the game panel to the frame
        frame.add(gamePanel);

        frame.setVisible(true);
    }
}