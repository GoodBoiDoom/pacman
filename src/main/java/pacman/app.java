package pacman;
import javax.swing.JFrame;
public class app {
    public static void main(String[] args) {
        int row=21,column=19;
        int tileSize=32;
        int boardWidth=column*tileSize;
        int boardHeight=row*tileSize;

        JFrame frame=new JFrame();
        frame.setTitle("pacman");
        frame.setSize(boardWidth,boardHeight);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        theGoodBoi game=new theGoodBoi();
        frame.add(game);
        frame.pack();
        game.requestFocus();
        frame.setVisible(true);
    }
}
