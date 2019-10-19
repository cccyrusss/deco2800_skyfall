package deco2800.skyfall.gamemenu;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import deco2800.skyfall.managers.GameManager;
import deco2800.skyfall.managers.GameMenuManager;
import deco2800.skyfall.managers.TextureManager;

public class FeedbackBar extends AbstractUIElement {

    //Game menu manager
    private final GameMenuManager gmm;

    //Current skin
    private final Skin skin;

    //Feedback text
    private Label feedback;

    //Feedback bar
    private Table feedbackBarTable;

    public FeedbackBar(Stage stage, String[] textureNames, TextureManager tm, Skin skin, GameMenuManager gmm) {
        super(stage, textureNames, tm);
        this.gmm = gmm;
        this.skin = skin;
        this.draw();
    }

    @Override
    public void updatePosition() {
        feedbackBarTable.setPosition(gmm.getTopLeftX() + stage.getCamera().viewportWidth / 5, gmm.getBottomRightY());
    }

    @Override
    public void draw() {
        feedbackBarTable = new Table();
        feedbackBarTable.setBackground(gmm.generateTextureRegionDrawableObject("feedback_bar"));
        feedbackBarTable.setSize(800, 55);

        feedback = new Label("Click 'HELP' if you get stuck", skin, "white-text");
        feedback.setFontScale(0.8f);
        feedbackBarTable.add(feedback);
        stage.addActor(feedbackBarTable);
    }

    public void updateText(int i) {
        String text;
        switch (i) {
            case 1 :
                text = "Item added to inventory";
                break;
            case 2 :
                text = "Inventory full";
                break;
            default :
                text = "";
                break;
        }
        feedback.setText(text);
        showFeedbackBar();
        gmm.setFeedbackBarUpdate(0);
    }

    public void showFeedbackBar() {
        feedbackBarTable.setVisible(true);
    }

    public void hideFeedbackBar() {
        feedbackBarTable.setVisible(false);
    }

    @Override
    public void update() {
        super.update();
        int i = gmm.getFeedbackBarUpdate();
        if (i != 0) {
            updateText(i);
        }
    }
}
