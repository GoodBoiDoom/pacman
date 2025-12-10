package pacman;
import javax.swing.JFrame;
public class app {
    public static void main(String[] args) {
        int row=22,column=19;
        int tileSize=32;
        int boardWidth=column*tileSize;
        int boardHeight=row*tileSize;

        JFrame frame=new JFrame();
        frame.setTitle("pacman");
        frame.setSize(boardWidth,boardHeight);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Pacman game=new Pacman();
        frame.add(game);
        frame.pack();
        game.requestFocus();
        frame.setVisible(true);
    }
}
