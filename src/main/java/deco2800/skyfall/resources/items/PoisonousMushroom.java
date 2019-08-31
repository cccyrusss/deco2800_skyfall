package deco2800.skyfall.resources.items;

import deco2800.skyfall.resources.Item;
import deco2800.skyfall.resources.HealthResources;
import deco2800.skyfall.util.HexVector;
import deco2800.skyfall.worlds.Tile;

public class PoisonousMushroom extends HealthResources implements Item {
    // the name of the item
    private String name;

    // the colour of the PoisonousMushroom
    private String colour;

    // determines whether or not the resource can be traded
    //private Boolean exchangeable;

    // the biome the poisonous mushroom is in (will change to different type in future?)
    private String biome;


    //whether or not the item impacts the player's food fullness
    private Boolean hasFoodEffect;


    /**
     * Create default Poisonous Mushroom.
     */
    public PoisonousMushroom(){

        this.biome = "Forest";
        this.colour ="black white";
        this.name ="PoisonousMushroom";
        this.hasFoodEffect = true;
        //PoisonousMushroom can increase the foodValue but reduce the healthValue
        this.foodValue = -20;
        //Todo: Look into this.healthValue = -20;
        this.healthValue = -20;
    }

    @Override
    public String getName() {
        return "PoisonousMushroom";
    }


    /**
     * Returns the biome the PoisonousMushroom is situated in
     * @return the biome the PoisonousMushroom is situated in
     */
    public String getBiome(){
        return biome;
    }


    /**
     * Returns whether or not the item impacts the player's food fullness
     *
     * @return True if the item impacts on the player's food fullness, false otherwise
     */
    public Boolean hasFoodEffect() {
        return hasFoodEffect;
    }

    @Override
    public String toString() {
        return "" + subtype + ":" + name;
    }


    /**
     * Returns the item description
     * @return the item description
     */
    @Override
    public String getDescription() {
        return "This item reduces the main character's health.";
    }



}