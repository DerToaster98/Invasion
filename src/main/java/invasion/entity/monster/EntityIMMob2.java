package invasion.entity.monster;

import invasion.nexus.Nexus;
import net.minecraft.entity.EntityCreature;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;


public class EntityIMMob2 extends EntityCreature {

    private static final DataParameter<Integer> TIER = EntityDataManager.createKey(EntityIMMob2.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> TEXTURE = EntityDataManager.createKey(EntityIMMob2.class, DataSerializers.VARINT);

    private Nexus nexus;

    public EntityIMMob2(World worldIn) {
        super(worldIn);
    }

    public EntityIMMob2(World worldIn, Nexus nexus) {
        super(worldIn);
        this.nexus = nexus;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.getDataManager().register(TIER, 1);
        this.getDataManager().register(TEXTURE, 0);
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();

    }

    public Nexus getNexus() {
        return this.nexus;
    }

    public int getTier() {
        return this.getDataManager().get(TIER);
    }

    public void setTier(int tier) {
        this.getDataManager().set(TIER, tier);
    }

    public int getTextureID() {
        return this.getDataManager().get(TEXTURE);
    }

}
