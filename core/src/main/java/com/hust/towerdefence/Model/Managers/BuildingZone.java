package com.hust.towerdefence.Model.Managers;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class BuildingZone {
    private final String name;
    private final Rectangle bounds;

    public BuildingZone(String name, float x, float y, float width, float height) {
        this.name = name;
        this.bounds = new Rectangle(x, y, width, height);
    }

    public String getName() {
        return name;
    }

    public Rectangle getBounds() {
        return new Rectangle(bounds);
    }

    public boolean contains(float x, float y) {
        return bounds.contains(x, y);
    }

    public Vector2 getCenter() {
        return new Vector2(bounds.x + bounds.width / 2f, bounds.y + bounds.height / 2f);
    }

    public boolean isPurchaseBuilding() {
        return "barrack".equals(name)
            || "archery".equals(name)
            || "monastery".equals(name)
            || "house1".equals(name)
            || "house2".equals(name);
    }

    public boolean isDefenseTower() {
        return "tower1".equals(name)
            || "tower2".equals(name)
            || "enemytower1".equals(name)
            || "enemytower2".equals(name);
    }

    public String getDisplayName() {
        if ("barrack".equals(name)) return "Barrack";
        if ("archery".equals(name)) return "Archery";
        if ("monastery".equals(name)) return "Monastery";
        if ("house1".equals(name)) return "House 1";
        if ("house2".equals(name)) return "House 2";
        if ("maintower".equals(name)) return "Main Tower";
        if ("tower1".equals(name)) return "Tower 1";
        if ("tower2".equals(name)) return "Tower 2";
        if ("enemytower1".equals(name)) return "Enemy Tower 1";
        if ("enemytower2".equals(name)) return "Enemy Tower 2";
        return name;
    }
}
