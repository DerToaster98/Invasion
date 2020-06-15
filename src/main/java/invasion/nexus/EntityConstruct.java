package invasion.nexus;

public class EntityConstruct {
    private final IMEntityType entityType;
    private final int texture;
    private final int tier;
    private final int flavour;
    private final int minAngle;
    private final int maxAngle;
    private final float scaling;

    public EntityConstruct(IMEntityType mobType, int tier, int texture, int flavour, float scaling, int minAngle, int maxAngle) {
        this.entityType = mobType;
        this.texture = texture;
        this.tier = tier;
        this.flavour = flavour;
        this.scaling = scaling;
        this.minAngle = minAngle;
        this.maxAngle = maxAngle;
    }

    public IMEntityType getMobType() {
        return entityType;
    }

    public int getTexture() {
        return texture;
    }

    public int getTier() {
        return tier;
    }

    public int getFlavour() {
        return flavour;
    }

    public float getScaling() {
        return scaling;
    }

    public int getMinAngle() {
        return minAngle;
    }

    public int getMaxAngle() {
        return maxAngle;
    }
}