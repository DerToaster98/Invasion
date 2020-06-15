package invasion.entity.projectile;

import invasion.init.ModBlocks;
import invasion.init.ModEntityTypes;
import invasion.nexus.Nexus;
import invasion.tileentity.NexusTileEntity;
import invasion.util.MathUtil;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.*;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import java.util.List;


public class BoulderEntity extends ThrowableEntity {
   // private int xTile;
   // private int yTile;
   // private int zTile;
   // private Block inTile;
   // private int inData;
   // private boolean inGround;
    private int life = 60;
   // public boolean doesArrowBelongToPlayer;
  //  public int arrowShake;
  //  public EntityLivingBase shootingEntity;
  //  private int ticksInGround;
    private int ticksInAir;
    public boolean arrowCritical;

    public BoulderEntity(World worldIn) {
        super(ModEntityTypes.BOULDER, worldIn);
    }

    public BoulderEntity(double x, double y, double z, World worldIn) {
        super(ModEntityTypes.BOULDER, x, y, z, worldIn);
    }

    public BoulderEntity(LivingEntity livingEntityIn, World worldIn) {
        super(ModEntityTypes.BOULDER, livingEntityIn, worldIn);
    }

    // USE shoot instead
    /*
    public void setBoulderHeading(double x, double y, double z, float speed,
                                  float variance) {
        float distance = MathHelper.sqrt(x * x + y * y + z * z);
        x /= distance;
        y /= distance;
        z /= distance;

        x += this.rand.nextGaussian() * variance;
        y += this.rand.nextGaussian() * variance;
        z += this.rand.nextGaussian() * variance;
        x *= speed;
        y *= speed;
        z *= speed;
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;
        float xzDistance = MathHelper.sqrt(x * x + z * z);
        this.prevRotationYaw = (this.rotationYaw = (float) (Math.atan2(x, z) * 180.0D / 3.141592653589793D));
        this.prevRotationPitch = (this.rotationPitch = (float) (Math.atan2(y,
                xzDistance) * 180.0D / 3.141592653589793D));
        this.ticksInGround = 0;
    }

     */

    @Override
    protected void registerData() {}

   /* @Override
    public void setVelocity(double dx, double dy, double dz) {
       setMotion(dx,dy,dz);
       if ((this.prevRotationPitch == 0.0F) && (this.prevRotationYaw == 0.0F)) {
            float f = MathHelper.sqrt(d * d + d2 * d2);
            this.prevRotationYaw = (this.rotationYaw = (float) (Math.atan2(d,
                    d2) * 180.0D / 3.141592741012573D));
            this.prevRotationPitch = (this.rotationPitch = (float) (Math.atan2(
                    d1, f) * 180.0D / 3.141592741012573D));
            this.prevRotationPitch = this.rotationPitch;
            this.prevRotationYaw = this.rotationYaw;
            this.setLocationAndAngles(this.posX, this.posY, this.posZ,
                    this.rotationYaw, this.rotationPitch);
        }
    }*/

    @Override
    public void tick() {
        super.tick();
        if ((this.prevRotationPitch == 0.0F) && (this.prevRotationYaw == 0.0F)) {
            float f = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.prevRotationYaw = (this.rotationYaw = (float) (Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI));
            this.prevRotationPitch = (this.rotationPitch = (float) (Math.atan2(this.motionY, f) * 180.0D / Math.PI));
        }

        IBlockState blockState = this.world.getBlockState(new BlockPos(this.xTile, this.yTile, this.zTile));
        if (blockState.getBlock() != Blocks.AIR) {
            //block.setBlockBoundsBasedOnState(this.world, new BlockPos(this.xTile, this.yTile, this.zTile));
            //AxisAlignedBB axisalignedbb = block.getSelectedBoundingBox(this.world.getBlockState(new BlockPos(this.xTile, this.yTile, this.zTile)),this.world, new BlockPos(this.xTile, this.yTile, this.zTile));
            AxisAlignedBB axisalignedbb = blockState.getBlock().getBoundingBox(blockState, this.world, new BlockPos(this.xTile, this.yTile, this.zTile));
            if ((axisalignedbb != null) && (axisalignedbb.contains(new Vec3d(this.posX, this.posY, this.posZ)))) {
                this.inGround = true;
            }

        }

        if ((this.inGround) || (this.life-- <= 0)) {
            this.setDead();
            return;
        }

        this.ticksInAir += 1;

        Vec3d vec3d = new Vec3d(this.posX, this.posY, this.posZ);
        Vec3d vec3d1 = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        // after update required one less vec, chose false, could also be true
        RayTraceResult rtr0 = this.world.rayTraceBlocks(vec3d, vec3d1, false);
        vec3d = new Vec3d(this.posX, this.posY, this.posZ);
        vec3d1 = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        if (rtr0 != null) vec3d1 = new Vec3d(rtr0.hitVec.x, rtr0.hitVec.y, rtr0.hitVec.z);

        Entity entity = null;
        List list = this.world.getEntitiesWithinAABBExcludingEntity(
                this, this.getEntityBoundingBox().offset(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));
        double d = 0.0D;
        for (int l = 0; l < list.size(); l++) {
            Entity entity1 = (Entity) list.get(l);
            if ((entity1.canBeCollidedWith()) && ((entity1 != this.shootingEntity) || (this.ticksInAir >= 5))) {
                float f5 = 0.3F;
                AxisAlignedBB axisalignedbb1 = entity1.getEntityBoundingBox().expand(f5, f5, f5);
                RayTraceResult rtr = axisalignedbb1.calculateIntercept(vec3d, vec3d1);
                if (rtr != null) {
                    double d1 = vec3d.distanceTo(rtr.hitVec);
                    if ((d1 < d) || (d == 0.0D)) {
                        entity = entity1;
                        d = d1;
                    }
                }
            }
        }
        if (entity != null) rtr0 = new RayTraceResult(entity);
        /*if (rtr0 != null) {
            if (rtr0.entityHit != null) {
                int damage = (int) (Math.max(this.ticksInAir / 20.0F, 1.0F) * 6.0F);
                if (damage > 14) damage = 14;
                if (rtr0.entityHit.attackEntityFrom(
                        DamageSource.causeMobDamage(this.shootingEntity), damage)) {
                    if ((rtr0.entityHit instanceof EntityLiving)) {
                        if (!this.world.isRemote) {
                            EntityLiving entityLiving = (EntityLiving) rtr0.entityHit;
                            entityLiving.setArrowCountInEntity(entityLiving.getArrowCountInEntity() + 1);
                        }
                    }
                    //this.world.playSoundAtEntity(this, "random.explode", 1.0F, 0.9F / (this.rand.nextFloat() * 0.2F + 0.9F));
                    this.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, 1f, 0.9f / (this.rand.nextFloat() * 0.2f + 0.9f));
                    this.setDead();
                }
            } else {
                this.xTile = rtr0.getBlockPos().getX();
                this.yTile = rtr0.getBlockPos().getY();
                this.zTile = rtr0.getBlockPos().getZ();
                this.inTile = this.world.getBlockState(new BlockPos(this.xTile, this.yTile, this.zTile)).getBlock();
//				this.inData = this.world.getBlockMetadata(this.xTile, this.yTile, this.zTile);
                this.motionX = ((float) (rtr0.hitVec.x - this.posX));
                this.motionY = ((float) (rtr0.hitVec.y - this.posY));
                this.motionZ = ((float) (rtr0.hitVec.z - this.posZ));
                float f2 = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
                this.posX -= this.motionX / f2 * 0.05D;
                this.posY -= this.motionY / f2 * 0.05D;
                this.posZ -= this.motionZ / f2 * 0.05D;
                //this.world.playSoundAtEntity(this, "random.explode", 1.0F, 0.9F / (this.rand.nextFloat() * 0.2F + 0.9F));
                this.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, 1f, 0.9f / (this.rand.nextFloat() * 0.2f + 0.9f));
                this.inGround = true;
                this.arrowCritical = false;

                Block block2 = this.world.getBlockState(new BlockPos(this.xTile, this.yTile, this.zTile)).getBlock();
                if (block2 == BlocksAndItems.blockNexus) {
                    Nexus nexus = (Nexus) this.world.getTileEntity(new BlockPos(this.xTile, this.yTile, this.zTile));
                    if (nexus != null) {
                        nexus.attackNexus(2);
                    }
                } else if (block2 != Blocks.BEDROCK) {
                    if ((block2 != null) && (block2 != BlocksAndItems.blockNexus) && (block2 != Blocks.CHEST)) {
                        // check if mobgriefing is enabled
                        boolean mobgriefing = this.world.getGameRules().getBoolean("mobGriefing");

                        // int meta = this.world.getBlockMetadata(this.xTile,
                        // this.yTile, this.zTile);
                        // block=Blocks.air;
                        // block.onBlockDestroyedByPlayer(this.world,
                        // this.xTile, this.yTile, this.zTile, meta);
                        // block.dropBlockAsItem(this.world, this.xTile,
                        // this.yTile, this.zTile, meta, 0);
                        if (!this.world.isRemote) {
                            // this.world.createExplosion(null, this.xTile,
                            // this.yTile, this.zTile, 0.5F, true);

                            Explosion explosion = new Explosion(this.world, this, this.xTile, this.yTile, this.zTile, 2.0F, false, mobgriefing);

                            explosion.doExplosionA();
                            explosion.doExplosionB(false);
                        }

                    }

                }
            }

        }*/

        if (this.arrowCritical) {
            for (int i1 = 0; i1 < 4; i1++) {
                this.world.spawnParticle(EnumParticleTypes.CRIT, this.posX
                                + this.motionX * i1 / 4.0D, this.posY + this.motionY
                                * i1 / 4.0D,
                        this.posZ + this.motionZ * i1 / 4.0D,
                        -this.motionX, -this.motionY + 0.2D, -this.motionZ);
            }

        }

        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;

        float xyVelocity = MathHelper.sqrt(this.motionX * this.motionX
                + this.motionZ * this.motionZ);
        this.rotationYaw = ((float) (Math.atan2(this.motionX, this.motionZ) * 180.0D / 3.141592653589793D));
        for (this.rotationPitch = ((float) (Math
                .atan2(this.motionY, xyVelocity) * 180.0D / 3.141592653589793D)); this.rotationPitch
                     - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F)
            ;
        while (this.rotationPitch - this.prevRotationPitch >= 180.0F)
            this.prevRotationPitch += 360.0F;
        while (this.rotationYaw - this.prevRotationYaw < -180.0F)
            this.prevRotationYaw -= 360.0F;
        while (this.rotationYaw - this.prevRotationYaw >= 180.0F)
            this.prevRotationYaw += 360.0F;
        this.rotationPitch = (this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F);
        this.rotationYaw = (this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F);
        float airResistance = 1.0F;
        float gravityAcel = 0.025F;
        if (this.isInWater()) {
            for (int k1 = 0; k1 < 4; k1++) {
                float f7 = 0.25F;
                this.world.spawnParticle(EnumParticleTypes.WATER_BUBBLE,
                        this.posX - this.motionX * f7, this.posY - this.motionY
                                * f7,
                        this.posZ - this.motionZ * f7,
                        this.motionX, this.motionY, this.motionZ);
            }

            airResistance = 0.8F;
        }
        this.motionX *= airResistance;
        this.motionY *= airResistance;
        this.motionZ *= airResistance;
        this.motionY -= gravityAcel;
        this.setPosition(this.posX, this.posY, this.posZ);
    }


    @Override
    protected void onImpact(RayTraceResult result) {
        if (result.getType() == RayTraceResult.Type.ENTITY) {
            float damage = MathHelper.clamp(ticksInAir * 0.3f,6.0f,14.0f);

            if (rtr0.entityHit.attackEntityFrom(
                    DamageSource.causeMobDamage(this.shootingEntity), damage)) {
                if ((rtr0.entityHit instanceof EntityLiving)) {
                    if (!this.world.isRemote) {
                        EntityLiving entityLiving = (EntityLiving) rtr0.entityHit;
                        entityLiving.setArrowCountInEntity(entityLiving.getArrowCountInEntity() + 1);
                    }
                }
                playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, 1f, 0.9f / (this.rand.nextFloat() * 0.2f + 0.9f));

            }
            remove();
        } else if (result.getType()== RayTraceResult.Type.BLOCK) {

            BlockPos hitPos = null;
            this.xTile = rtr0.getBlockPos().getX();
            this.yTile = rtr0.getBlockPos().getY();
            this.zTile = rtr0.getBlockPos().getZ();
            this.inTile = this.world.getBlockState(new BlockPos(this.xTile, this.yTile, this.zTile)).getBlock();
//				this.inData = this.world.getBlockMetadata(this.xTile, this.yTile, this.zTile);
            this.motionX = ((float) (rtr0.hitVec.x - this.posX));
            this.motionY = ((float) (rtr0.hitVec.y - this.posY));
            this.motionZ = ((float) (rtr0.hitVec.z - this.posZ));
            float f2 = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
            this.posX -= this.motionX / f2 * 0.05D;
            this.posY -= this.motionY / f2 * 0.05D;
            this.posZ -= this.motionZ / f2 * 0.05D;
            //this.world.playSoundAtEntity(this, "random.explode", 1.0F, 0.9F / (this.rand.nextFloat() * 0.2F + 0.9F));
            playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, 1f, 0.9f / (this.rand.nextFloat() * 0.2f + 0.9f));

            Block blockHit = this.world.getBlockState(hitPos).getBlock();
            if (blockHit == ModBlocks.NEXUS.get()) {
                TileEntity nexusTileEntity = (NexusTileEntity) this.world.getTileEntity(hitPos);
                if (nexusTileEntity != null) {
                    // TODO damage nexus with 2
                }
            } else if (blockHit != Blocks.BEDROCK) {
                if (blockHit != ModBlocks.NEXUS.get() && blockHit != Blocks.CHEST) {
                    // check if mobgriefing is enabled
                    boolean mobgriefing = this.world.getGameRules().getBoolean("mobGriefing");

                    // int meta = this.world.getBlockMetadata(this.xTile,
                    // this.yTile, this.zTile);
                    // block=Blocks.air;
                    // block.onBlockDestroyedByPlayer(this.world,
                    // this.xTile, this.yTile, this.zTile, meta);
                    // block.dropBlockAsItem(this.world, this.xTile,
                    // this.yTile, this.zTile, meta, 0);
                    if (!this.world.isRemote) {
                        Explosion explosion = new Explosion(this.world, this, this.xTile, this.yTile, this.zTile, 2.0F, false, mobgriefing);

                        explosion.doExplosionA();
                        explosion.doExplosionB(false);
                    }

                }

            }
        }

    }
//	@Override
//	public float getShadowSize() {
//		return 0.0F;
//	}

    public int getFlightTime() {
        return this.ticksInAir;
    }
}