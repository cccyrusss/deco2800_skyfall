package deco2800.skyfall.worlds.biomes;

import deco2800.skyfall.worlds.Tile;

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


    //TODO implement algorithem ? That determines the ground patterns
    //TODO add seeding to the random generation so it can be tested
    //Likes grouped with likes

    /**
     * Method that will determine the textures of the forest biome textures
     *
     * @param random the RNG to use to generate the textures
     */
    @Override
    public void setTileTextures(Random random) {
        textures.add("mountain_0");
        for (Tile tile : getTiles()) {
            int randInt = random.nextInt(textures.size());
            tile.setTexture(textures.get(randInt));
        }
    }
}