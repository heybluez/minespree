import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.*;

public class mining extends JPanel implements ActionListener, KeyListener {

    public class Tile {
        int x;
        int y;
        BufferedImage image;
        String type;
    
        Tile(int x, int y, BufferedImage image, String type) {
            this.x = x;
            this.y = y;
            this.image = image;
            this.type = type;
        }
    }

private static final int GRID_SIZE = 8;
private int tileSize;
private int boardWidth;
private int boardHeight;

private int money = 1000;
private int pickaxeDurability = 1000;
private int round = 1;

private Tile miningStart;
private Tile bomb;
private Tile exit;
private ArrayList<Tile> gemstones;
private ArrayList<Tile> stones;
private ArrayList<Tile> stores;

private Random random;

private BufferedImage rubyImage;
private BufferedImage diamondImage;
private BufferedImage topazImage;
private BufferedImage goldImage;
private BufferedImage bombImage;
private BufferedImage miningStartImage;
private BufferedImage exitImage;
private BufferedImage stoneImage;
private BufferedImage hardStoneImage;
private BufferedImage obsidianImage;
private BufferedImage store1Image;
//private BufferedImage store2Image;
//private BufferedImage store3Image;
private BufferedImage backgroundImage;

private Timer gameLoop;
private boolean hasHandyPickaxe = false;
private boolean hasMiningHelmet = false;
private static final double BASE_REPAIR_KIT_PRICE = 200.0; // Base price of the Repair Kit

private boolean fortunePotionActive = false;
private boolean shieldPotionActive = false;
private boolean gloriousPotionActive = false;
private long fortunePotionEndTime;
private long shieldPotionEndTime;
private long gloriousPotionEndTime;
private int playerHealth = 100;
private BigBully bigBully;
private boolean bigBullyRound = false;
private Timer bossDelayTimer;
private boolean bossReadyToShoot = false;
private BufferedImage bigBullyImage;
private BufferedImage projectileImage;

private int velocityX = 0;
private int velocityY = 0;
private Timer shootTimer;
private Timer bossRoundTimer;

private int jumpStartY = -1; // Track the Y position when the jump starts
private boolean isJumping = false;
private int jumpVelocity = 0;
private int maxJumpDuration = 10; // Number of game ticks the jump lasts
private int currentJumpDuration = 0;
private static final int GRAVITY = 1; // Gravity constant
private boolean isFalling = false; // Track if the character is falling

public mining(int width, int height, Color bgColor) {
    this.boardWidth = width;
    this.boardHeight = height;
    this.tileSize = Math.min(boardWidth, boardHeight) / GRID_SIZE;
    setPreferredSize(new Dimension(boardWidth, boardHeight));
    setBackground(bgColor);
    addKeyListener(this);
    setFocusable(true);

    miningStartImage = loadImage("src/images/character.png");
    rubyImage = loadImage("src/images/ruby.jpg");
    diamondImage = loadImage("src/images/diamond.jpg");
    topazImage = loadImage("src/images/topaz.jpg");
    goldImage = loadImage("src/images/gold.jpg");
    bombImage = loadImage("src/images/bomb.png");
    exitImage = loadImage("src/images/exit.jpg");
    stoneImage = loadImage("src/images/stone.jpg");
    hardStoneImage = loadImage("src/images/hardstone.jpg");
    obsidianImage = loadImage("src/images/obsidian.jpg");
    store1Image = loadImage("src/images/npc1.jpg");
    //store2Image = loadImage("src/images/store.png");
    //store3Image = loadImage("src/images/store.png");
    bigBullyImage = loadImage("src/images/character.png");
    projectileImage = loadImage("src/images/stone.jpg");

    try {
        backgroundImage = ImageIO.read(new File("src/images/miningbackground.png"));
    } catch (IOException e) {
        System.err.println("Error loading background image: " + e.getMessage());
    }

    miningStart = new Tile(0, 0, miningStartImage, "start");
    bomb = new Tile(-1, -1, bombImage, "bomb");
    exit = new Tile(GRID_SIZE - 1, GRID_SIZE - 1, exitImage, "exit");
    gemstones = new ArrayList<>();
    stones = new ArrayList<>();
    stores = new ArrayList<>();

    random = new Random();

    placeGemstones();
    placeBomb();
    fillRemainingWithStones();
    bigBully = new BigBully(GRID_SIZE / 2, 0, bigBullyImage, projectileImage);

    gameLoop = new Timer(16, this); // About 60 FPS
    gameLoop.start();

    shootTimer = new Timer(1000, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (bigBullyRound) {
                bigBully.shootRandom(); // Randomly shoot projectiles from the sky
            }
        }
    });
    shootTimer.start();

    bossRoundTimer = new Timer(30000, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (bigBullyRound) {
                bigBullyRound = false;
                exit = new Tile(GRID_SIZE - 1, GRID_SIZE - 1, exitImage, "exit");
                repaint(); // Refresh the display to show the exit
                JOptionPane.showMessageDialog(mining.this, "You are lucky this time! Don't come again.");
                bossRoundTimer.stop();
            }
        }
    });
}




@Override
public void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (backgroundImage != null) {
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }
    g.setColor(Color.BLACK);
    for (int i = 0; i <= GRID_SIZE; i++) {
        g.drawLine(i * tileSize, 0, i * tileSize, boardHeight);
        g.drawLine(0, i * tileSize, boardWidth, i * tileSize);
    }
    drawTile(g, miningStart);
    drawTile(g, bomb);
    drawTile(g, exit);
    for (Tile gem : gemstones) {
        drawTile(g, gem);
    }
    for (Tile stone : stones) {
        drawTile(g, stone);
    }
    for (Tile store : stores) {
        drawTile(g, store);
    }
    g.setColor(Color.WHITE);
    g.setFont(new Font("Arial", Font.PLAIN, 16));
    g.drawString("Money: " + money + "$", 10, 20);
    g.drawString("Durability: " + pickaxeDurability, 10, 40);
    g.drawString("Round: " + round, 10, 60);
    if (fortunePotionActive) {
        g.drawString("Fortune Potion Active", 10, 80);
    }
    if (shieldPotionActive) {
        g.drawString("Shield Potion Active", 10, 100);
    }
    if (gloriousPotionActive) {
        g.drawString("Glorious Potion Active", 10, 120);
    }
    if (pickaxeDurability <= 0) {
        g.setColor(Color.RED);
        g.drawString("Game Over - Pickaxe Broken!", 10, 140);
    }
    if (bigBullyRound) {
        bigBully.draw(g, tileSize); // Draw falling projectiles
    }
    g.setColor(Color.RED);
    g.drawString("Health: " + playerHealth, 10, 160);
}

    private void drawTile(Graphics g, Tile tile) {
        if (tile.image != null) {
            g.drawImage(tile.image, tile.x * tileSize, tile.y * tileSize, tileSize, tileSize, this);
        }
    }

    private BufferedImage loadImage(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            System.err.println("Error loading image: " + e.getMessage());
            return null;
        }
    }

    public void placeGemstones() {
        ArrayList<BufferedImage> gemstoneImages = new ArrayList<>();
        gemstoneImages.add(rubyImage);
        gemstoneImages.add(diamondImage);
        gemstoneImages.add(topazImage);
        gemstoneImages.add(goldImage);
    
        int numGemstones;
        if (hasMiningHelmet) {
            numGemstones = 2 + random.nextInt(5); // 2-6 gemstones if player has Mining Helmet
        } else {
            numGemstones = 2 + random.nextInt(3); // 2-4 gemstones otherwise
        }
    
        numGemstones = Math.min(numGemstones, GRID_SIZE * GRID_SIZE - 4);
    
        ArrayList<Tile> placedGemstones = new ArrayList<>();
    
        for (int i = 0; i < numGemstones; i++) {
            Tile gem = new Tile(-1, -1, null, "gemstone");
            do {
                gem.x = random.nextInt(GRID_SIZE);
                gem.y = random.nextInt(GRID_SIZE);
            } while (collision(gem, miningStart) || collision(gem, bomb) || collision(gem, exit) || isTileOccupied(gem, placedGemstones));
    
            gem.image = gemstoneImages.get(random.nextInt(gemstoneImages.size()));
            gem.type = getGemstoneType(gem.image);
    
            placedGemstones.add(gem);
        }
    
        gemstones.clear();
        gemstones.addAll(placedGemstones);
    }

    private boolean isTileOccupied(Tile tile, ArrayList<Tile> placedTiles) {
        for (Tile t : placedTiles) {
            if (collision(tile, t)) {
                return true;
            }
        }
        return false;
    }

    private String getGemstoneType(BufferedImage image) {
        if (image == rubyImage) return "ruby";
        if (image == diamondImage) return "diamond";
        if (image == topazImage) return "topaz";
        if (image == goldImage) return "gold";
        return "";
    }

    public void placeBomb() {
        do {
            bomb.x = random.nextInt(GRID_SIZE);
            bomb.y = random.nextInt(GRID_SIZE);
        } while (collision(bomb, miningStart) || collision(bomb, exit) || isTileOccupied(bomb, gemstones));
    }

    public void fillRemainingWithStones() {
        stones.clear();
        for (int x = 0; x < GRID_SIZE; x++) {
            for (int y = 0; y < GRID_SIZE; y++) {
                if (collision(new Tile(x, y, null, ""), miningStart) ||
                    collision(new Tile(x, y, null, ""), bomb) ||
                    collision(new Tile(x, y, null, ""), exit) ||
                    isTileOccupied(new Tile(x, y, null, ""), gemstones) ||
                    isTileOccupied(new Tile(x, y, null, ""), stores)) {
                    continue;
                }
                Tile stone = new Tile(x, y, null, "");
                int durability = getStoneDurability();
                if (durability == 1) {
                    stone.image = stoneImage;
                } else if (durability == 2) {
                    stone.image = hardStoneImage;
                } else {
                    stone.image = obsidianImage;
                }
                stones.add(stone);
            }
        }
    }

    

    private boolean collision(Tile tile1, Tile tile2) {
    return tile1.x == tile2.x && tile1.y == tile2.y;
}

private long roundStartTime;

public class BigBully {
    ArrayList<Projectile> projectiles;
    Random random;
    boolean hasSpoken;
    long dialogStartTime;
    int dialogDuration = 3000; // 3 seconds for the dialog to appear

    public BigBully(int x, int y, BufferedImage image, BufferedImage projectileImage) {
        this.projectiles = new ArrayList<>();
        this.random = new Random();
        this.hasSpoken = false;
        this.dialogStartTime = System.currentTimeMillis();
    }

    public void shootRandom() {
        int startX = random.nextInt(GRID_SIZE);
        int numProjectiles = 3; // Number of projectiles to shoot in a burst
        int spacing = 2; // Spacing between the projectiles in terms of grid cells

        for (int i = 0; i < numProjectiles; i++) {
            int xOffset = i * spacing; // Calculate offset for each projectile
            int startXWithOffset = (startX + xOffset) % GRID_SIZE; // Wrap around if necessary
            projectiles.add(new Projectile(startXWithOffset, 0, projectileImage));
        }
    }

    public void moveProjectiles() {
        for (int i = 0; i < projectiles.size(); i++) {
            Projectile p = projectiles.get(i);
            p.moveDown();
    
            // Remove the projectile if it goes out of bounds
            if (p.isOutOfBounds()) {
                projectiles.remove(i);
                i--; // Adjust the index after removal
            }
        }
    }

    public void draw(Graphics g, int tileSize) {
        // Draw the Big Bully image at the top center
        g.drawImage(bigBullyImage, (GRID_SIZE / 2 - 1) * tileSize, 0, tileSize * 2, tileSize * 2, null);

        // Display dialog if the bully hasn't spoken yet
        if (!hasSpoken) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - dialogStartTime <= dialogDuration) {
                g.setColor(Color.BLACK);
                g.fillRect(50, 100, 300, 50);
                g.setColor(Color.WHITE);
                g.drawString("What are you doing in my cave??", 60, 130);
            } else {
                hasSpoken = true;
            }
        }

        // Draw falling projectiles
        for (Projectile p : projectiles) {
            p.draw(g, tileSize);
        }
    }
}

public class Projectile {
    int x, y;
    BufferedImage image;
    double speed = 1; // Speed in pixels per frame

    public Projectile(int x, int y, BufferedImage image) {
        this.x = x;
        this.y = y;
        this.image = image;
    }

    public void moveDown() {
        y += speed; // Move down by speed pixels
    }

    public void draw(Graphics g, int tileSize) {
        g.drawImage(image, x * tileSize, y * tileSize, tileSize, tileSize, null);
    }

    public boolean isOutOfBounds() {
        // Allow the projectile to go a bit further beyond the grid before disappearing
        return y * tileSize >= getHeight();
    }
}

    private void resetGame() {
        stores.clear();
        round++;
        gemstones.clear();
        stones.clear();
        placeGemstones();
        placeBomb();
        fillRemainingWithStones();
        miningStart.x = 0;
        miningStart.y = 0;
        velocityX = 0;
        velocityY = 0;
    
        if (round % 5 == 0) {
            placeStores();
            gemstones.clear();
            stones.clear();
            bomb.x = -1;
            bomb.y = -1;
            miningStart.x = 4;
            miningStart.y = 0;
        }
    
        if (round % 5 != 0 && random.nextInt(2) == 0) { // 50% chance to spawn Big Bully
            bigBullyRound = true;
            bigBully = new BigBully(GRID_SIZE / 2, 0, bigBullyImage, projectileImage); // Boss at the top
            miningStart.x = GRID_SIZE / 2;
            miningStart.y = GRID_SIZE - 1; // Player at the bottom
            gemstones.clear();
            stones.clear();
            bomb.x = -1;
            bomb.y = -1;
            exit.x = -1;
            exit.y = -1;
            System.out.println("Big Bully Round initiated!");
    
            // Start the boss delay timer after the dialog duration
            bossDelayTimer = new Timer(3000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    bossReadyToShoot = true;
                    bossDelayTimer.stop();
                    bossRoundTimer.start(); // Start the boss round timer
                }
            });
            bossDelayTimer.setInitialDelay(3000); // Set delay for dialog duration
            bossDelayTimer.start();
    
            shootTimer = new Timer(2000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (bigBullyRound) {
                        bigBully.moveProjectiles(); // Update projectile positions
                        checkCollisions(); // Check for collisions
                    }
                }
            });
            shootTimer.start();
        } else {
            bigBullyRound = false;
            System.out.println("Regular round.");
        }
    
        if (gloriousPotionActive) {
            long elapsedTime = System.currentTimeMillis() - gloriousPotionEndTime;
            int timeSpent = (int) (elapsedTime / 1000); // Time in seconds
    
            // Increase money earned based on time spent
            int bonus = Math.max(0, 1000 - (timeSpent * 10));
            money += bonus;
        }
    
        repaint();
    }

    private boolean isCollision(Projectile p, Tile player) {
        return p.x * tileSize < (player.x + 1) * tileSize &&
               (p.x + 1) * tileSize > player.x * tileSize &&
               p.y * tileSize < (player.y + 1) * tileSize &&
               (p.y + 1) * tileSize > player.y * tileSize;
    }

    private void checkCollisions() {
        if (bigBullyRound) {
            for (int i = 0; i < bigBully.projectiles.size(); i++) {
                Projectile p = bigBully.projectiles.get(i);
    
                // Check if the projectile collides with the player
                if (isCollision(p, miningStart)) {
                    playerHealth -= 10; // Adjust the damage as needed
                    bigBully.projectiles.remove(i);
                    i--; // Decrement the index to account for the removed projectile
                }
            }
        }
    }

    public void move(int dx, int dy) {
        if (pickaxeDurability <= 0) {
            return;
        }
    
        // Handle Jumping
        if (dy < 0 && !isJumping && !isFalling && isOnBlock()) {
            isJumping = true;
            jumpStartY = miningStart.y; // Store the starting Y position for jumping
            jumpVelocity = 5; // Set initial jump velocity
        }
    
        if (isJumping) {
            // Update the vertical position during the jump
            miningStart.y = jumpStartY - jumpVelocity; // Jump exactly one tile up
            jumpVelocity--; // Decrease velocity
            
            // When the jump is complete
            if (jumpVelocity < 0) {
                isJumping = false;
                jumpStartY = -1;
                // Reset Y position to simulate falling
                if (!isOnBlock()) {
                    isFalling = true;
                }
            }
        } else {
            // Horizontal movement
            int newX = miningStart.x + dx;
            int newY = miningStart.y;
        
            if (dy > 0 && !isOnBlock()) { // Falling
                newY = miningStart.y + dy;
            }
        
            // Ensure the character does not move beyond grid boundaries
            if (newX >= 0 && newX < GRID_SIZE && newY >= 0 && newY < GRID_SIZE) {
                miningStart.x = newX;
                miningStart.y = newY;
            }
        }
    
        isFalling = !isOnBlock(); // Check if the character is falling
        
        if (isFalling) {
            applyGravity(); // Apply gravity if falling
        }
    
        checkCollisions(); // Check for collisions with other tiles
        handleTileCollisions(); // Handle collisions with tiles
    }
    
    // Move this method outside of `move` method
    private void handleTileCollisions() {
        for (int i = 0; i < gemstones.size(); i++) {
            if (collision(miningStart, gemstones.get(i))) {
                Tile gem = gemstones.get(i);
                int baseValue = 0;
                int durabilityCost = 3;
    
                switch (gem.type) {
                    case "ruby":
                        baseValue = 30;
                        break;
                    case "diamond":
                        baseValue = 40;
                        break;
                    case "topaz":
                        baseValue = 20;
                        break;
                    case "gold":
                        baseValue = 25;
                        break;
                }
    
                if (hasHandyPickaxe) {
                    baseValue += 10; // Increase by 10 if Handy Pickaxe is purchased
                }
    
                if (fortunePotionActive) {
                    baseValue = (int) (baseValue * 1.1); // Increase money by 10% if Fortune Potion is active
                }
    
                money += baseValue;
                pickaxeDurability -= durabilityCost;
                gemstones.remove(i);
                break;
            }
        }
    
        if (collision(miningStart, bomb)) {
            if (!shieldPotionActive) {
                pickaxeDurability -= 100;
                bomb.x = -1;
                bomb.y = -1;
            }
        }
    
        for (int i = 0; i < stones.size(); i++) {
            if (collision(miningStart, stones.get(i))) {
                Tile stone = stones.get(i);
                if (stone.image == stoneImage) {
                    pickaxeDurability -= 1;
                } else if (stone.image == hardStoneImage) {
                    pickaxeDurability -= 2;
                } else if (stone.image == obsidianImage) {
                    pickaxeDurability -= 3;
                }
                stones.remove(i);
                break;
            }
        }
    
            if (collision(miningStart, exit)) {
                if (gloriousPotionActive) {
                    long timeSpent = System.currentTimeMillis() - (roundStartTime);
                    int bonusMoney = (int) (money * Math.max(1 - (timeSpent / 60000.0), 0)); // Less time, more money
                    money += bonusMoney;
                }
                resetGame();
            }
    
            for (int i = 0; i < stores.size(); i++) {
                if (collision(miningStart, stores.get(i))) {
                    showStoreDialog();
                    miningStart.x = GRID_SIZE / 2;
                    miningStart.y = GRID_SIZE / 2;
                }
            }
    
            repaint();
        }
    



        private boolean isOnBlock() {
            // Check if there's a tile directly below the character
            int belowX = miningStart.x;
            int belowY = miningStart.y + 1;
            
            // Check if the position below the character is within bounds
            if (belowY >= GRID_SIZE) {
                return false; // Beyond grid limits
            }
            
            // Check if the tile below is occupied by a stone
            for (Tile stone : stones) {
                if (stone.x == belowX && stone.y == belowY) {
                    return true; // Character is on a block (stone)
                }
            }

            for(Tile gemstone : gemstones){
                if (gemstone.x == belowX && gemstone.y == belowY){
                    return true;
                }
            }
            
            return false; // No block below
        }

        private void applyGravity() {
            if (isFalling) {
                miningStart.y += GRAVITY;
                if (miningStart.y >= GRID_SIZE - 1) { // Prevent going below the grid
                    miningStart.y = GRID_SIZE - 1;
                    isFalling = false;
                }
            }
        }



    private int getStoneDurability() {
        int chance = random.nextInt(100);
        if (chance < 70) {
            return 1;
        } else if (chance < 90) {
            return 2;
        } else {
            return 3;
        }
    }

    public boolean hasHandyPickaxe() {
        return hasHandyPickaxe;
    }
    
    public void setHandyPickaxe(boolean hasHandyPickaxe) {
        this.hasHandyPickaxe = hasHandyPickaxe;
    }
    
    public boolean hasMiningHelmet() {
        return hasMiningHelmet;
    }
    
    public void setMiningHelmet(boolean hasMiningHelmet) {
        this.hasMiningHelmet = hasMiningHelmet;
    }

    public void placeStores() {
        stores.clear();
        stores.add(new Tile(0, 0, store1Image, "Old Miner Store"));
        stores.add(new Tile(GRID_SIZE - 1, 0, store1Image, "Witch Store")); // Place Witch Store at (7, 0)
    }

    public int getMoney() {
        return money;
    }
    
    public void decreaseMoney(int amount) {
        money -= amount;
    }

    public int getRepairKitPrice() {
        return (int) Math.round(BASE_REPAIR_KIT_PRICE + (round * 10)); 
    }

    public void setFortunePotion(boolean active) {
        fortunePotionActive = active;
        if (active) {
            fortunePotionEndTime = System.currentTimeMillis() + 3 * 60 * 1000; // 3 minutes
        }
    }
    
    public void setShieldPotion(boolean active) {
        shieldPotionActive = active;
        if (active) {
            shieldPotionEndTime = System.currentTimeMillis() + 3 * 60 * 1000; // 3 minutes
        }
    }
    
    public void setGloriousPotion(boolean active) {
        gloriousPotionActive = active;
        if (active) {
            gloriousPotionEndTime = System.currentTimeMillis() + 1 * 60 * 1000; // 1 minute
        }
    }
    
        // Create a Timer that fires once after the duration
    
    
        public void setItemBought(int index) {
            switch (index) {
                case 0:
                    hasHandyPickaxe = true;
                    break;
                case 1:
                    pickaxeDurability += 200; // Apply repair kit effect
                    break;
                case 2:
                    hasMiningHelmet = true;
                    break;
                case 3: // Assuming Fortune Potion
                    setFortunePotion(true);
                    break;
                case 4: // Assuming Shield Potion
                    setShieldPotion(true);
                    break;
                case 5: // Assuming Glorious Potion
                    setGloriousPotion(true);
                    break;
            }
        }

    private void showStoreDialog() {
        Tile currentStore = null;
        for (Tile store : stores) {
            if (collision(miningStart, store)) {
                currentStore = store;
                break;
            }
        }
    
        if (currentStore != null) {
            if (currentStore.type.equals("Old Miner Store")) {
                oldMinerStorePanel panel = new oldMinerStorePanel(this);
                JDialog dialog = new JDialog((Frame) null, "Old Miner Store", true);
                dialog.setLayout(new BorderLayout());
                dialog.add(panel, BorderLayout.CENTER);
                dialog.setSize(1000, 700);
                dialog.setLocationRelativeTo(null);
                dialog.setVisible(true);
            } else if (currentStore.type.equals("Witch Store")) {
                WitchStorePanel panel = new WitchStorePanel(this);
                JDialog dialog = new JDialog((Frame) null, "Witch Store", true);
                dialog.setLayout(new BorderLayout());
                dialog.add(panel, BorderLayout.CENTER);
                dialog.setSize(1000, 700);
                dialog.setLocationRelativeTo(null);
                dialog.setVisible(true);
            }
        }
    }

    @Override
public void actionPerformed(ActionEvent e) {
    long currentTime = System.currentTimeMillis();

    if (fortunePotionActive && currentTime > fortunePotionEndTime) {
        fortunePotionActive = false;
    }

    if (shieldPotionActive && currentTime > shieldPotionEndTime) {
        shieldPotionActive = false;
    }

    if (gloriousPotionActive && currentTime > gloriousPotionEndTime) {
        gloriousPotionActive = false;
    }

    move(velocityX, velocityY);

    if (isFalling) {
        applyGravity();
    }

    // Apply gravity if not jumping
    if (!isJumping) {
        velocityY += GRAVITY;
    }

    if (isJumping) {
        currentJumpDuration++;
        if (currentJumpDuration > maxJumpDuration) {
            isJumping = false;
            currentJumpDuration = 0;
        }
    }

    // Update player position
    miningStart.y += velocityY;

    // Collision detection with the ground and stones
    if (miningStart.y >= GRID_SIZE) {
        miningStart.y = GRID_SIZE - 1;
        velocityY = 0;
    }

    // Prevent moving out of the screen
    if (miningStart.y < 0) {
        miningStart.y = 0;
        velocityY = 0;
    }

    // Check collision with the ground and stones
    for (Tile stone : stones) {
        if (collision(miningStart, stone)) {
            miningStart.y -= velocityY;
            velocityY = 0;
            isJumping = false;
        }
    }




    if (bigBullyRound) {
        if (bossReadyToShoot) {
            bigBully.moveProjectiles();
        }
        bigBully.moveProjectiles();

        // Check for collisions with the player
        for (Projectile p : bigBully.projectiles) {
            if (p.x == miningStart.x && p.y == miningStart.y) {
                playerHealth -= 25;
                if (playerHealth <= 0) {
                    // Game over logic
                }
            }
        }
    }

    repaint();
}

@Override
public void keyPressed(KeyEvent e) {
    int keyCode = e.getKeyCode();
    switch (keyCode) {
        case KeyEvent.VK_LEFT:
            move(-1, 0);
            break;
        case KeyEvent.VK_RIGHT:
            move(1, 0);
            break;
        case KeyEvent.VK_UP:
            move(0, -1);
            break;
        case KeyEvent.VK_DOWN:
            move(0, 1);
            break;
        default:
            break;
    }
}

@Override
public void keyReleased(KeyEvent e) {
    int key = e.getKeyCode();

    if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT) {
        velocityX = 0;
    }

    if (key == KeyEvent.VK_UP || key == KeyEvent.VK_DOWN) {
        velocityY = 0;
    }
}

@Override
public void keyTyped(KeyEvent e) {
    // Not used
}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Mining Game");
        mining game = new mining(800, 800, Color.BLACK);
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}