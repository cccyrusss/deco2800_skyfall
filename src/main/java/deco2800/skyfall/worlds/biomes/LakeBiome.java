package deco2800.skyfall.worlds.biomes;

import deco2800.skyfall.worlds.Tile;
import deco2800.skyfall.worlds.generation.perlinnoise.NoiseGenerator;
import deco2800.skyfall.worlds.generation.perlinnoise.TileNoiseGenerator;

import java.util.ArrayList;
import java.util.Random;

/**
 * Lake biome that is used for the lakes
 */
public class LakeBiome extends AbstractBiome {
    private NoiseGenerator textureGenerator;

    /**
     * Constructor for a Biome
     */
    public LakeBiome(AbstractBiome parentBiome, Random random) {
        super("lake", parentBiome);

        textureGenerator = new NoiseGenerator(random, 3, 40, 0.7);
    }

    @Override
    public void setTileTexture(Tile tile) {
        ArrayList<String> textures = new ArrayList<>();
        textures.add("lake_1");
        textures.add("lake_2");

        double perlinValue = textureGenerator.getOctavedPerlinValue(tile.getCol(), tile.getRow());
        int adjustedPerlinValue = (int) Math.floor(perlinValue * textures.size());
        if (adjustedPerlinValue >= textures.size()) {
            adjustedPerlinValue = textures.size() - 1;
        }
        // TODO Is `setPerlinValue` still required?
        tile.setPerlinValue(adjustedPerlinValue);
        tile.setTexture(textures.get(adjustedPerlinValue));
    }
}
