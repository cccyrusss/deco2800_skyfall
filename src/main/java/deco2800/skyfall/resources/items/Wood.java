package deco2800.skyfall.resources.items;

import deco2800.skyfall.resources.Item;
import deco2800.skyfall.resources.NaturalResources;
import deco2800.skyfall.util.HexVector;
import deco2800.skyfall.worlds.Tile;

public class Wood extends NaturalResources implements Item {

    public Wood(){
        //default constructor added for building inventory
    }

    public Wood(String name, Tile position){
        super(name, position);
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Boolean hasHealingPower() {
        return null;
    }

    @Override
    public HexVector getCoords() {
        return null;
    }

    @Override
    public String getSubtype() {
        return null;
    }

    @Override
    public Boolean isCarryable() {
        return null;
    }
}
