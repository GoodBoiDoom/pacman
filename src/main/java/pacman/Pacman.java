package pacman;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Random;
import javax.swing.*;

public class Pacman extends JPanel implements ActionListener,KeyListener{

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
            char prevDirection=this.direction;
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
                velocityY=-tileSize/8;
            }else if(this.direction=='S'){
                velocityX=0;
                velocityY=+tileSize/8;
            }else if(this.direction=='A'){
                velocityX=-tileSize/8;
                velocityY=0;
            }else if(this.direction=='D'){
                velocityX=+tileSize/8;
                velocityY=0;
            }
        }
        void reset(){
            this.x=startX;
            this.y=startY;
            this.velocityX=0;
            this.velocityY=0;
        }
    }

    int rowCount=21, columnCount =19;
    int tileSize=32;
    int boardWidth=columnCount*tileSize;
    int boardHeight= rowCount *tileSize;

    private Image wallImage;
    private Image redWallImage;
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
    int score=0;
    int lives=3;
    boolean gameOver=false;

    Pacman() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);

        //setBackground();
        //loading images
        wallImage = new ImageIcon(getClass().getResource("/wall.png")).getImage();
        redWallImage=new ImageIcon(getClass().getResource("/redWall.png")).getImage();

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
        gameLoop = new Timer(33,this);//≈60fps,(1000ms/16ms)
        gameLoop.start();
    }
    //X = wall, O = skip, P = pac man, ' ' = food
    //Ghosts: b = blue, o = orange, p = pink, r = red
    private String[] tileMap = {
            "X XX XXXXXX XX XX X",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X                 X",
            "X XX X XXXXX X XX X",
            "X    X       X    X",
            "XXXX XXxbxxxXX XXXX",
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
            "X XX XX XXXXXX XX X"
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
                    Block redWall = new Block(redWallImage, x, y, tileSize, tileSize);
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


        //score
        g.setFont(new Font("Times New Roman", Font.BOLD, 15));
        if(gameOver){
            g.setColor(Color.RED);
            g.setFont(new Font("Chiller", Font.BOLD, 50));
            g.drawString("GAME OVER!!!",boardWidth/4, boardHeight/2-tileSize*2);
            g.setFont(new Font("Chiller", Font.BOLD, 30));
            g.setColor(Color.WHITE);
            g.drawString("SCORE: "+String.valueOf(score), boardWidth/3, boardHeight/2-tileSize/2);
            g.setFont(new Font("Chiller", Font.BOLD, 20));
            g.setColor(Color.GREEN);
            g.drawString("Press ANY key to restart the game!", boardWidth/4, boardHeight/2+tileSize/2);
        }else{
            g.drawImage(pacman.image,pacman.x, pacman.y,  pacman.width, pacman.height,null);

            for(Block wall:walls) g.drawImage(wall.image, wall.x+4, wall.y+4, wall.width - 8, wall.height - 8, null);

            for(Block ghost:ghosts) g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);

            g.setColor(Color.WHITE);
            for(Block food:foods) g.fillRect(food.x, food.y, food.width, food.height);

            //for(Block wall:walls) g.fillRect(wall.x+4, wall.y+4, wall.width - 8, wall.height - 8);

            for(Block wall:redWalls)g.drawImage(wall.image, wall.x+4, wall.y+4, wall.width - 8, wall.height - 8, null);

            g.setColor(Color.RED);
            g.drawString("x"+String.valueOf(lives)+" Score: "+String.valueOf(score), tileSize/4, tileSize/2);
        }
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        move();
        repaint();
        if(gameOver){
            gameLoop.stop();
        }
    }

    public void move(){
        pacman.x+=pacman.velocityX;
        pacman.y+=pacman.velocityY;

        if(pacman.x<=0) pacman.x=(boardWidth-1*tileSize)+pacman.x;
        if(pacman.x>boardWidth) pacman.x=0;
        if(pacman.y<=0) pacman.y=(boardHeight-1*tileSize)+pacman.y;
        if(pacman.y>boardHeight) pacman.y=0;
        //check all the walls for collisions; since they are mapped to a hashset lookup takes up constant time, hence collisions are search for efficiently
        for(Block wall:walls)
            if (collision(pacman, wall)) {
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
                if(pacman.x<=0) pacman.x=(boardWidth-1*tileSize)+pacman.x;
                if(pacman.x>boardWidth) pacman.x=0;
                if(pacman.y<=0) pacman.y=(boardHeight-1*tileSize)+pacman.y;
                if(pacman.y>boardHeight) pacman.y=0;
                //pacman.updateDirection(pacman.prevDirection);makes pacman bounce back
                break;//since pacman only moves one position at a time/per gameloop/per frame
            }
        for(Block wall:redWalls)
            if (collision(pacman, wall)) {
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
                if(pacman.x<=0) pacman.x=(boardWidth-1*tileSize)+pacman.x;
                if(pacman.x>boardWidth) pacman.x=0;
                if(pacman.y<=0) pacman.y=(boardHeight-1*tileSize)+pacman.y;
                if(pacman.y>boardHeight) pacman.y=0;
                //pacman.updateDirection(pacman.prevDirection);makes pacman bounce back
                break;//since pacman only moves one position at a time/per gameloop/per frame
            }

        //check for ghost collisions
        for(Block ghost:ghosts){
            if(collision(pacman, ghost)) {
                lives-=1;
                resetPositions();
            }
            if(lives<=0){
                gameOver=true;
                return;
            }
            ghost.x+=ghost.velocityX;
            ghost.y+=ghost.velocityY;
            if(ghost.x<=0) ghost.x=(boardWidth-1*tileSize)+ghost.x;
            if(ghost.x>boardWidth) ghost.x=0;
            if(ghost.y<=0) ghost.y=(boardHeight-1*tileSize)+ghost.y;
            if(ghost.y>boardHeight) ghost.y=0;
            for(Block wall:walls) {
                if (collision(ghost, wall)) {
                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;
                    if (ghost.x <= 0) ghost.x = (boardWidth - 1 * tileSize) + ghost.x;
                    if (ghost.x > boardWidth) ghost.x = 0;
                    if (ghost.y <= 0) ghost.y = (boardHeight - 1 * tileSize) + ghost.y;
                    if (ghost.y > boardHeight) ghost.y = 0;
                    char newDirection = directions[rand.nextInt(directions.length)];
                    ghost.updateDirection(newDirection);
                    //pacman.updateDirection(pacman.prevDirection);makes pacman bounce back
                    break;//since pacman only moves one position at a time/per gameloop/per frame
                }
            }
            for(Block wall:redWalls) {
                if (collision(ghost, wall)) {
                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;
                    if (ghost.x > boardWidth) ghost.x = 0;
                    if (ghost.y <= 0) ghost.y = (boardHeight - 1 * tileSize) + ghost.y;
                    if (ghost.y > boardHeight) ghost.y = 0;
                    char newDirection = directions[rand.nextInt(directions.length)];
                    ghost.updateDirection(newDirection);
                    //pacman.updateDirection(pacman.prevDirection);makes pacman bounce back
                    break;//since pacman only moves one position at a time/per gameloop/per frame
                }
            }
        }

        //food collisions
        for(Block food:foods){
            if (collision(pacman, food)) {
                foods.remove(food);
                score+=100;
                break;
            }
        }
        if(foods.isEmpty()){
            loadMap();
            resetPositions();
        }
    }

    public boolean collision(Block a, Block b){
        return a.x<b.x+b.width && a.y<b.y+b.height && a.x+a.width>b.x && a.y+a.height>b.y;
    }

    public void resetPositions(){
        pacman.reset();
        for(Block ghost:ghosts){
            ghost.reset();
            char newDirection = directions[rand.nextInt(directions.length)];
            ghost.updateDirection(newDirection);
        }
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {}

    @Override
    public void keyPressed(KeyEvent keyEvent) {}

    @Override
    public void keyReleased(KeyEvent keyEvent) {

        if(gameOver){
            loadMap();
            resetPositions();
            lives=3;
            score=0;
            gameOver=false;
            gameLoop.start();
        }

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
