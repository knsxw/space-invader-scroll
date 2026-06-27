package gdd;

import gdd.scene.Scene2;
import gdd.scene.Title;
import javax.swing.JFrame;

public class Game extends JFrame {

    Title title;
    Scene2 scene2;

    public Game() {
        title = new Title(this);
        scene2 = new Scene2(this);
        initUI();
    }

    private void initUI() {

        setTitle("Space Invaders");
        setSize(Global.BOARD_WIDTH, Global.BOARD_HEIGHT);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);

        loadTitle();
    }

    public void loadTitle() {
        getContentPane().removeAll();
        // add(new Title(this));
        add(title);
        title.start();
        revalidate();
        repaint();
    }

    public void loadScene1() {
        getContentPane().removeAll();
        // add(new Scene2(this));
        add(scene2);
        title.stop();
        scene2.start();
        revalidate();
        repaint();
    }
}