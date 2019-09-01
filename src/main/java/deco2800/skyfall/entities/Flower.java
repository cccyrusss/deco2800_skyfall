package deco2800.skyfall.entities;

import deco2800.skyfall.animation.Animatable;
import deco2800.skyfall.animation.AnimationLinker;
import deco2800.skyfall.animation.AnimationRole;
import deco2800.skyfall.animation.Direction;
import deco2800.skyfall.managers.GameManager;
import deco2800.skyfall.util.HexVector;
import deco2800.skyfall.util.WorldUtil;

public class Flower extends EnemyEntity implements Animatable {
    private static final transient int HEALTH = 2;
    private static final transient float ATTACK_RANGE = 1f;
    private static final transient int ATTACK_SPEED = 1000;
    private static final transient String BIOME="forest";
    private boolean moving=false;
    private static final transient String ENEMY_TYPE="flower";
    //savage animation
    private MainCharacter mc;
    public Flower(float row, float col, String texturename, int health, int armour, int damage) {
        super(row, col, texturename, health, armour, damage);
    }

    public Flower(float col, float row, MainCharacter mc) {
        super(col,row);
        this.setTexture("flower");
        this.setObjectName("flower");
        this.setHeight(1);
        this.setHealth(HEALTH);
        this.setLevel(2);
        this.setSpeed(1);
        this.setArmour(2);
        this.mc = mc;
        this.configureAnimations();
        this.setDirectionTextures();
    }

    public Flower(float col, float row) {
        super(col,row);
        this.setTexture("robot");
        this.setObjectName("robot");
        this.setHeight(1);
        this.setHealth(HEALTH);
        this.setLevel(2);
        this.setSpeed(1);
        this.setArmour(2);
    }

    public String getEnemyType(){
        return ENEMY_TYPE;
    }



    public boolean getMoving(){
        return moving;
    }

    public String getBiome(){
        return BIOME;
    }



    /**
     * @return string representation of this class including its enemy type, biome and x,y coordinates
     */
    @Override
    public String toString() {
        return String.format("%s at (%d, %d) %s biome", getEnemyType(), (int)getCol(), (int)getRow(),getBiome());
    }

    private boolean mcnear = false;
    private boolean mcleave = false;
    private int time1 =0;
    private int time2 = 0;
    private int time3 = 0;
    @Override
    public void onTick(long i) {
        if (this.isDead()) {
            this.flowerDead();
        } else {
            super.onTick(i);
            if (mc != null) {
                float colDistance = mc.getCol() - this.getCol();
                float rowDistance = mc.getRow() - this.getRow();

                if ((colDistance * colDistance + rowDistance * rowDistance) < 4 || mcnear) {
                    mcnear = true;
                    //flower open
                    if(time1<=135) {
                        time1++;
                        this.setCurrentState(AnimationRole.DEFENCE);
                    }
                    else {
                        //flower melee
                        if (time2 <= 120) {
                            time2++;
                            this.setCurrentState(AnimationRole.MELEE);
                        }
                        else {
                            //flower close if there is no play near it.
                            if((colDistance * colDistance + rowDistance * rowDistance) < 4 && !mcleave) {
                                time2 = 0;
                            }
                            else{
                                mcleave = true;
                                if (time3 <= 135 ) {
                                    time3++;
                                    this.setCurrentState(AnimationRole.MOVE);
                                }
                                else{
                                    this.setCurrentState(AnimationRole.NULL);
                                    mcnear = false;
                                    mcleave = false;
                                    time1 = 0;
                                    time2 = 0;
                                    time3 = 0;
                                }

                            }
                        }
                        }
                    }

                else {
                        this.setCurrentState(AnimationRole.NULL);


                }

            } else {
                System.out.println("Mc is null");
            }

        }
    }

    int time=0;
    private void flowerDead(){
        if(time<=100){
            time++;
            setCurrentState(AnimationRole.NULL);
            this.setTexture("flowerDead");
            this.setObjectName("flowerDead");
        }else{
            GameManager.get().getWorld().removeEntity(this);

        }

    }

    @Override
    public void configureAnimations() {
        this.addAnimations(
                AnimationRole.DEFENCE,
                Direction.DEFAULT,
                new AnimationLinker("flower_defence",
                        AnimationRole.MELEE, Direction.DEFAULT,
                        true, true));
        this.addAnimations(
                AnimationRole.MELEE,
                Direction.DEFAULT,
                new AnimationLinker("flower_melee",
                        AnimationRole.MELEE, Direction.DEFAULT,
                        true, true));
        this.addAnimations(
                AnimationRole.MOVE,
                Direction.DEFAULT,
                new AnimationLinker("flower_close",
                        AnimationRole.MOVE, Direction.DEFAULT,
                        true, true));

    }

    @Override
    public void setDirectionTextures() {

    }



}
