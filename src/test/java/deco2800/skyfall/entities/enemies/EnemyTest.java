package deco2800.skyfall.entities.enemies;

import deco2800.skyfall.animation.AnimationRole;
import deco2800.skyfall.animation.Direction;
import deco2800.skyfall.entities.MainCharacter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test enemy.
 */
public class EnemyTest {

    // An instance of a main character
    private MainCharacter testCharacter;

    // Enemies
    private Enemy testEnemy;
    private Enemy testDummyEnemy;

    // Strings
    private String objectName = "Heavy";
    private String biomeName = "Forest";

    /**
     * Set up for enemy tests.
     */
    @Before
    public void setUp() {
        // Set up main character
        MainCharacter.resetInstance();
        testCharacter = MainCharacter.getInstance();

        // Set up enemy, need the longer constructor to test toString(), equals() and hashcode()
        testEnemy = new Enemy(30f, 30f, "enemyHitBox" , objectName,
            0.06f, biomeName, "enemyTexture");
        testEnemy.setHealth(10);

        testDummyEnemy = new Enemy(0f, 0f, "dummyHitBox" , objectName,
                0.06f, biomeName, "dummyTexture");
    }

    @Test
    public void setterAndGetterTests() {

        testEnemy.setHealth(10);
        Assert.assertEquals(10, testEnemy.getHealth());

        testEnemy.setBiome("testBiome");
        Assert.assertEquals("testBiome", testEnemy.getBiome());

        testEnemy.setMainCharacter(testCharacter);
        Assert.assertEquals(testCharacter, testEnemy.getMainCharacter());

        testEnemy.setHurt(true);
        Assert.assertTrue(testEnemy.getHurt());

        Assert.assertTrue(testEnemy.canDealDamage());
        Assert.assertArrayEquals(new int[0], testEnemy.getResistanceAttributes());

        // Test movementDirections, will test other directions later
        Assert.assertEquals(Direction.NORTH, testEnemy.movementDirection(2f));
    }

    /**
     * Test enemy's ability to be hurt.
     * Related methods include:
     *  > takeDamage()
     *  > checkIfHurtEnded()
     */
    @Test
    public void enemyHurtTest() {
        testEnemy.takeDamage(3);
        // set hurt time to 0.
        Assert.assertEquals(0, testEnemy.getHurtTime());
        // set enemy's "isHurt" status to true.
        Assert.assertTrue(testEnemy.getHurt());
        // reduce enemy's health
        Assert.assertEquals(7, testEnemy.getHealth());
        // If hurt equals TRUE,
        // and updateAnimation() is called in onTick(),
        // AnimationRole changed from NULL to HURT.
        testEnemy.updateAnimation();
        Assert.assertEquals(AnimationRole.HURT, testEnemy.getCurrentState());

        // when hurt time is less than 340 (less than 2 seconds in the game)...
        testEnemy.checkIfHurtEnded();
        Assert.assertEquals(20, testEnemy.getHurtTime());
        Assert.assertTrue(testEnemy.getHurt());
        // After hurt animations (around 2 seconds)...
        testEnemy.setHurtTime(360);
        testEnemy.checkIfHurtEnded();
        // reset hurt time to 0.
        Assert.assertEquals(0, testEnemy.getHurtTime());
        // enemy recovered
        Assert.assertFalse(testEnemy.getHurt());
    }

    @Test
    public void enemyAttackTest() {
        // will write that later
    }

    @Test
    public void setValues() {
        // someone write this
    }

    /**
     * Originally intended to test updateAnimation(),
     * but since it requires a set up of AnimationManager,
     * there will only be setter getter tests for now.
     */
    @Test
    public void setAndGetEnemyAnimationTest() {
        // If enemy dies, set animation state to DEAD.
        testEnemy.setCurrentState(AnimationRole.DEAD);
        Assert.assertEquals(AnimationRole.DEAD, testEnemy.getCurrentState());

        // If enemy hurts, set animation state to HURT.
        testEnemy.setCurrentState(AnimationRole.HURT);
        Assert.assertEquals(AnimationRole.HURT, testEnemy.getCurrentState());

        // If enemy moves, set animation state to MOVE.
        testEnemy.setCurrentState(AnimationRole.MOVE);
        Assert.assertEquals(AnimationRole.MOVE, testEnemy.getCurrentState());

        // If enemy attacks, set animation state to ATTACK.
        testEnemy.setCurrentState(AnimationRole.HURT);
        Assert.assertEquals(AnimationRole.HURT, testEnemy.getCurrentState());
    }

    /**
     * Test whether the sound files for the enemies are correct.
     */
    @Test
    public void setAndGetSoundTest() {
        testEnemy.configureSounds();
        Assert.assertEquals("enemy_walk", testEnemy.getChaseSound());
        Assert.assertEquals("enemy_attack", testEnemy.getAttackSound());
        Assert.assertEquals("enemy_dead", testEnemy.getDeadSound());
    }

    @Test
    public void testToString() {
        String testString = "Heavy at (30, 30) Forest biome";
        Assert.assertEquals(testString, testEnemy.toString());
    }

    @Test
    public void testEquals() {
        // Not equal due to different hashcode.
        Assert.assertFalse(testEnemy.equals(testDummyEnemy));

        // Not equal due to different instance
        Assert.assertFalse(testEnemy.equals(testCharacter));

        // Equals due to same instance and same hashcode
        testDummyEnemy = new Enemy(30f, 30f, "enemyHitBox" ,
                "Heavy",0.06f, "Forest", "enemyTexture");
        Assert.assertTrue(testEnemy.equals(testDummyEnemy));
    }
}