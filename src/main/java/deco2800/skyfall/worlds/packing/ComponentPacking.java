package deco2800.skyfall.worlds.packing;

import deco2800.skyfall.entities.AbstractEntity;
import deco2800.skyfall.util.HexVector;
import deco2800.skyfall.util.WorldUtil;
import deco2800.skyfall.worlds.Tile;
import deco2800.skyfall.worlds.biomes.AbstractBiome;
import deco2800.skyfall.worlds.world.World;

/**
 * This class is used to separate the huge environment packing into
 * different specific small parts/components to do the packing. And
 * it should be added into the packing queue from a Environment Packer.
 *
 */
public abstract class ComponentPacking {

    private EnvironmentPacker packer;

    ComponentPacking(EnvironmentPacker packer) {
        if (packer == null) {
            throw new NullPointerException("Invalid environment packer.");
        }
        this.packer = packer;
    }

    /**
     * Packing a small specific part of the environment packing.
     * @param world a world will be packed up by this procedure
     */
    public abstract void packing(World world);

    /* -------------------------------------------------------------------------------
        Followings are utility methods on packing process to avoid code duplication
     ------------------------------------------------------------------------------- */

    /**
     * @return which environment packer the packing component is in.
     */
    public EnvironmentPacker getPacker() {
        return packer;
    }

    /**
     * Get the biome type from a existing tile on the world.
     * @param x tile's X position on the world
     * @param y tile's Y position on the world
     * @return existing biome type if success, otherwise null
     */
    public AbstractBiome getBiomeFromTile(float x, float y) {
        HexVector tilePos = new HexVector(x, y);
        Tile tile = packer.getPackedWorld().getTile(tilePos);
        if (tile != null) {
            return tile.getBiome();
        }
        return null;
    }

    /**
     * Changes the biome type of an available tile on the world to an available
     * new biome type.
     * @param x tile's X position on the world
     * @param y tile's Y position on the world
     * @param newBiome a biome type object
     * @return true if success, otherwise false
     */
    public boolean changeTileBiome(float x, float y, AbstractBiome newBiome) {
        HexVector tilePos = new HexVector(x, y);
        Tile tile = getPacker().getPackedWorld().getTile(tilePos);
        if (newBiome != null && tile != null) {
            newBiome.addTile(tile);
            return true;
        }
        return false;
    }

    /**
     * Removes all entities on an available specific tile range on the world. Notes
     * that the removal includes entities that are not built based on the tile's
     * coordinate but within the range of the tile.
     * @param x tile's X position on the world
     * @param y tile's Y position on the world
     * @return true if success, otherwise false
     */
    public boolean removeEntityOnTile(float x, float y) {
        Tile tile = getPacker().getPackedWorld().getTile(x, y);

        if (tile != null) {
            float[] tilePos = WorldUtil.colRowToWorldCords(x, y);
            float tileSize = 100; // from somewhere in the game code

            for (AbstractEntity entity : getPacker ().getPackedWorld().getEntities()) {
                float[] entityPos = WorldUtil.colRowToWorldCords(entity.getCol(), entity.getRow());
                if ((entityPos[0] >= tilePos[0] && entityPos[0] <= tilePos[0] + tileSize)
                        && (entityPos[1] >= tilePos[1] && entityPos[1] <= tilePos[1] + tileSize)) {
                    getPacker().getPackedWorld().removeEntity(entity);
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Moves all entities on a specific tile range on the world to an available tile.
     * Notes that the movement includes entities that are not built based on the
     * tile's coordinate but within the range of the tile.
     * @param x old tile's X position on the world
     * @param y old tile's Y position on the world
     * @param newX a tile's X position moved to on the world
     * @param newY a tile's Y position moved to on the world
     * @return true if success, otherwise false
     */
    public boolean moveEntityFromTileToTile(float x, float y, float newX, float newY) {
        Tile oldTile = getPacker().getPackedWorld().getTile(x, y);
        Tile tile = getPacker().getPackedWorld().getTile(newX, newY);

        if (oldTile != null && tile != null) {
            float[] oldTilePos = WorldUtil.colRowToWorldCords(x, y);
            float tileSize = 100; // from somewhere in the game code

            for (AbstractEntity entity : getPacker ().getPackedWorld().getEntities()) {
                float[] entityPos = WorldUtil.colRowToWorldCords(entity.getCol(), entity.getRow());
                if ((entityPos[0] >= oldTilePos[0] && entityPos[0] <= oldTilePos[0] + tileSize)
                        && (entityPos[1] >= oldTilePos[1] && entityPos[1] <= oldTilePos[1] + tileSize)) {
                    entity.setPosition(entity.getCol() + tile.getCol() - oldTile.getCol(),
                            entity.getRow() + tile.getRow() - oldTile.getRow());
                }
            }
            return true;
        }
        return false;
    }
}