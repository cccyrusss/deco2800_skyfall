package deco2800.skyfall.entities;

import com.badlogic.gdx.*;
import com.badlogic.gdx.math.*;
import deco2800.skyfall.Tickable;
import deco2800.skyfall.managers.*;
import deco2800.skyfall.observers.*;
import deco2800.skyfall.util.HexVector;

public class PlayerPeon extends Peon implements KeyDownObserver,
        KeyUpObserver, Tickable {

    protected Vector2 direction;
    protected float currentSpeed;

    private boolean moveUp = false;
    private boolean moveLeft = false;
    private boolean moveRight = false;
    private boolean moveDown = false;

    /**
     * PlayerPeon Constructor
     * @param row the row position in the world
     * @param col the column position in the world
     * @param speed the player's speed
     */
    public PlayerPeon(float row, float col, float speed, String name,
                      int health) {
        super(row, col, speed, name, health);
        this.direction = new Vector2(row, col);
        this.direction.limit2(0.05f);
    }

    /**
     * The basic test hitbox created when a player clicks the mouse.
     * If an enemy is in this hitbox, they will be damaged once.
     */
    public PlayerPeon(float row, float col, float speed) {
        this.setObjectName("playerPeon");
        GameManager.getManagerFromInstance(InputManager.class)
                .addKeyDownListener(this);
        GameManager.getManagerFromInstance(InputManager.class)
                .addKeyUpListener(this);
    }

    /**
     * Calculates the new movement point depending on what movement keys are held down.
     */
    private void updateMoveVector() {
        if (moveUp) {
            this.direction.add(0.0f, speed);
        }
        if (moveLeft) {
            this.direction.sub(speed, 0.0f);
        }
        if (moveDown) {
            this.direction.sub(0.0f, speed);
        }
        if (moveRight) {
            this.direction.add(speed, 0.0f);
        }
    }

    /**
     * @param i
     */
    @Override
    public void onTick(long i) {
        if (task != null && task.isAlive()) {
            task.onTick(i);

            if (task.isComplete()) {
                this.task = null;
            }
        }

//        if (Gdx.input.isKeyJustPressed(Input.Keys.B)) {
//            GameManager.getManagerFromInstance(ConstructionManager.class).displayWindow();
//        }
    }

    /**
     * Attack with the weapon the character has equip.
     */
    public void attack() {

        //Spawn projectile in front of character for now.


        //Get AbstractWorld from static class GameManager.

        //Add the projectile entity to the game world.

    }

    /**
     * Perform a special attack with the right click.
     */
    public void specialAttack() {
        //release a more powerful attack.
    }

    @Override
    public void moveTowards(HexVector destination){
        position.moveToward(destination, this.currentSpeed);
    }

    public void notifyTouchDown ( int screenX, int screenY, int pointer,
                                  int button) {

        // cant move when click game menu
        if (screenX > 185 && screenX < 1095 && screenY > 615) {
            return;
        }

        if (button == 1) {
            this.attack();
        }
    }



    /**
     * Sets the appropriate movement flags to true on keyDown
     * @param keycode the key being pressed
     */
    @Override
    public void notifyKeyDown ( int keycode){
        // if game is paused, no sound
        if (GameManager.getPaused()) {
            return;
        }

        switch (keycode) {
            case Input.Keys.W:
                moveUp = true;
                break;
            case Input.Keys.A:
                moveLeft = true;
                break;
            case Input.Keys.S:
                moveDown = true;
                break;
            case Input.Keys.D:
                moveRight = true;
                break;
            default:
                break;
        }
    }

    /**
     * Sets the appropriate movement flags to false on keyUp
     * @param keycode the key being released
     */
    @Override
    public void notifyKeyUp ( int keycode){
        switch (keycode) {
            case Input.Keys.W:
                moveUp = false;
                break;
            case Input.Keys.A:
                moveLeft = false;
                break;
            case Input.Keys.S:
                moveDown = false;
                break;
            case Input.Keys.D:
                moveRight = false;
                break;
            default:
                break;
        }
    }
}
