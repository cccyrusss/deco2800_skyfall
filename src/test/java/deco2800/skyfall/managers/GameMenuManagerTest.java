package deco2800.skyfall.managers;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import deco2800.skyfall.gamemenu.AbstractPopUpElement;
import deco2800.skyfall.gamemenu.AbstractUIElement;
import deco2800.skyfall.gamemenu.HealthCircle;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import static org.mockito.Mockito.*;


public class GameMenuManagerTest {
    private GameMenuManager gmm;
    private TextureManager tm;
    private SoundManager sm;
    private InventoryManager im;
    private Stage stage;
    private Skin skin;
    private Map<String, AbstractPopUpElement> popUps;
    private Map<String, AbstractUIElement> uiElements;


    @Before
    public void setUp() {
        tm = mock(TextureManager.class);
        sm = mock(SoundManager.class);
        im = mock(InventoryManager.class);
        stage = mock(Stage.class);
        skin = mock(Skin.class);
        popUps = mock(Map.class);
        uiElements = mock(Map.class);

        gmm = new GameMenuManager(tm, sm, im, stage, skin, popUps, uiElements);
    }

    @Test
    public void onTickPopUpTest() {
        AbstractPopUpElement mockPopUp = mock(AbstractPopUpElement.class);
        gmm.setPopUp("mockPopUp");
        when(popUps.get("mockPopUp")).thenReturn(mockPopUp);
        when(mockPopUp.isVisible()).thenReturn(false);
        doNothing().when(mockPopUp).update();
        doNothing().when(mockPopUp).show();



        gmm.onTick(0);
        verify(mockPopUp).update();
        verify(mockPopUp).show();
    }

    @Test
    public void onTickUpdateTest() {
        gmm.setPopUp(null);
        //AbstractPopUpElement is still
        AbstractUIElement mockPopUp = mock(AbstractPopUpElement.class);

        doNothing().when(mockPopUp).update();


        HashMap<String, Object> actualMap = new HashMap<>();

        actualMap.put("mock1", 2);
        actualMap.put("mock2", 2);
        actualMap.put("mock3", 2);

        when(uiElements.keySet()).thenReturn(actualMap.keySet());
        doReturn(mockPopUp).when(uiElements).get(anyString());

        gmm.onTick(0);
        verify(mockPopUp, times(3)).update();

    }


    @Test
    public void getCurrentPopUpTest() {
        when(popUps.get("goldTable")).thenReturn(null);
        gmm.setPopUp("goldTable");

        gmm.getCurrentPopUp();
        verify(popUps).get("goldTable");
    }

    @Test
    public void getPopUpFromTable() {
        when(popUps.get("chestTable")).thenReturn(null);
        gmm.getPopUp("chestTable");
        verify(popUps).get("chestTable");
    }

    @Test
    public void drawAllElementsWithoutStatsManager() {
        gmm.drawAllElements();
        verify(popUps, never()).put(anyString(), any());
        verify(uiElements, never()).put(anyString(), any());
    }


    @After()
    public void tearDown() {
        tm = null;
        sm = null;
        im = null;
        stage = null;
        skin = null;
        popUps = null;
        uiElements = null;
        gmm = null;
    }
}