package deco2800.skyfall.gamemenu;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import deco2800.skyfall.managers.GameMenuManager;
import deco2800.skyfall.managers.StatisticsManager;
import deco2800.skyfall.managers.TextureManager;

public class HealthCircle extends AbstractUIElement {
    private final GameMenuManager gmm;
    private float currentHealth;
    private int newHealth; // maybe for animating it down.
    private  ImageButton biggerCircle;
    private  ImageButton smallerCircle;
    private float positionX;
    private float positionY;
    private float offset;
    private Label label;
    StatisticsManager sm;
    Skin skin;

    final int offsetNewGuiX = 175;
    /**
     * Updates the inner circle.
     */
    private void updateInnerCircle() {
        float diff = currentHealth - newHealth;

        if (smallerCircle == null || biggerCircle == null) {
            return;
        }

        smallerCircle.setSize((float) 2 * newHealth,
                (float) 2 * newHealth);
        offset += (diff * 2) / 2;
        smallerCircle.setPosition(positionX + offset, positionY + offset);
        currentHealth = newHealth;

        System.out.println("POSTION " + smallerCircle.getX() + smallerCircle.getY());

        if(sm.getHealth() < 1) {
            label.setText("DEAD");
        } else {
            label.setText("Health: " + sm.getHealth());
        }
    }

    /**
     * Updates the health circle and the position if the screen has been resized
     */
    @Override
    public void update() {
        super.update();
        newHealth = sm.getHealth();
        if ((currentHealth - newHealth) >= 0) {
            updateInnerCircle();
        }
    }
    /**
     * Keeps the object on the top left of the screen
     */
    @Override
    public void updatePosition() {
        positionX = gmm.getTopRightX() - stage.getCamera().viewportWidth / 2;
        positionY = gmm.getTopRightY() - 100;
        smallerCircle.setPosition(positionX + offset, positionY + offset);
        biggerCircle.setPosition(positionX, positionY);
        label.setPosition(positionX + 80, positionY + 30);
        label.toBack();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void draw() {

        label = new Label("Health: 10", skin,  "blue-pill");
        label.setAlignment(Align.center);
        label.setFontScale(0.7f);

        final int OUTER_CIRCLE = 1;
        final int INNER_CIRCLE = 0;

        this.biggerCircle = new ImageButton(textures[OUTER_CIRCLE]);
        biggerCircle.setSize(100, 100);

        this.smallerCircle = new ImageButton(textures[INNER_CIRCLE]);
        smallerCircle.setSize(100, 100);

        smallerCircle.setName("innerHealthCircle");
        biggerCircle.setName("outerHealthCircle");

        stage.addActor(biggerCircle);
        stage.addActor(smallerCircle);
        stage.addActor(label);
    }


    /**
     * Constructors
     * @param stage The game stage
     * @param textureNames The texture names to fetch
     * @param tm The texture manager
     * @param sm The statistics manager
     */
    public HealthCircle(Stage stage, String[] textureNames, TextureManager tm, StatisticsManager sm, Skin skin, GameMenuManager gmm) {
        super(stage, textureNames, tm);
        this.sm = sm;
        this.skin = skin;
        this.gmm = gmm;
        this.draw();
        currentHealth = sm.getHealth();
    }
}
