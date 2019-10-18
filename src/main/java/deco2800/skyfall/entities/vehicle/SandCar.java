package deco2800.skyfall.entities.vehicle;

import deco2800.skyfall.entities.MainCharacter;
import deco2800.skyfall.managers.GameManager;

public class SandCar extends AbstractVehicle {
    private static final transient String BIOME = "desert";
    public static final String TextureName = "sand_car";
    private boolean underUsing = false;
    MainCharacter mc;

    public SandCar(float col, float row, MainCharacter mc) {
        super(col, row, TextureName);
        this.setTexture(TextureName);
        this.setObjectName(TextureName);
        this.setHeight(1);
        this.setHealth(10);
        this.setSpeed(0.00f);
        this.mc = mc;
        this.configureAnimations();
        this.setDirectionTextures();
    }

    public String getBiome() {
        return BIOME;
    }

    public boolean isOnUse() {
        return underUsing;
    }

    @Override
    public String getName() {
        return SandCar.TextureName;
    }

    @Override
    public String getDescription() {
        return "vehicle sand_car";
    }

    public void removeSandCar() {
        GameManager.get().getWorld().removeEntity(this);
    }
}
