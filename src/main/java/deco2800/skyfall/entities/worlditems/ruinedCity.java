package deco2800.skyfall.entities.worlditems;

import deco2800.skyfall.worlds.Tile;

public class ruinedCity extends AbstractEnvironment {

    /**
     * Entity ID
     *
     */
    private static final String ENTITY_ID_STRING = "ruined_city";

    /**
     * Set the entity ID of ruined city to the object name
     *
     */
    public ruinedCity() {
        super();
        setupParams();
        this.setTexture("ruinedCity");
    }

    /**
     * Initialises
     *
     * @param tile       - The tile it spawns on
     * @param obstructed - Whether the entity is obstructed by something
     */
    public ruinedCity(Tile tile, boolean obstructed) {
        super(tile, "ruinedCity", obstructed);
        setupParams();
    }

    /**
     * Loads a static entity from a memento
     *
     * @param memento the static ruined city to add
     */
    public ruinedCity(SaveableEntityMemento memento) {
        super(memento);
        setupParams();
    }

    private void setupParams() {
        this.setObjectName(ENTITY_ID_STRING);
        this.entityType = "ruinedCity";
    }

    /**
     * Initialises ruined city
     *
     * @param tile - The tile it spawns on
     */
    @Override
    public ruinedCity newInstance(Tile tile) {
        return new ruinedCity(tile, this.isObstructed());
    }
}
