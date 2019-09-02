package deco2800.skyfall.entities;

import deco2800.skyfall.managers.GameManager;
import deco2800.skyfall.managers.PhysicsManager;
import deco2800.skyfall.worlds.biomes.AbstractBiome;
import deco2800.skyfall.worlds.biomes.ForestBiome;
import deco2800.skyfall.worlds.world.TestWorld;

import deco2800.skyfall.worlds.Tile;
import deco2800.skyfall.worlds.world.World;
import deco2800.skyfall.worlds.world.WorldBuilder;
import deco2800.skyfall.worlds.world.WorldDirector;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(GameManager.class)
public class EntitySpawnTableTest {

    private World testWorld = null;

    private PhysicsManager physics;

    // size of test world
    final int worldSize = 100;

    // create a mock game manager for successful .getWorld()
    @Mock
    private GameManager mockGM;

    AbstractBiome biome;

    @Before
    public void createTestEnvironment() {
        WorldBuilder worldBuilder = new WorldBuilder();
        WorldDirector.constructTestWorld(worldBuilder);
        testWorld = worldBuilder.getWorld();

        biome = new ForestBiome();

        // create tile map, add tiles and push to testWorld
        CopyOnWriteArrayList<Tile> tileMap = new CopyOnWriteArrayList<>();

        for (int i = 0; i < worldSize; i++) {
            Tile tile = new Tile(1.0f * i, 0.0f);
            tileMap.add(tile);
            biome.addTile(tile);
        }

        testWorld.setTileMap(tileMap);

        mockGM = mock(GameManager.class);
        mockStatic(GameManager.class);

        // required for proper EntitySpawnTable.placeEntity
        when(GameManager.get()).thenReturn(mockGM);
        when(mockGM.getWorld()).thenReturn(testWorld);

        physics = new PhysicsManager();
        when(mockGM.getManager(PhysicsManager.class)).thenReturn(physics);
    }

    // tests the place method
    @Test
    public void testPlaceEntity() {
        Tile tile = new Tile(0.0f, 0.0f);
        Rock rock = new Rock();

        // check tile has no rock
        assertTrue(!tile.hasParent());

        // place
        EntitySpawnTable.placeEntity(rock, tile);

        // has the rock
        assertTrue(tile.hasParent());

    }

    // simple method to count number of static entities on world
    private int countWorldEntities() {
        int count = 0;
        for (Tile tile : testWorld.getTileMap()) {
            if (tile.hasParent()) {
                count++;
            }
        }
        return count;
    }

    @Test
    public void testDirectPlace() {
        // check if construction was valid
        assertEquals(worldSize, testWorld.getTileMap().size());

        // count before spawning
        assertEquals(0, countWorldEntities());

        // check basic spawnEntities
        final double chance = 0.95;
        Rock rock = new Rock();
        EntitySpawnTable.spawnEntities(rock, chance, testWorld);

        // count after spawning
        assertTrue(countWorldEntities() > 0);

        //check perlin noice place
        int currentCount = countWorldEntities();



    }

    @Test
    public void maxMinPlacementTest() {
        WorldBuilder worldBuilder = new WorldBuilder();
        WorldDirector.constructTestWorld(worldBuilder);
        World newWorld = worldBuilder.getWorld();

        // create tile map, add tiles and push to testWorld
        CopyOnWriteArrayList<Tile> newTileMap = new CopyOnWriteArrayList<>();

        for (int i = 0; i < worldSize; i++) {
            Tile tile = new Tile(1.0f * i, 0.0f);
            newTileMap.add(tile);
            biome.addTile(tile);
        }

        newWorld.setTileMap(newTileMap);

        EntitySpawnRule newRule = new EntitySpawnRule(2, 4, null, true);
        newRule.setChance(1.0);

        Rock rock = new Rock();
        EntitySpawnTable.spawnEntities(rock, newRule, newWorld);

        int count = 0;
        for (Tile tile : newWorld.getTileMap()) {
            if (tile.hasParent()) {
                count++;
            }
        }

        assertTrue("Count was " + count, count <= 4);
    }
}
