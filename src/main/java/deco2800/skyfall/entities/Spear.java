package deco2800.skyfall.entities;

public class Spear extends RangeWeapon {

    /**
     * Get durability of the weapon
     * @return a number represent the durability of the weapon
     * on the scale of 0 to 10
     */
    @Override
    public int getDurability() {
        return 7;
    }

    /**
     * Get attack rate of the weapon
     * @return a number represent the attack rate of the weapon
     * on the scale of 1 to 5
     */
    @Override
    public int getAttackRate() {
        return 4;
    }

    /**
     * Get damage of the weapon
     * @return a number as damage amount of the weapon
     */
    @Override
    public int getDamage() {
        return 5;
    }

    /**
     * Return the name of the weapon
     * @return a string "spear"
     */
    public String getName() {
        return "spear";
    }

    @Override
    public void onTick(long i) {

    }
}
