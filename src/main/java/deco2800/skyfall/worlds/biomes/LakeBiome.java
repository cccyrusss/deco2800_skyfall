package deco2800.skyfall.worlds.biomes;

import deco2800.skyfall.worlds.Tile;
import deco2800.skyfall.worlds.generation.perlinnoise.TileNoiseGenerator;

import java.util.ArrayList;
import java.util.Random;

/**
 * Lake biome that is used for the lakes
 */
public class LakeBiome extends AbstractBiome {

    /**
     * Constructor for a Biome
     */
    public LakeBiome(AbstractBiome parentBiome) {
        super("lake", parentBiome);
    }


    /**
     * Method that will determine the textures of the ocean biome textures
     *
     * @param random the RNG to use to generate the textures
     */
    @Override
    public void setTileTextures(Random random) {
        // TODO see if different textures should be used to the ocean
        ArrayList<String> textures = new ArrayList<>();
        textures.add("lake_1");
        textures.add("lake_2");

        //Perlin noise generation
        new TileNoiseGenerator(getTiles(), random, 3, 40,0.7, Tile::setPerlinValue);

        for (Tile tile : getTiles()) {
            int perlinValue = (int) Math.floor(tile.getPerlinValue() * textures.size());
            tile.setTexture(textures.get(perlinValue < textures.size() ? perlinValue : textures.size() - 1));
        }
    }
}

