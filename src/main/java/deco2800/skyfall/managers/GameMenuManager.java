package deco2800.skyfall.managers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import deco2800.skyfall.GameScreen;
import deco2800.skyfall.entities.MainCharacter;
import deco2800.skyfall.gamemenu.GameMenuScreen;
import deco2800.skyfall.gui.HealthCircle;
import deco2800.skyfall.gui.SettingsTable;
import deco2800.skyfall.gamemenu.PopUpTable;


/**
 * Managers the menu bar during the game
 */
public class GameMenuManager extends TickableManager {



    private static TextureManager textureManager;
    private Stage stage;
    private MainCharacter mainCharacter;
    private HealthCircle healthCircle;
    private InventoryManager inventory;
    private SoundManager soundManager;
    private Skin skin;
    private String[] characters;

    public static final int NUMBEROFCHARACTERS = 5;

    public GameMenuManager() {
        textureManager = GameManager.get().getManager(TextureManager.class);
        inventory = GameManager.get().getManager(InventoryManager.class);
        soundManager = GameManager.get().getManager(SoundManager.class);
        stage = null;
        skin = null;
        characters = new String[NUMBEROFCHARACTERS];
        // testing
        characters[0] = "main_piece";
        characters[1] = "bowman";
        characters[2] = "robot";
        characters[3] = "spider";
        characters[4] = "spacman_ded";
        GameMenuScreen.currentCharacter = 0;
    }

    @Override
    public void onTick(long i) {
        //Get the current state of the inventory on tick so that display can be updated
        inventory = GameManager.get().getManager(InventoryManager.class);

        if (healthCircle != null) {
            healthCircle.update();
        }



    }

    public InventoryManager getInventory() {
        return inventory;
    }

    public static TextureManager getTextureManager() {
        return textureManager;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Stage getStage() {
        return stage;
    }

    public void setSkin(Skin skin) {
        this.skin = skin;
    }

    public Skin getSkin() {
        return skin;
    }


    private void pause() {
        GameManager.setPaused(true);
    }

    public void resume(PopUpTable table) {
        GameManager.setPaused(false);
        GameScreen.isPaused = false;
        exit(table);
    }

    private void exit(PopUpTable table) {
        table.setVisible(false);
        table.getExit().setVisible(false);
        PopUpTable.setOpened(null);
        System.out.println("exited " + table.name);
        BGMManager.unmute(); // Un-mute the BGM when menu is closed
    }

    public void open(PopUpTable table) {
        if (PopUpTable.getOpened() != null) {
            System.out.println("Should be exited: " + PopUpTable.getOpened().name);
            exit(PopUpTable.getOpened());
        }
        table.setVisible(true);
        table.getExit().setVisible(true);
        GameScreen.isPaused = true;
        pause();
        PopUpTable.setOpened(table);
        System.out.println("opened " + table.name);
        BGMManager.mute(); // Mute the BGM when menu is opened
    }


    public static TextureRegionDrawable generateTextureRegionDrawableObject(String sName) {
        return new TextureRegionDrawable((new TextureRegion(textureManager.getTexture(sName))));
    }

    public void setMainCharacter(MainCharacter mainCharacter) {
        if (stage == null) {
            System.out.println("Please set stage before adding character");
            return;
        }
        this.mainCharacter = mainCharacter;

}

    public MainCharacter getMainCharacter() {
        return mainCharacter;
    }

    /**
     * Adds the circle to menu Screen
     * @param hc
     */
    public void addHealthCircle(HealthCircle hc) {
        this.healthCircle = hc;
    }

    public String[] getCharacters() {
        return characters;
    }
}


