package deco2800.skyfall.gamemenu.popupmenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import deco2800.skyfall.gamemenu.AbstractPopUpElement;
import deco2800.skyfall.managers.GameMenuManager;
import deco2800.skyfall.managers.StatisticsManager;
import deco2800.skyfall.managers.TextureManager;
import java.util.Map;


public class GoldTable extends AbstractPopUpElement{
    private final Skin skin;
    private final StatisticsManager sm;
    private Table goldPanel;

    public GoldTable(Stage stage, ImageButton exit, String[] textureNames,
                      TextureManager tm, GameMenuManager gameMenuManager,
                      StatisticsManager sm, Skin skin) {
        super(stage,exit, textureNames, tm, gameMenuManager);
        this.skin = skin;
        this.sm = sm;
        this.draw();
    }


    @Override
    public void update() {
        super.update();
        updateGoldPanel();
    }

    @Override
    public void draw() {
        super.draw();

        baseTable = new Table();
        baseTable.setSize(700,700);
        baseTable.setPosition(Gdx.graphics.getWidth()/2f - baseTable.getWidth()/2,
                (Gdx.graphics.getHeight() + 160) / 2f - baseTable.getHeight()/2);
        baseTable.top();
        baseTable.setBackground(gameMenuManager.generateTextureRegionDrawableObject("pop up screen"));
        baseTable.setName("baseTable");

        super.goldAndConstructionTableDuplicatedFunctionality("goldBanner");
        updateGoldPanel();

        baseTable.addActor(this.goldPanel);
        baseTable.setVisible(false);

        stage.addActor(baseTable);
    }

    /***
     * Updates the gold panel to display the current value of each coin.
     */
    private void updateGoldPanel(){
        if (goldPanel != null) {
            goldPanel.clear();
        } else {
            goldPanel = new Table();
        }
        goldPanel.setName("goldPanel");
        goldPanel.setSize(500, 450);
        goldPanel.setPosition(110, 100);
        goldPanel.setBackground(gameMenuManager.generateTextureRegionDrawableObject("menu_panel"));

        Map<Integer, Integer> goldAmounts = sm.getCharacter().getGoldPouch();

        float count = 0;
        float xpos = 20;
        float ypos = 280;

        for (Map.Entry<Integer, Integer> entry : goldAmounts.entrySet()) {
            ImageButton icon = new ImageButton(gameMenuManager.generateTextureRegionDrawableObject("goldPiece" + entry.getKey()));
            icon.setName("icon");
            icon.setSize(100, 100);
            icon.setPosition(xpos + count * 130, ypos);

            goldPanel.addActor(icon);

            Label num = new Label(entry.getValue().toString(), skin, "white-label");
            num.setPosition(xpos + 85 + count * 130, ypos + 75);
            goldPanel.addActor(num);

            count++;

            if ((count) % 3 == 0) {
                ypos -= 120;
                count = 0;
            }
        }

    }

}
