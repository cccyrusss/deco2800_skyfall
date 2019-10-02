package deco2800.skyfall.entities.worlditems;

import deco2800.skyfall.util.HexVector;
import deco2800.skyfall.worlds.Tile;

import java.util.Map;

public class VolcanicTree extends AbstractTree {

    protected static final String ENTITY_ID_STRING = "volcanic_tree";

    public VolcanicTree(float col, float row, int renderOrder, Map<HexVector, String> texture) {
        super(col, row, renderOrder, texture);
        this.woodAmount = 15;
    }

    public VolcanicTree(Tile tile, boolean obstructed) {
        super(tile, obstructed, "vTree" + nextTreeTexture);
        this.woodAmount = 15;

        VolcanicTree.nextTreeTexture = randomGen.nextInt(3) + 1;
        this.entityType = "VolcanicTree";
    }

    public VolcanicTree(SaveableEntityMemento memento) {
        super(memento);
        this.woodAmount = 15;
    }

    /**
     * The newInstance method implemented for the VolcanicTree class to allow for
     * item dispersal on game start up. This function is implemented with the
     * 
     * 
     * @return Duplicate rock tile with modified position.
     */
    @Override
    public VolcanicTree newInstance(Tile tile) {
        return new VolcanicTree(tile, this.isObstructed());
    }

    @Override
    public boolean equals(Object other) {

        if (!(other instanceof VolcanicTree)) {
            return false;
        }

        return super.equals(other);
    }

    /**
     * Gets the hashCode of the tree
     *
     * @return the hashCode of the tree
     */
    @Override
    public int hashCode() {
        return super.hashCode(97);
    }
}