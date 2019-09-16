package deco2800.skyfall.entities.weapons;

import deco2800.skyfall.entities.MainCharacter;
import deco2800.skyfall.managers.InventoryManager;
import deco2800.skyfall.worlds.Tile;
import org.junit.*;

public class WeaponInventoryIntegrationTest {

    private MainCharacter mc;
    private InventoryManager inventory;
    private Weapon sword;
    private Weapon spear;
    private Weapon bow;
    private Weapon axe;

    /**
     * Setup function to initialise variables before each test
     */
    @Before
    public void setup() {
        mc = MainCharacter.getInstance(0f, 0f, 0.05f, "Main Piece", 10);
        inventory = new InventoryManager();
        sword = new Sword(new Tile(null, 0, 0), false);
        spear = new Spear(new Tile(null, 0, 0), false);
        bow = new Bow(new Tile(null, 0, 0), false);
        axe = new Axe(new Tile(null, 0, 0), false);
    }

    /**
     * Tear down function to set everything to null after each test
     */
    @After
    public void tearDown() {
        mc = null;
        inventory = null;
        sword = null;
        spear = null;
        bow = null;
        axe = null;
    }

    /**
     * Tests pickup functionality and integration with inventory UI
     */
    @Test
    public void pickupTest() {
        // inventory starts with 6 items

        inventory.add(axe);
        Assert.assertEquals(1, inventory.getAmount(axe.getName()));
        Assert.assertEquals(7, inventory.getTotalAmount());

        inventory.add(axe);
        Assert.assertEquals(2, inventory.getAmount("axe"));
        Assert.assertEquals(8, inventory.getTotalAmount());

        inventory.add(sword);
        Assert.assertEquals(1, inventory.getAmount("sword"));
        Assert.assertEquals(2, inventory.getAmount("axe"));
        Assert.assertEquals(9, inventory.getTotalAmount());
    }

    /**
     * Tests drop functionality and integration with inventory UI
     */
    @Test
    public void dropTest() {
        inventory.add(axe);
        inventory.add(axe);
        inventory.add(sword);

        inventory.drop("sword");
        Assert.assertEquals(2, inventory.getAmount("axe"));
        Assert.assertEquals(0, inventory.getAmount("sword"));
        Assert.assertEquals(8, inventory.getTotalAmount());

        inventory.dropMultiple("axe", 2);
        Assert.assertEquals(0, inventory.getAmount("axe"));
        Assert.assertEquals(6, inventory.getTotalAmount());
    }


    /**
     * Tests that main character can equip a weapon
     */
    @Test
    public void equipTest() {
        Assert.assertEquals("no_weapon", mc.getEquipped());

        mc.setEquipped("axe");
        Assert.assertEquals("axe", mc.getEquipped());

        mc.setEquipped("sword");
        Assert.assertEquals("sword", mc.getEquipped());
    }

    /**
     * Tests that main character can unequip a weapon
     */
    @Test
    public void unequipTest() {
        mc.setEquipped("axe");
        mc.unequip();
        Assert.assertEquals("no_weapon", mc.getEquipped());
    }

    /**
     * Tests that the quick access inventory can be accessed and modified
     * correctly when dealing with weapons
     */
    @Test
    public void quickAccessTest() {
        inventory.add(axe);
        inventory.quickAccessAdd("axe");
        Assert.assertTrue(inventory.getQuickAccess().containsKey("axe"));

        inventory.quickAccessRemove("axe");
        Assert.assertFalse(inventory.getQuickAccess().containsKey("axe"));
    }
}
