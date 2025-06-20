import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Random;
import javax.swing.*;


public class PacMan extends JPanel implements ActionListener, KeyListener {
    class Block {
        int x;
        int y;
        int width;
        int height;
        Image image;

        int startX;  // To know the start position of x when the game restarts
        int startY;  // To know the start position of y when the game restarts
        char direction =  'U';
        int velocityX = 0;
        int velocityY = 0;

        Block(Image image, int x, int y, int width, int height) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.startX = x;
            this.startY = y;
        }

        void updateDirection(char direction) {
            char prevDirection = this.direction;
            this.direction = direction;
            updateVelocity();
            this.x += this.velocityX;  // Update position immediately to avoid flickering
            this.y += this.velocityY;
            for (Block wall: walls) {
                if (isCollision(this, wall)) {
                    this.x -= this.velocityX;
                    this.y -= this.velocityY;  // Revert position if collision occurs, if ghost or pacman collides with a wall
                    this.direction = prevDirection;  
                    updateVelocity(); // Update velocity back to previous direction
                }
            }
        }

        void updateVelocity() {                             //rule switch for velocity
            switch (this.direction) {
                case 'U' -> {
                    this.velocityX = 0;
                    this.velocityY = -tileSize/4;
                }
                case 'D' -> {
                    this.velocityX = 0;
                    this.velocityY = tileSize/4;
                }
                case 'L' -> {
                    this.velocityX = -tileSize/4;
                    this.velocityY = 0;
                }
                case 'R' -> {
                    this.velocityX = tileSize/4; 
                    this.velocityY = 0;
                }
                default -> {
                }
            }
        }
    }
    private int rowCount = 21;
    private int colCount = 19;
    private int tileSize = 32;
    private int boardWidth = colCount * tileSize;
    private int boardHeight = rowCount * tileSize;

    private Image wallImage;
    private Image blueGhostImage;
    private Image orangeGhostImage;
    private Image pinkGhostImage;
    private Image redGhostImage;
    
    private Image pacmanUpImage;
    private Image pacmanDownImage;
    private Image pacmanLeftImage;  
    private Image pacmanRightImage;

    private String[] tileMap = {   
        "xxxxxxxxxxxxxxxxxxx",
        "x        x        x",
        "x xx xxx x xxx xx x",                 //x = wall, o = skip, P=pacman, ' ' = food
        "x                 x",                 // b = blueGhost, p = pinkGhost, O = orangeGhost, r = redGhost
        "x xx x xxxxx x xx x",
        "x    x       x    x",
        "xxxx xxxx xxxx xxxx",
        "ooox x       x xooo",
        "xxxx x xxrxx x xxxx",
        "o       bpO       o",
        "xxxx x xxxxx x xxxx",
        "ooox x       x xooo",
        "xxxx x xxxxx x xxxx",
        "x        x        x",
        "x xx xxx x xxx xx x",
        "x  x     P     x  x",
        "xx x x xxxxx x x xx",
        "x    x   x   x    x",
        "x xxxxxx x xxxxxx x",
        "x                 x",
        "xxxxxxxxxxxxxxxxxxx"
    };

    HashSet<Block> walls;
    HashSet<Block> foods;
    HashSet<Block> ghosts;
    Block pacman;

    Timer gameLoop;
    char directions[] = {'U', 'D', 'L', 'R'};
    Random random = new Random();    //to move the ghost randomly based on directions


    PacMan() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);

        //load the images
        wallImage = new ImageIcon(getClass().getResource("./wall.png")).getImage();
        blueGhostImage = new ImageIcon(getClass().getResource("./blueGhost.png")).getImage();
        orangeGhostImage = new ImageIcon(getClass().getResource("./orangeGhost.png")).getImage();
        pinkGhostImage = new ImageIcon(getClass().getResource("./pinkGhost.png")).getImage();
        redGhostImage = new ImageIcon(getClass().getResource("./redGhost.png")).getImage();
        //load pacman images
        pacmanUpImage = new ImageIcon(getClass().getResource("./pacmanUp.png")).getImage();
        pacmanDownImage = new ImageIcon(getClass().getResource("./pacmanDown.png")).getImage();
        pacmanLeftImage = new ImageIcon(getClass().getResource("./pacmanLeft.png")).getImage();
        pacmanRightImage = new ImageIcon(getClass().getResource("./pacmanRight.png")).getImage();

        loadMap();
        for (Block ghost: ghosts) {
            char newDirection =directions[random.nextInt(4)];  // Randomly select a direction for each ghost
            ghost.updateDirection(newDirection);
        }
        gameLoop = new Timer(50,this);  //20fps  1000/50
        gameLoop.start();
    }

    public void loadMap() {
        walls = new HashSet<Block>();
        foods = new HashSet<Block>();
        ghosts = new HashSet<Block>();

        for (int r = 0; r < rowCount; r++) {
            for (int c = 0;c < colCount; c++) {
                String row = tileMap[r];
                char tileMapChar = row.charAt(c);

                int x = c*tileSize;
                int y = r*tileSize;

                if (tileMapChar == 'x') { 
                    Block wall = new Block(wallImage, x, y, tileSize, tileSize);     //wall
                    walls.add(wall);
                }
                else if (tileMapChar == 'b') {
                    Block ghost = new Block(blueGhostImage, x, y, tileSize, tileSize);   //blue ghost
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'p') {
                    Block ghost = new Block(pinkGhostImage, x, y, tileSize, tileSize);   //pink ghost
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'O') {
                    Block ghost = new Block(orangeGhostImage, x, y, tileSize, tileSize);  //orange ghost
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'r') {
                    Block ghost = new Block(redGhostImage, x, y, tileSize, tileSize);   //red ghost
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'P') {
                    pacman = new Block(pacmanRightImage, x, y, tileSize, tileSize);   //pacman
                }
                else if (tileMapChar == ' ') {
                    Block food = new Block(null, x+14, y+14, 4, 4);  //food
                    foods.add(food);
                }
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }
    
    public void draw(Graphics g) {
        g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);

        for (Block ghost: ghosts) {
        g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
        }

        for (Block wall: walls) {
            g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
        }

        g.setColor(Color.white);   //color of food
        for (Block food: foods) {
            g.fillRect(food.x, food.y, food.width, food.height);
        }
    }

    public void move() {
        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;

        for (Block wall: walls) {
            if (isCollision(pacman, wall)) {
                pacman.x -= pacman.velocityX;       // If collision with wall, revert to previous position
                pacman.y -= pacman.velocityY;
                break;
            }
        }

        for (Block ghost: ghosts) {
            ghost.x += ghost.velocityX;          // Move each ghost
            ghost.y += ghost.velocityY;

            for (Block wall: walls) {
                if (isCollision(ghost, wall)) {    // If collision with wall, revert to previous position
                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;
                    char newDirection = directions[random.nextInt(4)];  // Randomly select a new direction
                    ghost.updateDirection(newDirection);
                }
            }

            if (isCollision(pacman, ghost)) {      // If pacman collides with a ghost, reset the game
                loadMap();
                pacman.updateDirection('R');  // Reset pacman's direction to right
            }   
        }}
    }

    public boolean isCollision(Block block1, Block block2) {
        return block1.x < block2.x + block2.width &&
               block1.x + block1.width > block2.x &&
               block1.y < block2.y + block2.height &&
               block1.y + block1.height > block2.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //System.out.println("keyEvent: "+e.getKeyCode());
        switch (e.getKeyCode()) {                                                    //rule switch for key events
            case KeyEvent.VK_UP -> pacman.updateDirection('U');
            case KeyEvent.VK_DOWN -> pacman.updateDirection('D');
            case KeyEvent.VK_LEFT -> pacman.updateDirection('L');
            case KeyEvent.VK_RIGHT -> pacman.updateDirection('R');
            default -> {
            }
        }

        // Update the image based on the direction
        switch (pacman.direction) {
            case 'U':
                pacman.image = pacmanUpImage;
                break;
            case 'D':
                pacman.image = pacmanDownImage;
                break;
            case 'L':
                pacman.image = pacmanLeftImage;
                break;
            case 'R':
                pacman.image = pacmanRightImage;
                break;
            default:
                break;
        }
    }
}
