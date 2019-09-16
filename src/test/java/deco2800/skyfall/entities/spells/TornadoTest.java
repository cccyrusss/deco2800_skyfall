package deco2800.skyfall.entities.spells;

import deco2800.skyfall.entities.AbstractEntity;
import deco2800.skyfall.entities.EnemyEntity;
import deco2800.skyfall.entities.Spider;
import deco2800.skyfall.managers.GameManager;
import deco2800.skyfall.util.HexVector;
import deco2800.skyfall.worlds.world.World;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class TornadoTest {

    Tornado tornado;

    @Before
    public void setup() throws Exception {
        tornado = new Tornado(new HexVector(), "tornado_placeholder",
                "spell", 0f, 0f,
                20,
                0.1f,
                10);
    }

    /**
     * Test the tornado position.
     */
    @Test
    public void positionTest(){
        assertThat("", tornado.getCol(), is(equalTo(0f)));
        assertThat("", tornado.getRow(), is(equalTo(0f)));
    }

    @Test
    public void getManaTest() {
        assertThat("", tornado.getManaCost(), is(equalTo(10)));
    }

    @Test
    public void testOnTick() {

        //Test the inside branch.

        GameManager gm = GameManager.get();
        World world = mock(World.class);
        EnemyEntity enemy = mock(EnemyEntity.class);
        gm.setWorld(world);

        //Add a new list with mock enemy.
        List list = new LinkedList<AbstractEntity>();
        list.add(enemy);

        //Make gm and world return mocked objects.
        when (world.getEntities()).thenReturn(list);
        //Mock enemy position at the position of the projectile.
        when (enemy.getPosition()).thenReturn(new HexVector(0f,0f));

        tornado.onTick(0);
        //Reset cooldown.
        tornado.onTick(0);

        //Verify enemy took 20 damage twice.
        //I've already tested the takeDamage method in enemy test.
        verify(enemy, times(2)).takeDamage(20);
    }
}