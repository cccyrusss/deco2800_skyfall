package deco2800.skyfall.entities.enemies;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An instance of a Scout (easy level) enemy.
 */
public class Scout extends Enemy {

    // Logger for tracking enemy information
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Constructor to create new Scout enemy.
     *
     * @param col the x-coordinate of the enemy.
     * @param row the y-coordinate of the enemy.
     * @param scaling the factor the enemy's stat is scale in this enemy.
     * @param biome the biome this enemy is in.
     * @param textureName the name of the texture of the enemt.
     */
    public Scout(float col, float row, float scaling, String biome, String textureName) {
        super(col, row, "ScoutHitBox", "Scout",0.2f,  biome, textureName);

        this.setTexture("enemyScout");

        // Assign values, includes default values
        this.setValues(scaling, 10, 1, 3,10,20);
    }
}
