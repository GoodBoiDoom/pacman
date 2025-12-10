package pacman;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Random;
import javax.swing.*;

public class theGoodBoi extends JPanel implements ActionListener,KeyListener{

    class Block{
        int x;
        int y;
        int width;
        int height;
        Image image;

        int startX;
        int startY;

        char direction='W';//WASD
        int velocityX=0;
        int velocityY=0;

        Block(Image image, int x, int y, int width, int height){
            this.image = image;
            this.x = x;
            this.y = y;
            this.startX = x;
            this.startY = y;
            this.width = width;
            this.height = height;
        }

        void updateDirection(char direction){
            char prevDirection=this.direction;;
            this.direction=direction;
            updateVelocity();
            this.x+=this.velocityX;
            this.y+=this.velocityY;
            for(Block wall:walls)
                if (collision(this, wall)) {
                    this.x -= this.velocityX;
                    this.y -= this.velocityY;
                    this.direction = prevDirection;
                    updateVelocity();
                    //pacman.updateDirection(pacman.prevDirection);makes pacman bounce back
                    break;//since pacman only moves one position at a time/per gameloop/per frame
                }
            for(Block wall:redWalls)
                if (collision(this, wall)) {
                    this.x -= this.velocityX;
                    this.y -= this.velocityY;
                    //pacman.updateDirection(pacman.prevDirection);makes pacman bounce back
                    break;//since pacman only moves one position at a time/per gameloop/per frame
                }
            this.x -= this.velocityX;
            this.y -= this.velocityY;
        }
        void updateVelocity(){
            if(this.direction=='W'){
                velocityX=0;
                velocityY=-tileSize/4;
            }else if(this.direction=='S'){
                velocityX=0;
                velocityY=+tileSize/4;
            }else if(this.direction=='A'){
                velocityX=-tileSize/4;
                velocityY=0;
            }else if(this.direction=='D'){
                velocityX=+tileSize/4;
                velocityY=0;
            }
        }
    }

    int rowCount=21, columnCount =19;
    int tileSize=32;
    int boardWidth=columnCount*tileSize;
    int boardHeight= rowCount *tileSize;

    private Image wallImage;
    private Image blueGhostImage;
    private Image redGhostImage;
    private Image pinkGhostImage;
    private Image orangeGhostImage;

    private Image pacmanUpImage;
    private Image pacmanDownImage;
    private Image pacmanLeftImage;
    private Image pacmanRightImage;

    HashSet<Block> walls;
    HashSet<Block> redWalls;
    HashSet<Block> foods;
    HashSet<Block> ghosts;
    Block pacman;

    Timer gameLoop;
    char directions[]={'W','A','S','D'};//WASD
    Random rand=new Random();

    theGoodBoi() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);

        //setBackground();
        //loading images
        wallImage = new ImageIcon(getClass().getResource("/wall.png")).getImage();

        pacmanDownImage = new ImageIcon(getClass().getResource("/pacmanDown.png")).getImage();
        pacmanUpImage = new ImageIcon(getClass().getResource("/pacmanUp.png")).getImage();
        pacmanLeftImage = new ImageIcon(getClass().getResource("/pacmanLeft.png")).getImage();
        pacmanRightImage = new ImageIcon(getClass().getResource("/pacmanRight.png")).getImage();

        redGhostImage = new ImageIcon(getClass().getResource("/redGhost.png")).getImage();
        pinkGhostImage = new ImageIcon(getClass().getResource("/pinkGhost.png")).getImage();
        orangeGhostImage = new ImageIcon(getClass().getResource("/orangeGhost.png")).getImage();
        blueGhostImage = new ImageIcon(getClass().getResource("/blueGhost.png")).getImage();

        loadMap();
        for(Block ghost:ghosts){
            char newDirection=directions[rand.nextInt(directions.length)];
            ghost.updateDirection(newDirection);
        }
        gameLoop = new Timer(50,this);//20fps,(1000ms/50ms)
        gameLoop.start();
    }
    //X = wall, O = skip, P = pac man, ' ' = food
    //Ghosts: b = blue, o = orange, p = pink, r = red
    private String[] tileMap = {
            "XXXXXXXXXXXXXXXXXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X                 X",
            "X XX X XXXXX X XX X",
            "X    X       X    X",
            "XXXX XXxbxxx   XXXX",
            "OOOX   x x p   XOOO",
            "XXXX   xxxxx   XXXX",
            "O      r x x      O",
            "XXXX   xxxox   XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXXXX X XXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X  X     P     X  X",
            "XX X X XXXXX X X XX",
            "X    X   X   X    X",
            "X XXXXXX X XXXXXX X",
            "X                 X",
            "XXXXXXXXXXXXXXXXXXX"
    };

    public void loadMap(){
        walls=new HashSet<Block>();
        foods=new HashSet<Block>();
        ghosts=new HashSet<Block>();
        redWalls=new HashSet<Block>();

        for(int r=0;r<rowCount;r++)
            for (int c = 0; c < columnCount; c++) {
                String row = tileMap[r];
                char tileMapchar = row.charAt(c);

                int x = c * tileSize;
                int y = r * tileSize;

                if (tileMapchar == 'X') {//Block Wall
                    Block wall = new Block(wallImage, x, y, tileSize, tileSize);
                    walls.add(wall);
                } else if (tileMapchar == 'x') {//Block Wall
                    Block redWall = new Block(null, x, y, tileSize, tileSize);
                    redWalls.add(redWall);
                } else if (tileMapchar == 'b') {//Blue Ghost
                    Block ghost = new Block(blueGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                } else if (tileMapchar == 'p') {//Pink Ghost
                    Block ghost = new Block(pinkGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                } else if (tileMapchar == 'o') {//Orange ""
                    Block ghost = new Block(orangeGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                } else if (tileMapchar == 'r') {//Red ""
                    Block ghost = new Block(redGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                } else if (tileMapchar == 'P') pacman = new Block(pacmanRightImage, x, y, tileSize, tileSize);
                else if (tileMapchar == ' ') {
                    Block food = new Block(null, x + 14, y + 14, 4, 4);
                    foods.add(food);
                }
            }
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g){
        g.drawImage(pacman.image,pacman.x, pacman.y,  pacman.width, pacman.height,null);

        for(Block wall:walls) g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);

        for(Block ghost:ghosts) g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);

        g.setColor(Color.WHITE);
        for(Block food:foods) g.fillRect(food.x, food.y, food.width, food.height);

        g.setColor(Color.RED);
        for(Block wall:redWalls) g.fillRect(wall.x, wall.y, wall.width - 4, wall.height - 4);

    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        move();
        repaint();
    }

    public void move(){
        pacman.x+=pacman.velocityX;
        pacman.y+=pacman.velocityY;

        //check all the walls for collisions; since they are mapped to a hashset lookup takes up constant time, hence collisions are search for efficiently
        for(Block wall:walls)
            if (collision(pacman, wall)) {
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
                //pacman.updateDirection(pacman.prevDirection);makes pacman bounce back
                break;//since pacman only moves one position at a time/per gameloop/per frame
            }
        for(Block wall:redWalls)
            if (collision(pacman, wall)) {
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
                //pacman.updateDirection(pacman.prevDirection);makes pacman bounce back
                break;//since pacman only moves one position at a time/per gameloop/per frame
            }

        //check for ghost collisions
        for(Block ghost:ghosts){
            ghost.x+=ghost.velocityX;
            ghost.y+=ghost.velocityY;
            for(Block wall:walls)
                if (collision(ghost, wall)) {
                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;
                    char newDirection=directions[rand.nextInt(directions.length)];
                    ghost.updateDirection(newDirection);
                    //pacman.updateDirection(pacman.prevDirection);makes pacman bounce back
                    break;//since pacman only moves one position at a time/per gameloop/per frame
                }
            for(Block wall:redWalls)
                if (collision(ghost, wall)) {
                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;
                    char newDirection=directions[rand.nextInt(directions.length)];
                    ghost.updateDirection(newDirection);
                    //pacman.updateDirection(pacman.prevDirection);makes pacman bounce back
                    break;//since pacman only moves one position at a time/per gameloop/per frame
                }
        }
    }

    public boolean collision(Block a, Block b){
        return a.x<b.x+b.width && a.y<b.y+b.height && a.x+a.width>b.x && a.y+a.height>b.y;
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {

    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {}

    @Override
    public void keyReleased(KeyEvent keyEvent) {
        //System.out.println("KeyEvent: "+KeyEvent.getKeyText(keyEvent.getKeyCode()) );
        //four directions WASD
        if(keyEvent.getKeyCode()==KeyEvent.VK_UP) pacman.updateDirection('W');
        else if(keyEvent.getKeyCode()==KeyEvent.VK_DOWN) pacman.updateDirection('S');
        else if(keyEvent.getKeyCode()==KeyEvent.VK_LEFT) pacman.updateDirection('A');
        else if(keyEvent.getKeyCode()==KeyEvent.VK_RIGHT) pacman.updateDirection('D');

        if(pacman.direction=='W')
            pacman.image=pacmanUpImage;
        else if(pacman.direction=='S')
            pacman.image=pacmanDownImage;
        else if(pacman.direction=='A')
            pacman.image=pacmanLeftImage;
        else if(pacman.direction=='D')
            pacman.image=pacmanRightImage;
    }

}
