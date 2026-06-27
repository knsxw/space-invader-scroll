package gdd.sprite;

import static gdd.Global.*;

public class ZigZagAlien extends Enemy {

    private int zigDirection = 1;
    private int stepCount = 0;
    private static final int STEPS_TO_CHANGE = 20;

    public ZigZagAlien(int x, int y) {
        super(x, y);
    }

    @Override
    public void act(int direction) {
        // Zig-zag motion: moves horizontally back and forth and moves down slowly
        this.x += 3 * zigDirection;
        this.y += 1;

        // Keep alien within horizontal borders
        if (this.x <= BORDER_LEFT) {
            this.x = BORDER_LEFT;
            zigDirection = 1;
        } else if (this.x >= BOARD_WIDTH - BORDER_RIGHT) {
            this.x = BOARD_WIDTH - BORDER_RIGHT;
            zigDirection = -1;
        }

        stepCount++;
        if (stepCount >= STEPS_TO_CHANGE) {
            zigDirection = -zigDirection;
            stepCount = 0;
        }
    }
}
