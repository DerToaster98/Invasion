package invmod.common.entity;

import invmod.common.mod_Invasion;
import invmod.common.nexus.TileEntityNexus;
import invmod.common.util.ExplosionUtil;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class EntityIMBoulder extends Entity
{
    private int xTile;
    private int yTile;
    private int zTile;
    private int inTile;
    private int inData;
    private boolean inGround;
    private int life;
    public boolean doesArrowBelongToPlayer;
    public int arrowShake;
    public EntityLivingBase shootingEntity;
    private int ticksInGround;
    private int ticksInAir;
    public boolean arrowCritical;

    public EntityIMBoulder(World world)
    {
        super(world);
        this.xTile = -1;
        this.yTile = -1;
        this.zTile = -1;
        this.inTile = 0;
        this.inData = 0;
        this.life = 60;
        this.inGround = false;
        this.doesArrowBelongToPlayer = false;
        this.arrowShake = 0;
        this.ticksInAir = 0;
        this.arrowCritical = false;
        setSize(0.5F, 0.5F);
    }

    public EntityIMBoulder(World world, double d, double d1, double d2)
    {
        super(world);
        this.xTile = -1;
        this.yTile = -1;
        this.zTile = -1;
        this.inTile = 0;
        this.inData = 0;
        this.life = 60;
        this.inGround = false;
        this.doesArrowBelongToPlayer = false;
        this.arrowShake = 0;
        this.ticksInAir = 0;
        this.arrowCritical = false;
        setSize(0.5F, 0.5F);
        setPosition(d, d1, d2);
        this.yOffset = 0.0F;
    }

    public EntityIMBoulder(World world, EntityLivingBase entityliving, float f)
    {
        super(world);
        this.xTile = -1;
        this.yTile = -1;
        this.zTile = -1;
        this.inTile = 0;
        this.inData = 0;
        this.life = 60;
        this.inGround = false;
        this.doesArrowBelongToPlayer = false;
        this.arrowShake = 0;
        this.ticksInAir = 0;
        this.arrowCritical = false;
        this.shootingEntity = entityliving;
        this.doesArrowBelongToPlayer = (entityliving instanceof EntityPlayer);
        setSize(0.5F, 0.5F);
        setLocationAndAngles(entityliving.posX, entityliving.posY + entityliving.getEyeHeight(), entityliving.posZ, entityliving.rotationYaw, entityliving.rotationPitch);
        this.posX -= MathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI) * 0.16F;
        this.posY -= 0.1D;
        this.posZ -= MathHelper.sin(this.rotationYaw / 180.0F * (float)Math.PI) * 0.16F;
        setPosition(this.posX, this.posY, this.posZ);
        this.yOffset = 0.0F;
        this.motionX = (-MathHelper.sin(this.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI));
        this.motionZ = (MathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI));
        this.motionY = (-MathHelper.sin(this.rotationPitch / 180.0F * (float)Math.PI));
        setBoulderHeading(this.motionX, this.motionY, this.motionZ, f, 1.0F);
    }

    protected void entityInit()
    {
    }

    public void setBoulderHeading(double x, double y, double z, float speed, float variance)
    {
        float distance = MathHelper.sqrt_double(x * x + y * y + z * z);
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
        float xzDistance = MathHelper.sqrt_double(x * x + z * z);
        this.prevRotationYaw = (this.rotationYaw = (float)(Math.atan2(x, z) * 180.0D / Math.PI));
        this.prevRotationPitch = (this.rotationPitch = (float)(Math.atan2(y, xzDistance) * 180.0D / Math.PI));
        this.ticksInGround = 0;
    }

    public void setVelocity(double d, double d1, double d2)
    {
        this.motionX = d;
        this.motionY = d1;
        this.motionZ = d2;

        if ((this.prevRotationPitch == 0.0F) && (this.prevRotationYaw == 0.0F))
        {
            float f = MathHelper.sqrt_double(d * d + d2 * d2);
            this.prevRotationYaw = (this.rotationYaw = (float)(Math.atan2(d, d2) * 180.0D / Math.PI));
            this.prevRotationPitch = (this.rotationPitch = (float)(Math.atan2(d1, f) * 180.0D / Math.PI));
            this.prevRotationPitch = this.rotationPitch;
            this.prevRotationYaw = this.rotationYaw;
            setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
            this.ticksInGround = 0;
        }
    }

    public void onUpdate()
    {
        super.onUpdate();

        if ((this.prevRotationPitch == 0.0F) && (this.prevRotationYaw == 0.0F))
        {
            float f = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.prevRotationYaw = (this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI));
            this.prevRotationPitch = (this.rotationPitch = (float)(Math.atan2(this.motionY, f) * 180.0D / Math.PI));
        }

        int i = this.worldObj.getBlockId(this.xTile, this.yTile, this.zTile);

        if (i > 0)
        {
            Block.blocksList[i].setBlockBoundsBasedOnState(this.worldObj, this.xTile, this.yTile, this.zTile);
            AxisAlignedBB axisalignedbb = Block.blocksList[i].getCollisionBoundingBoxFromPool(this.worldObj, this.xTile, this.yTile, this.zTile);

            if ((axisalignedbb != null) && (axisalignedbb.isVecInside(Vec3.createVectorHelper(this.posX, this.posY, this.posZ))))
            {
                this.inGround = true;
            }
        }

        if ((this.inGround) || (this.life-- <= 0))
        {
            setDead();
            return;
        }

        this.ticksInAir += 1;
        Vec3 vec3d = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
        Vec3 vec3d1 = Vec3.createVectorHelper(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        MovingObjectPosition movingobjectposition = this.worldObj.rayTraceBlocks_do_do(vec3d, vec3d1, false, true);
        vec3d = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
        vec3d1 = Vec3.createVectorHelper(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

        if (movingobjectposition != null)
        {
            vec3d1 = Vec3.createVectorHelper(movingobjectposition.hitVec.xCoord, movingobjectposition.hitVec.yCoord, movingobjectposition.hitVec.zCoord);
        }

        Entity entity = null;
        List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));
        double d = 0.0D;

        for (int l = 0; l < list.size(); l++)
        {
            Entity entity1 = (Entity)list.get(l);

            if ((entity1.canBeCollidedWith()) && ((entity1 != this.shootingEntity) || (this.ticksInAir >= 5)))
            {
                float f5 = 0.3F;
                AxisAlignedBB axisalignedbb1 = entity1.boundingBox.expand(f5, f5, f5);
                MovingObjectPosition movingobjectposition1 = axisalignedbb1.calculateIntercept(vec3d, vec3d1);

                if (movingobjectposition1 != null)
                {
                    double d1 = vec3d.distanceTo(movingobjectposition1.hitVec);

                    if ((d1 < d) || (d == 0.0D))
                    {
                        entity = entity1;
                        d = d1;
                    }
                }
            }
        }

        if (entity != null)
        {
            movingobjectposition = new MovingObjectPosition(entity);
        }

        if (movingobjectposition != null)
        {
            if (movingobjectposition.entityHit != null)
            {
                int damage = (int)(Math.max(this.ticksInAir / 20.0F, 1.0F) * 6.0F);

                if (damage > 14)
                {
                    damage = 14;
                }

                if (movingobjectposition.entityHit.attackEntityFrom(DamageSource.causeMobDamage(this.shootingEntity), damage))
                {
                    if ((movingobjectposition.entityHit instanceof EntityLiving))
                    {
                        if (!this.worldObj.isRemote)
                        {
                            EntityLiving entityLiving = (EntityLiving)movingobjectposition.entityHit;
                            entityLiving.setArrowCountInEntity(entityLiving.getArrowCountInEntity() + 1);
                        }
                    }

                    this.worldObj.playSoundAtEntity(this, "random.explode", 1.0F, 0.9F / (this.rand.nextFloat() * 0.2F + 0.9F));
                    setDead();
                }
            }
            else
            {
                this.xTile = movingobjectposition.blockX;
                this.yTile = movingobjectposition.blockY;
                this.zTile = movingobjectposition.blockZ;
                this.inTile = this.worldObj.getBlockId(this.xTile, this.yTile, this.zTile);
                this.inData = this.worldObj.getBlockMetadata(this.xTile, this.yTile, this.zTile);
                this.motionX = ((float)(movingobjectposition.hitVec.xCoord - this.posX));
                this.motionY = ((float)(movingobjectposition.hitVec.yCoord - this.posY));
                this.motionZ = ((float)(movingobjectposition.hitVec.zCoord - this.posZ));
                float f2 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
                this.posX -= this.motionX / f2 * 0.05D;
                this.posY -= this.motionY / f2 * 0.05D;
                this.posZ -= this.motionZ / f2 * 0.05D;
                this.worldObj.playSoundAtEntity(this, "random.explode", 1.0F, 0.9F / (this.rand.nextFloat() * 0.2F + 0.9F));
                this.inGround = true;
                this.arrowCritical = false;
                int id = this.worldObj.getBlockId(this.xTile, this.yTile, this.zTile);
                Block block = Block.blocksList[id];

                if (id == mod_Invasion.blockNexus.blockID)
                {
                    TileEntityNexus tileEntityNexus = (TileEntityNexus)this.worldObj.getBlockTileEntity(this.xTile, this.yTile, this.zTile);

                    if (tileEntityNexus != null)
                    {
                        tileEntityNexus.attackNexus(2);
                    }
                }
                else if (id != Block.bedrock.blockID)
                {
                    if ((block != null) && (id != mod_Invasion.blockNexus.blockID) && (id != 54))
                    {
                        if ((EntityIMLiving.getBlockSpecial(id) == BlockSpecial.DEFLECTION_1) && (this.rand.nextInt(2) == 0))
                        {
                            setDead();
                            return;
                        }

                        if (!this.worldObj.isRemote)
                        {
                            //check if mobgriefing is enabled
                            boolean mobgriefing = this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing");
                            Explosion explosion = new Explosion(this.worldObj, this, this.xTile, this.yTile, this.zTile, 0.5F);
                            explosion.isFlaming = false;
                            explosion.isSmoking = mobgriefing;
                            explosion.doExplosionA();
                            ExplosionUtil.doExplosionB(this.worldObj, explosion, false);
                        }
                    }
                }
            }
        }

        if (this.arrowCritical)
        {
            for (int i1 = 0; i1 < 4; i1++)
            {
                this.worldObj.spawnParticle("crit", this.posX + this.motionX * i1 / 4.0D, this.posY + this.motionY * i1 / 4.0D, this.posZ + this.motionZ * i1 / 4.0D, -this.motionX, -this.motionY + 0.2D, -this.motionZ);
            }
        }

        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;
        float xyVelocity = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
        this.rotationYaw = ((float)(Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI));

        for (this.rotationPitch = ((float)(Math.atan2(this.motionY, xyVelocity) * 180.0D / Math.PI)); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F);

        while (this.rotationPitch - this.prevRotationPitch >= 180.0F)
        {
            this.prevRotationPitch += 360.0F;
        }

        while (this.rotationYaw - this.prevRotationYaw < -180.0F)
        {
            this.prevRotationYaw -= 360.0F;
        }

        while (this.rotationYaw - this.prevRotationYaw >= 180.0F)
        {
            this.prevRotationYaw += 360.0F;
        }

        this.rotationPitch = (this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F);
        this.rotationYaw = (this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F);
        float airResistance = 1.0F;
        float gravityAcel = 0.025F;

        if (isInWater())
        {
            for (int k1 = 0; k1 < 4; k1++)
            {
                float f7 = 0.25F;
                this.worldObj.spawnParticle("bubble", this.posX - this.motionX * f7, this.posY - this.motionY * f7, this.posZ - this.motionZ * f7, this.motionX, this.motionY, this.motionZ);
            }

            airResistance = 0.8F;
        }

        this.motionX *= airResistance;
        this.motionY *= airResistance;
        this.motionZ *= airResistance;
        this.motionY -= gravityAcel;
        setPosition(this.posX, this.posY, this.posZ);
    }

    public void writeEntityToNBT(NBTTagCompound nbttagcompound)
    {
        nbttagcompound.setShort("xTile", (short)this.xTile);
        nbttagcompound.setShort("yTile", (short)this.yTile);
        nbttagcompound.setShort("zTile", (short)this.zTile);
        nbttagcompound.setByte("inTile", (byte)this.inTile);
        nbttagcompound.setByte("inData", (byte)this.inData);
        nbttagcompound.setByte("shake", (byte)this.arrowShake);
        nbttagcompound.setByte("inGround", (byte)(this.inGround ? 1 : 0));
        nbttagcompound.setBoolean("player", this.doesArrowBelongToPlayer);
    }

    public void readEntityFromNBT(NBTTagCompound nbttagcompound)
    {
        this.xTile = nbttagcompound.getShort("xTile");
        this.yTile = nbttagcompound.getShort("yTile");
        this.zTile = nbttagcompound.getShort("zTile");
        this.inTile = (nbttagcompound.getByte("inTile") & 0xFF);
        this.inData = (nbttagcompound.getByte("inData") & 0xFF);
        this.arrowShake = (nbttagcompound.getByte("shake") & 0xFF);
        this.inGround = (nbttagcompound.getByte("inGround") == 1);
        this.doesArrowBelongToPlayer = nbttagcompound.getBoolean("player");
    }

    public void onCollideWithPlayer(EntityPlayer entityplayer)
    {
        if (this.worldObj.isRemote);
    }

    public float getShadowSize()
    {
        return 0.0F;
    }

    public int getFlightTime()
    {
        return this.ticksInAir;
    }
}