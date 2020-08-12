package invasion.entity.block;

import invasion.Invasion;
import invasion.SoundHandler;
import invasion.entity.monster.InvadingEntity;
import invasion.init.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;


public class EggEntity extends InvadingEntity {
    //private static int META_HATCHED = 30;
    private static final DataParameter<Byte> META_HATCHED = EntityDataManager.createKey(EggEntity.class, DataSerializers.BYTE);
    private static final DataParameter<Integer> ROLL = EntityDataManager.createKey(InvadingEntity.class, DataSerializers.VARINT); //24
    private int hatchTime;
    private boolean hatched;
    private Entity parent;
    private Entity[] contents;

    public EggEntity(World world) {
        super(world);
        this.getDataManager().register(META_HATCHED, Byte.valueOf((byte) 0));
    }

    public EggEntity(Entity parent, Entity[] contents, int hatchTime) {
        super(parent.world);
        this.parent = parent;
        this.contents = contents;
        this.hatchTime = hatchTime;
        this.setBurnsInDay(false);
        this.hatched = false;
        this.ticks = 0;
        this.setBaseMoveSpeedStat(0.01F);

        this.getDataManager().register(META_HATCHED, Byte.valueOf((byte) 0));

        this.setMaxHealthAndHealth(Invasion.getMobHealth(this));

    }

    @Override
    public void tick() {
        super.tick();
        if(world.isRemote) return;


        if(this.ticksExisted> hatchTime *0.8) {
            getDataManager().set(META_HATCHED,(byte)1);
        }



    }

    @Override
    public void onEntityUpdate() {
        super.onEntityUpdate();
        if (!this.world.isRemote) {
            this.ticks += 1;
            if (this.hatched) {
                if (this.ticks > this.hatchTime + 40)
                    this.setDead();
            } else if (this.ticks > this.hatchTime) {
                this.hatch();
            }
        } else if ((!this.hatched)
                && (this.getDataManager().get(META_HATCHED) == 1)) {
            //this.world.playSoundAtEntity(this, "invmod:egghatch", 1.0F, 1.0F);
            this.playSound(SoundHandler.egghatch1, 1f, 1f);
            this.hatched = true;
        }
    }

    private void hatch() {
        //this.world.playSoundAtEntity(this, "invmod:egghatch", 1.0F, 1.0F);
        this.playSound(ModSounds.EGG_HATCH.get(), 1f, 1f);
        this.hatched = true;
        if (!this.world.isRemote) {
            this.getDataManager().set(META_HATCHED, Byte.valueOf((byte) 1));
            if (this.contents != null) {
                for (Entity entity : this.contents) {
                    entity.setPosition(this.posX, this.posY, this.posZ);
                    this.world.spawnEntity(entity);
                }
            }
        }
    }
}