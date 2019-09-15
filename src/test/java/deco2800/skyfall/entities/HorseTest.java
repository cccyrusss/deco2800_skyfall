package deco2800.skyfall.entities;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class HorseTest {
    private Horse horse;

    private MainCharacter mainCharacter;
    @Before
    public void setup() throws Exception {
        horse=new Horse(1f,5f, mainCharacter);
    }


    /**
     * To test robot position
     */
    @Test
    public void positionTest(){
        assertThat("", horse.getCol(), is(equalTo(1f)));
        assertThat("", horse.getRow(), is(equalTo(5f)));
    }

    /**
     * To test the height of robot
     */
    @Test
    public void heightTest(){
        Assert.assertEquals(1,horse.getHeight());
    }



    /**
     * To test biome
     */
    @Test
    public void testBiome(){
        Assert.assertEquals("grass", horse.getBiome());
    }


    /**
     * To test robot enemy type
     */
    @Test
    public void getVehicleTest() {
        Assert.assertEquals("horse",horse.getVehicleType());

    }

    /**
     * To test whether moving
     */
    @Test
    public void getMovingTest() {
        Assert.assertEquals(false, horse.getMove());

    }


    /**
     * Check whether the vehicle is available for this biome
     */
    @Test
    public void isAvailableTest() {
        Assert.assertTrue(horse.isAvailable());

    }
}


