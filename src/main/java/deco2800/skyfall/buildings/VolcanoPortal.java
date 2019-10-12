package deco2800.skyfall.buildings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import deco2800.skyfall.entities.MainCharacter;
import deco2800.skyfall.resources.Blueprint;
import deco2800.skyfall.worlds.world.World;

public class VolcanoPortal extends AbstractPortal implements Blueprint {
    private final transient Logger logger = LoggerFactory.getLogger(BuildingEntity.class);

    /**
     * Constructor for an building entity with normal rendering size.
     * 
     * @param col         the col position on the world
     * @param row         the row position on the world
     * @param renderOrder the height position on the world
     */
    public VolcanoPortal(float col, float row, int renderOrder) {
        super(col, row, renderOrder);
        this.setTexture("portal");
        this.setNext("volcanic_mountain");
        this.currentBiome = "mountain";
        this.name = "portal_volcano";
        this.blueprintLearned = false;

    }

    @Override
    public void teleport(MainCharacter character, World world) {
        logger.info("UNLOCKED ALL BIOMES - END OF GAME");
    }

}
