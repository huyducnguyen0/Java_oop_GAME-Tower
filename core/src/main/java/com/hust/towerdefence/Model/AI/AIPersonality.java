package com.hust.towerdefence.Model.AI;
import java.util.Random;
public class AIPersonality {
    private final float pawnWeight;
    private final float warriorWeight;
    private final float tntWeight;
    private final float saveWeight;

    public AIPersonality(long seed) {
        Random rand = new Random(seed);
        pawnWeight = 0.7f + rand.nextFloat() * 0.6f;
        warriorWeight = 0.7f + rand.nextFloat() * 0.6f;
        tntWeight = 0.7f + rand.nextFloat() * 0.6f;
        saveWeight = 0.7f + rand.nextFloat() * 0.6f;
    }

    public float getPawnWeight() { return pawnWeight; }
    public float getWarriorWeight() { return warriorWeight; }
    public float getTntWeight() { return tntWeight; }
    public float getSaveWeight() { return saveWeight; }
}
