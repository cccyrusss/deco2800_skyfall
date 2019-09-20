package deco2800.skyfall.entities.pets;

import deco2800.skyfall.animation.Animatable;
import deco2800.skyfall.animation.AnimationLinker;
import deco2800.skyfall.animation.AnimationRole;
import deco2800.skyfall.animation.Direction;
import deco2800.skyfall.entities.AbstractEntity;
import deco2800.skyfall.entities.Harvestable;
import deco2800.skyfall.entities.MainCharacter;
import deco2800.skyfall.entities.Spider;
import deco2800.skyfall.worlds.Tile;

import java.util.ArrayList;
import java.util.List;

public class IceWhitebear extends AbstractPet implements Animatable, Harvestable {
    MainCharacter mc;
    public IceWhitebear(float col, float row, MainCharacter mc){
        super(col, row);
        this.setTexture("icewhitebear");
        this.setObjectName("icewhitebear");
        this.setHeight(1);
        this.setHealth(3);
        this.setLevel(1);
        this.setSpeed(1);
        this.setArmour(1);
        this.mc = mc;

    }

    @Override
    public void onTick(long i) {
    }
    @Override
    public void configureAnimations() {
    }

    @Override
    public void setDirectionTextures() {

    }

    public void destoryice(){
        this.setHealth(this.getHealth()-1);
    }

    @Override
    public List<AbstractEntity> harvest(Tile tile) {
        List<AbstractEntity> abstractEntityList=new ArrayList<>();
        Whitebear whitebear=new Whitebear(-2, 0,mc);
        whitebear.setDomesticated(false);
        abstractEntityList.add(whitebear);
        return abstractEntityList;
    }
}
