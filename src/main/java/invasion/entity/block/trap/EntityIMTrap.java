package invasion.entity.block.trap;

import invasion.BlocksAndItems;
import invasion.SoundHandler;
import invasion.entity.monster.InvadingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;


public class EntityIMTrap extends Entity {

    public static final int TRAP_DEFAULT = 0;
    public static final int TRAP_RIFT = 1;
    public static final int TRAP_FIRE = 2;
    private static final int ARM_TIME = 60;
    private static final DataParameter<Byte> META_CHANGED = EntityDataManager.createKey(EntityIMTrap.class, DataSerializers.BYTE);
    private static final DataParameter<Integer> TRAP_TYPE = EntityDataManager.createKey(EntityIMTrap.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> IS_EMPTY = EntityDataManager.createKey(EntityIMTrap.class, DataSerializers.BOOLEAN);
    //private static final int META_CHANGED = 29;
    //private static final int META_TYPE = 30;
    //private static final int META_EMPTY = 31;
    private int trapType;
    private int ticks;
    private boolean isEmpty;
    private byte metaChanged;
    private boolean fromLoaded;

    public EntityIMTrap(World world) {
        super(world);
        this.setSize(0.5F, 0.28F);
        this.ticks = 0;
        this.isEmpty = false;
        this.isImmuneToFire = true;
        this.trapType = 0;
        this.metaChanged = (byte) (world.isRemote ? 1 : 0);
        //this.dataWatcher.addObject(29, Byte.valueOf(this.metaChanged));
        //this.dataWatcher.addObject(30, Integer.valueOf(this.trapType));
        //this.dataWatcher.addObject(31, Byte.valueOf((byte) (this.isEmpty ? 0 : 1)));
        this.getDataManager().register(META_CHANGED, this.metaChanged);
        this.getDataManager().register(TRAP_TYPE, this.trapType);
        this.getDataManager().register(IS_EMPTY, this.isEmpty);
    }

    public EntityIMTrap(World world, double x, double y, double z, int trapType) {
        this(world);
        this.trapType = trapType;
        //this.dataWatcher.updateObject(30, Integer.valueOf(trapType));
        this.getDataManager().set(TRAP_TYPE, this.trapType);
        this.setLocationAndAngles(x, y, z, 0.0F, 0.0F);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        this.ticks += 1;
        if (this.world.isRemote) {
			/*if ((this.metaChanged != this.dataWatcher.getWatchableObjectByte(29)) || (this.ticks % 20 == 0)) {
				this.metaChanged = this.dataWatcher.getWatchableObjectByte(29);
				this.trapType = this.dataWatcher.getWatchableObjectInt(30);
				boolean wasEmpty = this.isEmpty;
				this.isEmpty = (this.dataWatcher.getWatchableObjectByte(31) == 0);*/
            if ((this.metaChanged != this.getDataManager().get(META_CHANGED)) || (this.ticks % 20 == 0)) {
                this.metaChanged = this.getDataManager().get(META_CHANGED);
                this.trapType = this.getDataManager().get(TRAP_TYPE);
                boolean wasEmpty = this.isEmpty;
                this.isEmpty = this.getDataManager().get(IS_EMPTY);
                if ((this.isEmpty) && (!wasEmpty) && (this.trapType == 1))
                    this.doRiftParticles();
            }
            return;
        }

        if (!this.isValidPlacement()) {
            EntityItem entityitem = new EntityItem(this.world, this.posX,
                    this.posY, this.posZ, new ItemStack(BlocksAndItems.itemEmptyTrap, 1));
            entityitem.setDefaultPickupDelay();
            this.world.spawnEntity(entityitem);
            this.setDead();
        }

        if ((this.world.isRemote) || ((!this.isEmpty) && (this.ticks < 60))) {
            return;
        }

        List<EntityLivingBase> entities = this.world.getEntitiesWithinAABB(
                EntityLivingBase.class, this.getEntityBoundingBox());
        if ((entities.size() > 0) && (!this.isEmpty)) {
            for (EntityLivingBase entity : entities) {
                if (this.trapEffect(entity)) {
                    this.setEmpty();
                    return;
                }
            }
        }
    }

    public boolean trapEffect(EntityLivingBase triggerEntity) {

        switch (this.trapType) {
            default:
                triggerEntity.attackEntityFrom(DamageSource.GENERIC, 4.0F);
                break;
            case 1:
                triggerEntity.attackEntityFrom(DamageSource.MAGIC,
                        (triggerEntity instanceof EntityPlayer) ? 12.0F : 38.0F);

                List<Entity> entities = this.world.getEntitiesWithinAABBExcludingEntity(this,
                        this.getEntityBoundingBox().expand(1.899999976158142D, 1.0D, 1.899999976158142D));
                for (Entity entity : entities) {
                    entity.attackEntityFrom(DamageSource.MAGIC, 8.0F);
                    if ((entity instanceof InvadingEntity)) {
                        ((InvadingEntity) entity).stunEntity(60);
                    }
                }
                //this.world.playSoundAtEntity(this, "random.break", 1.5F,1.0F * (this.rand.nextFloat() * 0.25F + 0.55F));
                this.playSound(SoundEvents.ENTITY_ITEM_BREAK, 1.5f, this.rand.nextFloat() * 0.25f + 0.55f);
                break;
            case 2:
                //this.world.playSoundAtEntity(this, "mod_invasion:fireball" + 1, 1.5F,1.15F / (this.rand.nextFloat() * 0.3F + 1.0F));
                this.playSound(SoundHandler.fireball1, 1.5f, 1.15f / (this.rand.nextFloat() * 0.3f + 1f));
                this.doFireball(1.1F, 8);
                break;
            case 3:
                // Add poison effect to all surrounding Entities.
                break;

        }

        return true;
    }

    @Override
    public void onCollideWithPlayer(EntityPlayer entityPlayer) {
        if ((!this.world.isRemote) && (this.ticks > 30) && (this.isEmpty)) {
            if (entityPlayer.inventory.addItemStackToInventory(new ItemStack(BlocksAndItems.itemEmptyTrap, 1))) {
                //this.world.playSoundAtEntity(this, "random.pop", 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                this.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 0.2f, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7f + 1f) * 2f);
                entityPlayer.onItemPickup(this, 1);
                this.setDead();
            }
        }
    }

    @Override
    //public boolean interactFirst(EntityPlayer entityPlayer) {
    public EnumActionResult applyPlayerInteraction(EntityPlayer player, Vec3d vec, @Nullable ItemStack stack, EnumHand hand) {
        if ((this.world.isRemote) || (this.isEmpty)) return EnumActionResult.FAIL;
        ItemStack curItem = player.inventory.getCurrentItem();
        if ((curItem != null) && (curItem.getItem() == BlocksAndItems.itemProbe) && (curItem.getItemDamage() >= 1)) {
            Item item = BlocksAndItems.itemEmptyTrap;

            switch (this.trapType) {
                case 1:
                    item = BlocksAndItems.itemFlameTrap;
                    break;
                case 2:
                    item = BlocksAndItems.itemRiftTrap;
                    break;
                case 3:
                    item = BlocksAndItems.itemPoisonTrap;
                    break;
                default:
                    break;
            }

            EntityItem entityitem = new EntityItem(this.world, this.posX, this.posY, this.posZ, new ItemStack(item, 1));
            entityitem.setPickupDelay(5);
            this.world.spawnEntity(entityitem);
            this.setDead();
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.FAIL;
    }

    public boolean isEmpty() {
        return this.isEmpty;
    }

    public int getTrapType() {
        return this.trapType;
    }

    public boolean isValidPlacement() {
        return (this.world.getBlockState(new BlockPos(MathHelper.floor(this.posX),
                MathHelper.floor(this.posY) - 1,
                MathHelper.floor(this.posZ))).isNormalCube() && (this.world.getEntitiesWithinAABB(
                EntityIMTrap.class, this.getEntityBoundingBox()).size() < 2));
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public void entityInit() {
    }

    // @Override
    // public float getShadowSize() {
    // return 0.0F;
    // }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {
        this.isEmpty = nbttagcompound.getBoolean("isEmpty");
        this.trapType = nbttagcompound.getInteger("type");
        //this.dataWatcher.updateObject(31, Byte.valueOf((byte) (this.isEmpty ? 0 : 1)));
        //this.dataWatcher.updateObject(30, Integer.valueOf(this.trapType));
        this.getDataManager().set(IS_EMPTY, this.isEmpty);
        this.getDataManager().set(TRAP_TYPE, this.trapType);
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {
        nbttagcompound.setBoolean("isEmpty", this.isEmpty);
        nbttagcompound.setInteger("type", this.trapType);
    }

    @Override
    public double getYOffset() {
        return 0.0F;
    }

    private void setEmpty() {
        this.isEmpty = true;
        this.ticks = 0;
        //this.dataWatcher.updateObject(31, Byte.valueOf((byte) (this.isEmpty ? 0 : 1)));
        //this.dataWatcher.updateObject(29, Byte.valueOf((byte) (this.dataWatcher.getWatchableObjectByte(29) == 0 ? 1 : 0)));
        this.getDataManager().set(IS_EMPTY, this.isEmpty);
        this.getDataManager().set(META_CHANGED, (byte) (this.getDataManager().get(META_CHANGED) == 0 ? 1 : 0));
    }

    private void doFireball(float size, int initialDamage) {
        int x = MathHelper.floor(this.posX);
        int y = MathHelper.floor(this.posY);
        int z = MathHelper.floor(this.posZ);
        int min = 0 - (int) size;
        int max = 0 + (int) size;
        for (int i = min; i <= max; i++) {
            for (int j = min; j <= max; j++) {
                for (int k = min; k <= max; k++) {
                    if ((this.world.isAirBlock(new BlockPos(x + i, y + j, z + k)))
                            || (this.world.getBlockState(new BlockPos(x + i, y + j, z + k)).getMaterial().getCanBurn())) {
                        this.world.setBlockState(new BlockPos(x + i, y + j,
                                z + k), Blocks.FIRE.getDefaultState());
                    }
                }
            }
        }

        List<Entity> entities = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().expand(size, size, size));
        for (Entity entity : entities) {
            entity.setFire(8);
            entity.attackEntityFrom(DamageSource.ON_FIRE, initialDamage);
        }
    }

    private void doRiftParticles() {
        for (int i = 0; i < 300; i++) {
            float x = this.rand.nextFloat() * 6.0F - 3.0F;
            float z = this.rand.nextFloat() * 6.0F - 3.0F;
            this.world.spawnParticle(EnumParticleTypes.PORTAL,
                    this.posX + x, this.posY + 2.0D, this.posZ + z, -x / 3.0F,
                    -2.0D, -z / 3.0F);
        }
    }
}