package deco2800.skyfall.entities.worlditems;

import deco2800.skyfall.worlds.biomes.AbstractBiome;
import deco2800.skyfall.worlds.biomes.ForestBiome;
import deco2800.skyfall.worlds.generation.perlinnoise.NoiseGenerator;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

public class EntitySpawnRuleTest {
    AbstractBiome biome;

    @Test
    public void testAccessorsAndMutators() {
        ForestBiome biome = new ForestBiome(new Random(0));

        SpawnControl pieceWise = x -> {
            if ((0 < x) && (x <= 0.5)) {
                return 0;
            } else if ((0.5 < x) && (x <= 0.8)) {
                return 0.05;
            } else {
                return 0.4;
            }
        };

        EntitySpawnRule rule = new EntitySpawnRule(tile -> null, 10, true, pieceWise);
        rule.setChance(0.1);

        assertEquals(0.1, rule.getChance(), 0.001);
        // assertEquals(10, rule.getMin());
        // assertEquals(100, rule.getMax());

        // rule.setMin(9);
        // rule.setMax(101);

        // assertEquals(9, rule.getMin());
        // assertEquals(101, rule.getMax());

        // assertEquals(rule.getBiome(), biome);
        assertEquals(rule.getAdjustMap(), pieceWise);
        assertTrue(rule.getUsePerlin());
        assertFalse(rule.getLimitAdjacent());

        rule.setLimitAdjacent(true);
        rule.setLimitAdjacentValue(5.0);

        assertEquals(5.0, rule.getLimitAdjacentValue(), 0.001);
        assertTrue(rule.getLimitAdjacent());

        NoiseGenerator newGenerator = new NoiseGenerator(new Random().nextLong(), 2, 2.0, 0.3);
        rule.setNoiseGenerator(newGenerator);
        assertEquals(newGenerator, rule.getNoiseGenerator());
    }

    @Test
    public void testConstructors() {
        // FIXME:Ontonator Test for the `newInstance` value.

        EntitySpawnRule rule = new EntitySpawnRule(tile -> null, 20, 0);
        assertEquals(0.0, rule.getChance(), 0.001);

        // rule = new EntitySpawnRule(tile -> null, 5, 10);
        // assertEquals(5, rule.getMin());
        // assertEquals(10, rule.getMax());

        // rule = new EntitySpawnRule(tile -> null, 0.5, 5, 10);
        // assertEquals(0.5, rule.getChance(), 0.001);
        // assertEquals(5, rule.getMin());
        // assertEquals(10, rule.getMax());

        // biome = new ForestBiome(new Random(0));
        // rule = new EntitySpawnRule(tile -> null, 0.5, biome);
        // assertEquals(0.5, rule.getChance(), 0.001);
        // assertEquals(biome, rule.getBiome());

        // rule = new EntitySpawnRule(tile -> null, 0.5, 2, 100, biome);
        // assertEquals(0.5, rule.getChance(), 0.001);
        // assertEquals(2, rule.getMin());
        // assertEquals(100, rule.getMax());
        // assertEquals(biome, rule.getBiome());
    }

}
