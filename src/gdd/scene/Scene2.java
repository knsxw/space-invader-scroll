package gdd.scene;

import gdd.AudioPlayer;
import gdd.Game;
import gdd.SpawnDetails;

import static gdd.Global.*;
import gdd.sprite.Enemy;
import gdd.sprite.ZigZagAlien;
import gdd.sprite.Explosion;
import gdd.sprite.Player;
import gdd.sprite.Shot;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Scene2 extends JPanel {
    private int frame = 0;
    private List<Enemy> enemies;
    private List<Explosion> explosions;
    private List<Shot> shots;
    private Player player;
    // private Shot shot;

    private int direction = -1;
    private int deaths = 0;

    private boolean inGame = true;
    private String message = "Game Over";

    private final Dimension d = new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    private final Random randomizer = new Random();

    private Timer timer;
    private final Game game;

    private Map<Integer, List<SpawnDetails>> spawnMap = new HashMap<>();
    private int totalEnemiesToSpawn = 0;
    private int[][] activeMap;

    private final int[][] MAP = {
            { 0, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 0 },
            { 0, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 0 },
            { 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0 },
            { 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0 },
            { 0, 0, 1, 1, 0, 0, 0, 0, 1, 1, 0, 0 },
            { 0, 0, 1, 1, 0, 0, 0, 0, 1, 1, 0, 0 },
            { 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0 },
            { 0, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 0 },
            { 0, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 0 },
            { 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0 },
            { 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0 },
            { 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0 },
            { 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0 }
    };

    public Scene2(Game game) {
        this.game = game;
        initBoard();
        // gameInit();
    }

    private void addSpawn(int spawnFrame, int type, int x, int y) {
        spawnMap.computeIfAbsent(spawnFrame, k -> new ArrayList<>()).add(new SpawnDetails(type, x, y));
    }

    private void initBoard() {
        // Spawn Details (frame, type, x, y)
        // Type 1: Standard Enemy, Type 2: ZigZagAlien

        // Wave 1: Frame 50
        addSpawn(50, 1, 150, 50);
        addSpawn(50, 1, 300, 50);
        addSpawn(50, 1, 450, 50);

        // Wave 2: Frame 120
        addSpawn(120, 2, 100, 30);
        addSpawn(120, 2, 500, 30);

        // Wave 3: Frame 220
        addSpawn(220, 1, 100, 60);
        addSpawn(220, 1, 250, 60);
        addSpawn(220, 1, 400, 60);
        addSpawn(220, 1, 550, 60);

        // Wave 4: Frame 320
        addSpawn(320, 2, 200, 40);
        addSpawn(320, 2, 400, 40);

        // Wave 5: Frame 450
        addSpawn(450, 1, 150, 70);
        addSpawn(450, 1, 300, 70);
        addSpawn(450, 1, 450, 70);
        addSpawn(450, 1, 600, 70);

        // Wave 6: Frame 580
        addSpawn(580, 2, 100, 50);
        addSpawn(580, 2, 300, 50);
        addSpawn(580, 2, 500, 50);

        // Wave 7: Frame 700 (Final Wave)
        addSpawn(700, 1, 100, 80);
        addSpawn(700, 1, 200, 80);
        addSpawn(700, 1, 300, 80);
        addSpawn(700, 1, 400, 80);
        addSpawn(700, 1, 500, 80);

        // Compute total enemies to spawn dynamically
        totalEnemiesToSpawn = 0;
        for (var list : spawnMap.values()) {
            totalEnemiesToSpawn += list.size();
        }
    }

    private void gameInit() {
        // Initialize mutable active map from static MAP template
        activeMap = new int[MAP.length][MAP[0].length];
        for (int i = 0; i < MAP.length; i++) {
            System.arraycopy(MAP[i], 0, activeMap[i], 0, MAP[i].length);
        }

        enemies = new ArrayList<>();
        explosions = new ArrayList<>();
        shots = new ArrayList<>();

        player = new Player();
    }

    public void start() {
        addKeyListener(new TAdapter());
        setFocusable(true);
        requestFocusInWindow();
        setBackground(Color.black);

        timer = new Timer(1000 / 60, new GameCycle());
        timer.start();

        gameInit();
    }

    public void stop() {
        timer.stop();
    }

    private void drawAliens(Graphics g) {

        for (Enemy enemy : enemies) {

            if (enemy.isVisible()) {

                g.drawImage(enemy.getImage(), enemy.getX(), enemy.getY(), this);
            }

            if (enemy.isDying()) {

                enemy.die();
            }
        }
    }

    private void drawPlayer(Graphics g) {

        if (player.isVisible()) {

            g.drawImage(player.getImage(), player.getX(), player.getY(), this);
        }

        if (player.isDying()) {

            player.die();
            inGame = false;
        }
    }

    private void drawShot(Graphics g) {

        for (Shot shot : shots) {

            if (shot.isVisible()) {
                g.drawImage(shot.getImage(), shot.getX(), shot.getY(), this);
            }
        }
    }

    private void drawBombing(Graphics g) {

        for (Enemy e : enemies) {

            Enemy.Bomb b = e.getBomb();

            if (!b.isDestroyed()) {

                g.drawImage(b.getImage(), b.getX(), b.getY(), this);
            }
        }
    }

    private void drawExplosions(Graphics g) {

        List<Explosion> toRemove = new ArrayList<>();

        for (Explosion explosion : explosions) {

            if (explosion.isVisible()) {
                g.drawImage(explosion.getImage(), explosion.getX(), explosion.getY(), this);
                explosion.visibleCountDown();
                if (!explosion.isVisible()) {
                    toRemove.add(explosion);
                }
            }
        }

        explosions.removeAll(toRemove);
    }



    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        doDrawing(g);
    }

    private void doDrawing(Graphics g) {

        g.setColor(Color.black);
        g.fillRect(0, 0, d.width, d.height);

        g.setColor(Color.white);
        g.drawString("Frame: " + frame, 10, 10);

        g.setColor(Color.green);

        if (inGame) {

            g.drawLine(0, GROUND,
                    BOARD_WIDTH, GROUND);

            drawExplosions(g);
            drawAliens(g);
            drawPlayer(g);
            drawShot(g);
            drawBombing(g);

        } else {

            if (timer.isRunning()) {
                timer.stop();
            }

            gameOver(g);
        }

        Toolkit.getDefaultToolkit().sync();
    }

    private void gameOver(Graphics g) {

        g.setColor(Color.black);
        g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);

        g.setColor(new Color(0, 32, 48));
        g.fillRect(50, BOARD_WIDTH / 2 - 30, BOARD_WIDTH - 100, 50);
        g.setColor(Color.white);
        g.drawRect(50, BOARD_WIDTH / 2 - 30, BOARD_WIDTH - 100, 50);

        var small = new Font("Helvetica", Font.BOLD, 14);
        var fontMetrics = this.getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(message, (BOARD_WIDTH - fontMetrics.stringWidth(message)) / 2,
                BOARD_WIDTH / 2);
    }

    private void update() {

        // Check for spawn events
        if (spawnMap.containsKey(frame)) {
            List<SpawnDetails> list = spawnMap.get(frame);
            for (SpawnDetails spawnDetails : list) {
                Enemy enemy;
                if (spawnDetails.type == 2) {
                    enemy = new ZigZagAlien(spawnDetails.x, spawnDetails.y);
                } else {
                    enemy = new Enemy(spawnDetails.x, spawnDetails.y);
                }
                enemies.add(enemy);
            }
        }

        List<Shot> shotsToRemove = new ArrayList<>();

        if (totalEnemiesToSpawn > 0 && deaths == totalEnemiesToSpawn) {
            inGame = false;
            timer.stop();
            message = "Game won!";
        }

        // player
        player.act();

        // shot
        for (Shot shot : shots) {

            if (shot.isVisible()) {
                int shotX = shot.getX();
                int shotY = shot.getY();

                for (Enemy enemy : enemies) {
                    // Collision detection: shot and enemy
                    int enemyX = enemy.getX();
                    int enemyY = enemy.getY();

                    if (enemy.isVisible() && shot.isVisible()
                            && shotX >= (enemyX)
                            && shotX <= (enemyX + ALIEN_WIDTH)
                            && shotY >= (enemyY)
                            && shotY <= (enemyY + ALIEN_HEIGHT)) {

                        var ii = new ImageIcon(IMG_EXPLOSION);
                        enemy.setImage(ii.getImage());
                        enemy.setDying(true);
                        explosions.add(new Explosion(enemyX, enemyY));
                        deaths++;
                        shot.die();
                        shotsToRemove.add(shot);
                        AudioPlayer.playSound("src/audio/explode.wav");
                    }
                }

                int y = shot.getY();
                y -= 20;

                if (y < 0) {
                    shot.die();
                    shotsToRemove.add(shot);
                } else {
                    shot.setY(y);
                }
            }
        }
        shots.removeAll(shotsToRemove);

        // enemies direction updates
        for (Enemy enemy : enemies) {

            // Only update direction based on standard Enemy, since ZigZagAlien moves independently
            if (enemy.getClass() == Enemy.class && enemy.isVisible()) {
                int x = enemy.getX();

                if (x >= BOARD_WIDTH - BORDER_RIGHT && direction != -1) {

                    direction = -1;

                    for (Enemy e2 : enemies) {
                        e2.setY(e2.getY() + GO_DOWN);
                    }
                    break;
                }

                if (x <= BORDER_LEFT && direction != 1) {

                    direction = 1;

                    for (Enemy e : enemies) {
                        e.setY(e.getY() + GO_DOWN);
                    }
                    break;
                }
            }
        }

        for (Enemy enemy : enemies) {
            if (enemy.isVisible()) {

                int y = enemy.getY();

                if (y > GROUND - ALIEN_HEIGHT) {
                    inGame = false;
                    message = "Invasion!";
                }

                enemy.act(direction);
            }
        }

        // bombs
        for (Enemy enemy : enemies) {

            int chance = randomizer.nextInt(120); // Much less frequent bombs (from 15 to 120)
            Enemy.Bomb bomb = enemy.getBomb();

            if (chance == CHANCE && enemy.isVisible() && bomb.isDestroyed()) {

                bomb.setDestroyed(false);
                bomb.setX(enemy.getX());
                bomb.setY(enemy.getY());
            }

            int bombX = bomb.getX();
            int bombY = bomb.getY();
            int playerX = player.getX();
            int playerY = player.getY();

            // Bomb and player collisions
            if (player.isVisible() && !bomb.isDestroyed()
                    && bombX >= (playerX)
                    && bombX <= (playerX + PLAYER_WIDTH)
                    && bombY >= (playerY)
                    && bombY <= (playerY + PLAYER_HEIGHT)) {

                var ii = new ImageIcon(IMG_EXPLOSION);
                player.setImage(ii.getImage());
                player.setDying(true);
                bomb.setDestroyed(true);
                AudioPlayer.playSound("src/audio/explode.wav");
            }

            if (!bomb.isDestroyed()) {
                bomb.setY(bomb.getY() + 1);
                if (bomb.getY() >= GROUND - BOMB_HEIGHT) {
                    bomb.setDestroyed(true);
                }
            }
        }
        frame++;
    }

    private void doGameCycle() {
        update();
        repaint();
    }

    private class GameCycle implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            doGameCycle();
        }
    }

    private class TAdapter extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent e) {
            player.keyReleased(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            System.out.println("Scene2.keyPressed: " + e.getKeyCode());

            player.keyPressed(e);

            int x = player.getX();
            int y = player.getY();

            int key = e.getKeyCode();

            if (key == KeyEvent.VK_SPACE && inGame) {
                System.out.println("Shots: " + shots.size());
                if (shots.size() < 4) {
                    // Create a new shot and add it to the list
                    Shot shot = new Shot(x, y);
                    shots.add(shot);
                    AudioPlayer.playSound("src/audio/shot.wav");
                }
            }

        }
    }
}
