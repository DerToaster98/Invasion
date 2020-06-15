package invasion.nexus;

import invasion.util.RandomSelectionPool;


public class EntityPattern implements IEntityIMPattern {
    private final IMEntityType entityType;
    private final RandomSelectionPool<Integer> tierPool;
    private final RandomSelectionPool<Integer> texturePool;
    private final RandomSelectionPool<Integer> flavourPool;
    private static final int DEFAULT_TIER = 1;
    private static final int DEFAULT_FLAVOUR = 0;
    private static final int OPEN_TEXTURE = 0;
    private static final int OPEN_SCALING = 0;

    public EntityPattern(IMEntityType entityType) {
        this.entityType = entityType;
        this.tierPool = new RandomSelectionPool<>();
        this.texturePool = new RandomSelectionPool<>();
        this.flavourPool = new RandomSelectionPool<>();
    }

    @Override
    public EntityConstruct generateEntityConstruct() {
        return this.generateEntityConstruct(-180, 180);
    }

    @Override
    public EntityConstruct generateEntityConstruct(int minAngle, int maxAngle) {
        Integer tier = this.tierPool.selectNext();
        if (tier == null) {
            tier = 1;
        }
        Integer texture = this.texturePool.selectNext();
        if (texture == null) {
            texture = 0;
        }
        Integer flavour = this.flavourPool.selectNext();
        if (flavour == null) {
            flavour = 0;
        }
        return new EntityConstruct(this.entityType, tier, texture, flavour, 0.0F, minAngle, maxAngle);
    }

    public void addTier(int tier, float weight) {
        this.tierPool.addEntry(tier, weight);
    }

    public void addTexture(int texture, float weight) {
        this.texturePool.addEntry(texture, weight);
    }

    public void addFlavour(int flavour, float weight) {
        this.flavourPool.addEntry(flavour, weight);
    }

    @Override
    public String toString() {
        return "EntityIMPattern@" + Integer.toHexString(this.hashCode()) + "#" + this.entityType;
    }
}