package deco2800.skyfall.resources;

import deco2800.skyfall.util.HexVector;
import deco2800.skyfall.worlds.Tile;




public abstract class HealthResources implements deco2800.skyfall.resources.Item {

    // can the item be stored in the inventory
    private Boolean carryable;
    // the name of the item e.g. food, poison
    private String name;
    //impact the player's health or not
    private Boolean hasHealingPower;

    // the name of the subtype the item belongs to
    public String subtype;
    // the co-ordinates of the tile the item has been placed on
    private HexVector position;

    //How many amount of healing power could be recovered
    //private Integer AmountOfHealingPower;

    //How many amount of healing will be deducted if have a poison
    //private Integer HealingDeducted;


    //if and only if the items deduct the HP of player
    //private Boolean notHealingPower;

    //Items could change or not e.g. coins, items
    private Boolean exchangeable;

    public int foodValue;
    public int healthValue;



    public HealthResources(){
        //Added this default constructor to resolve an issue in apple class - Kathryn
        //this.name = name;
        this.carryable = true;
        this.subtype = "Health Resource";
        this.hasHealingPower = true;
        //Do we need a new type like FoodResources? and hasFoodEffect may false in here as medicine may not affect the food fullness

        //this.notHealingPower=false;
        this.exchangeable = true;

    }

    /**
     * Creates a new Health Resource with the given name
     *
     * @param name     the identifying name of the Health Resource
     * @param position the tile which the item has been placed on
     */

    public HealthResources(String name, Tile position) {
        this.name = name;
        this.carryable = true;
        this.subtype = "Health Resource";
        this.hasHealingPower = true;
        //Do we need a new type like FoodResources? and hasFoodEffect may false in here as medicine may not affect the food fullness

        //this.notHealingPower=false;
        this.exchangeable = true;

        this.position = position.getCoordinates();
    }


    /**
     * Returns the name of the health resource
     *
     * @return The name of the health resource
     */

    public String getName() {
        return name;
    }


    /**
     * Returns whether or not the item can be stored in the inventory
     *
     * @return True if the item can be added to the inventory, false
     * if it is consumed immediately
     */


    public Boolean isCarryable() {
        return carryable;
    }

    /**
     * Returns the subtype which the item belongs to.
     *
     * @return The subtype which the item belongs to.
     */

    public String getSubtype() {
        return subtype;
    }

    /**
     * Returns whether or not the item impacts the player's health
     *
     * @return True if the item impacts on the player's health, false otherwise
     */

    public Boolean hasHealingPower() {
        return hasHealingPower;
    }


/*    *//**
     * Returns whether or not the item could deduct the HP of players
     * @return True if the item deduct the player's health, false otherwise


     */


    /**public Boolean getNotHealingPower() {
     * return notHealingPower;
     * }*/


    //public Boolean getNotHealingPower() {
        //return notHealingPower;
    //}




    /**
     * Returns whether or not the item could be exchanged
     *
     * @return True if the item could be exhanged, false otherwise
     */
    @Override
    public Boolean isExchangeable() {
        return exchangeable;
    }

    /**
     * Returns the co-ordinates of the tile the item is on.
     *
     * @return the co-ordinates of the tile the item is on.
     */
    @Override
    public HexVector getCoords() {
        return position;
    }

    public int getFoodValue() {
        return foodValue;
    }
    public int getHealthValue(){
        return healthValue;
    }

    /**
     * Creates a string representation of the health resource in the format:
     *
     * <p>'{Health Resource}:{Name}' </p>
     *
     * <p>without surrounding quotes and with {natural resource} replaced by
     * the subtype of the item and {name} replaced with the item name
     * For example: </p>
     *
     * <p>Health Resource:Wood </p>
     *
     * @return A string representation of the health resource.
     */
    @Override
    public String toString() {
        return "" + subtype + ":" + name;
    }



}