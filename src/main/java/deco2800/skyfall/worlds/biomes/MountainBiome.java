package deco2800.skyfall.worlds.biomes;

import deco2800.skyfall.worlds.Tile;

import deco2800.skyfall.worlds.generation.perlinnoise.TileNoiseGenerator;
import java.util.ArrayList;
import java.util.Random;

/**
 * Mountain biome
 */
public class MountainBiome extends AbstractBiome {
    private ArrayList<String> textures = new ArrayList<>();

    /**
     * Constructor for a Biome
     */
    public MountainBiome() {
        super("mountain");
    }


    /**
     * Method that will determine the textures of the forest biome textures
     *
     * @param random the RNG to use to generate the textures
     */

    @Override
    public void setTileTextures(Random random) {
        ArrayList<String> textures = new ArrayList<>();
        textures.add("mountain_7");
        textures.add("mountain_8");
//        textures.add("mountain_5");
//        textures.add("mountain_0");
//        textures.add("mountain_6");

        //Perlin noise generation
        new TileNoiseGenerator(getTiles(), random, 4, 10,0.2, Tile::setPerlinValue);


        for (Tile tile : getTiles()) {
            int perlinValue = (int) Math.floor(tile.getPerlinValue() * textures.size());
            tile.setTexture(textures.get(perlinValue < textures.size() ? perlinValue : textures.size() - 1));
        }
    }

}