package deco2800.skyfall.gui;

import deco2800.skyfall.BaseGDXTest;
import org.junit.Before;
import org.junit.BeforeClass;

public class ManaBarTest extends BaseGDXTest {

    private ManaBar manabar;

    @BeforeClass
    public void beforeClass() {
        this.manabar = new ManaBar(100, "mana_bar_inner", "mana_bar");
    }

}