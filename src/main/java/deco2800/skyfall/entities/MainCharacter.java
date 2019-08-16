package deco2800.skyfall.entities;

import com.badlogic.gdx.*;
import com.badlogic.gdx.math.Vector2;
import deco2800.skyfall.Tickable;
import deco2800.skyfall.managers.*;
import deco2800.skyfall.observers.*;
import deco2800.skyfall.tasks.*;
import deco2800.skyfall.util.*;

import java.util.*;

/**
 * Main character in the game
 */
public class MainCharacter extends Peon implements KeyDownObserver, KeyUpObserver, Tickable {

    // List of the player's inventories
    // TODO need to replace List<String> with List<InventoryClass>
    private List<String> inventories;

    // Hotbar of inventories
    private List<String> hotbar;

    // The index of the item selected to be used in the hotbar
    // ie. [sword][gun][apple]
    // if selecting sword then equipped_item = 0,
    // if selecting gun the equipped_item = 1
    private int equipped_item;
    private final int INVENTORY_MAX_CAPACITY = 20;


    /*
    Potential future implementations

    // This is equipped items like rings, armour etc.
    private List<InventoryItem> misc;

    // These are status effects (ie. poison, regen, weakness)
    private List<StatusEffect> statusEffects;

    // These are player attributes ie. combat strength
    private List<Attributes> attributes;
    */

    // Level/point system for the Main Character to be recorded as game goes on
    private int level;

    // Textures for all 6 directions to correspond to movement of character
    private String[] textures;

    protected Vector2 direction;
    protected float currentSpeed;
    private boolean MOVE_UP = false;
    private boolean MOVE_LEFT = false;
    private boolean MOVE_RIGHT = false;
    private boolean MOVE_DOWN = false;


    /**
     * Private helper method to instantiate inventory for Main Character
     * constructor
     */
    private void instantiateInventory() {
        this.inventories = new ArrayList<>();
        this.hotbar = new ArrayList<>();
        this.hotbar.add("Rusty Sword");
        this.equipped_item = 0;
    }

    /**
     * Basic Main Character constructor
     */
    public MainCharacter(float col, float row, float speed, String name,
                         int health) {
        super(row, col, speed, name, health);
        //TODO: Change this to properly.
        this.setTexture("__ANIMATION_mario_right:1");
        this.setHeight(1);
        this.setObjectName("MainPiece");

        GameManager.getManagerFromInstance(InputManager.class)
                .addKeyDownListener(this);
        GameManager.getManagerFromInstance(InputManager.class)
                .addKeyUpListener(this);
        this.direction = new Vector2(row, col);
        this.direction.limit2(0.05f);

        this.level = 1;
        this.instantiateInventory();

        //TODO: Remove this.
        this.configure_animations();
    }

    /*Tester*/
    //TODO: change this to actual animations.
    private void configure_animations() {
        animations.put(AnimationRole.MOVE_NORTH, "mario_right");
        animations.put(AnimationRole.MOVE_NORTH_EAST, "mario_right");
        animations.put(AnimationRole.MOVE_NORTH_WEST, "mario_left");
        animations.put(AnimationRole.MOVE_SOUTH, "mario_left");
        animations.put(AnimationRole.MOVE_SOUTH_EAST, "mario_right");
        animations.put(AnimationRole.MOVE_SOUTH_WEST, "mario_left");
        animations.put(AnimationRole.MOVE_WEST, "mario_left");
        animations.put(AnimationRole.MOVE_EAST, "mario_right");

    }

    /**
     * Constructor with various textures
     * @param textures A array of length 6 with string names corresponding to
     *                different orientation
     *                 0 = North
     *                 1 = North-East
     *                 2 = South-East
     *                 3 = South
     *                 4 = South-West
     *                 5 = North-West
     */
    public MainCharacter(float col, float row, float speed, String name,
                         int health, String[] textures) {
        this(row, col, speed, name, health);

        this.textures = textures;
        this.setTexture(textures[2]);
    }

    /**
     * Adds item to player's collection
     * @param item inventory being added
     */
    public void pickUpInventory(String item) {
        inventories.add(item);
    }

    /**
     * Removes items from player's collection
     * @param item inventory being removed
     */
    public void dropInventory(String item) {
        inventories.remove(item);
    }

    /**
     * Gets the player's inventories, modification of the returned list
     * doesn't impact the internal class
     * @return a list of the player's inventories
     */
    public List<String> getInventories() {
        return new ArrayList<>(inventories);
    }

    /**
     * Equips an item from the inventory list
     * Max no of equipped items is 5
     * @param item inventory being equipped
     */
    public void equipItem(String item, int index) {
        if (hotbar.size() == 5) {
            String item_to_replace = hotbar.get(index);
            hotbar.set(index, item);
            inventories.add(item_to_replace);
        }
    }

    /**
     * Unequips an item
     * @param item inventory being unequipped
     */
    public void unequipItem(String item) {
        if(inventories.size() >= INVENTORY_MAX_CAPACITY) {
            hotbar.remove(item);
            inventories.add(item);
        }
    }

    /**
     * Gets the player's currently equipped inventory, modification of the
     * returned list doesn't impact the internal class
     * @return a list of the player's equipped inventories
     */
    public List<String> getHotbar() {
        return new ArrayList<>(hotbar);
    }

    /**
     * Gets the item currently equipped
     * @return the item currently equipped
     */
    public String getEquippedItem() {
        return this.hotbar.get(equipped_item);
    }

    /**
     * Change current level of character
     * @param change amount being added or subtracted
     */
    public void changeLevel(int change) {
        if (level + change >= 1) {
            this.level += change;
        }
    }

    /**
     * Gets the current level of character
     * @return level of character
     */
    public int getLevel() {
        return this.level;
    }

    /**
     * Get the inventory of the player
     * @return the inventory of the player
     */
    public ArrayList<String> getInventory(){
        return new ArrayList<>(inventories);
    }
    /*
    Potential more methods and related attributes:
    -record killed enemies
    -interaction with worlds
    -interaction with movement
    */

    /**
     * Handles tick based stuff, e.g. movement
     */
    private void updateMoveVector() {
        if (MOVE_UP){this.direction.add(0.0f, speed);}
        if (MOVE_LEFT){this.direction.sub(speed, 0.0f);}
        if (MOVE_DOWN){this.direction.sub(0.0f, speed);}
        if (MOVE_RIGHT){this.direction.add(speed, 0.0f);}
    }

    public void notifyTouchDown(int screenX, int screenY, int pointer, int button) {
        // only allow left clicks to move player
        if (button != 0) {
            // Right click run animation.
            //TODO: remove this.
            //System.out.println("MainCharacter Added to Queue");
            //toBeRun.add(new AnimationLinker(AnimationRole.COMBAT, "mario_right", this.getName(), new int[]{10, 10}));
            //System.out.println("Right Click run Animation");
            return;
        }
    }


    /**
     *
     * @param i
     */
    @Override
    public void onTick(long i){
        updateMoveVector();
        this.setCurrentSpeed(this.direction.len());
        this.moveTowards(new HexVector(this.direction.x, this.direction.y));
        //System.out.printf("(%s : %s) diff: (%s, %s)%n", this.direction, this.getPosition(), this.direction.x - this.getCol(), this.direction.y - this.getRow());
        //System.out.printf("%s%n", this.currentSpeed);
        //TODO: Check direction for animation here
    }

    @Override
    public void moveTowards(HexVector destination) {
        position.moveToward(destination, this.currentSpeed);
    }

    /**
     * Sets the Player's current movement speed.
     *
     * @param cSpeed the speed for the player to currently move at.
     */
    private void setCurrentSpeed(float cSpeed){
        this.currentSpeed = cSpeed;
    }

    /**
     * Sets the appropriate movement flags to true on keyDown
     *
     * @param keycode the key being pressed
     */
    @Override
    public void notifyKeyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.W:
                MOVE_UP = true;
                movingAnimation = AnimationRole.MOVE_NORTH;
                break;
            case Input.Keys.A:
                MOVE_LEFT = true;
                movingAnimation = AnimationRole.MOVE_WEST;
                break;
            case Input.Keys.S:
                MOVE_DOWN = true;
                movingAnimation = AnimationRole.MOVE_SOUTH;
                break;
            case Input.Keys.D:
                MOVE_RIGHT = true;
                movingAnimation = AnimationRole.MOVE_EAST;
                break;
        }
    }

    /**
     * Sets the appropriate movement flags to false on keyUp
     *
     * @param keycode the key being released
     */
    @Override
    public void notifyKeyUp(int keycode) {
        movingAnimation = AnimationRole.NULL;
        switch(keycode){
            case Input.Keys.W:
                MOVE_UP = false;
                break;
            case Input.Keys.A:
                MOVE_LEFT = false;
                this.setTexture("__ANIMATION_mario_left:1");
                break;
            case Input.Keys.S:
                MOVE_DOWN = false;
                break;
            case Input.Keys.D:
                MOVE_RIGHT = false;
                this.setTexture("__ANIMATION_mario_right:1");
                break;
        }
    }
}


