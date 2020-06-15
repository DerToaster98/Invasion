package invasion.client.render;

import invasion.Reference;
import invasion.client.render.model.BigBipedModel;
import invasion.entity.monster.InvadingZombieEntity;
import net.minecraft.client.model.ModelZombie;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;


public class RenderIMZombie extends RenderLiving<InvadingZombieEntity> {
    private static final ResourceLocation t_old = new ResourceLocation(Reference.MODID + ":textures/zombie_old.png");
    private static final ResourceLocation t_T1a = new ResourceLocation(Reference.MODID + ":textures/zombieT1a.png");
    private static final ResourceLocation t_pig = new ResourceLocation(Reference.MODID + ":textures/pigzombie64x32.png");
    private static final ResourceLocation t_T2 = new ResourceLocation(Reference.MODID + ":textures/zombieT2.png");
    private static final ResourceLocation t_T2a = new ResourceLocation(Reference.MODID + ":textures/zombieT2a.png");
    private static final ResourceLocation t_T3 = new ResourceLocation(Reference.MODID + ":textures/zombieT3.png");
    private static final ResourceLocation t_tar = new ResourceLocation(Reference.MODID + ":textures/zombietar.png");
    protected BigBipedModel bigBipedModel;

    public RenderIMZombie(RenderManager renderManager) {
        super(renderManager, new ModelZombie(0.0F, true) {
            //DarthXenon: This fixes the bug that makes mobs moves their limbs really fast, but I get the feeling that there is a better way to handle this. Oh well, as long as it works, I suppose.
            @Override
            public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
                super.setRotationAngles(limbSwing / 3f, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
            }
        }, 0.5F);
        this.bigBipedModel = new BigBipedModel();
        this.addLayer(new LayerHeldItem(this));
        this.addLayer(new LayerBipedArmor(this) {
            @Override
            protected void initArmor() {
                this.modelLeggings = new ModelZombie(0.5F, true);
                this.modelArmor = new ModelZombie(1.0F, true);
            }
        });
    }

    @Override
    public void doRender(InvadingZombieEntity entityLiving, double x, double y, double z, float yaw, float partialTicks) {
//		if ((entity instanceof EntityIMZombie)) {
//			if (((EntityIMZombie) entity).isBigRenderTempHack()) {
//				this.mainModel = this.modelBigBiped;
//				this.modelBigBiped.setSneaking(entity.isSneaking());
//			} else {
//				this.mainModel = this.modelBiped;
//			}
        //this.mainModel.setLivingAnimations(entityLiving, 0.0F, 0.0F, 0.0F);
        super.doRender(entityLiving, x, y, z, yaw, partialTicks);
//			super.doRender(entity, par2, par4, par6, par8, par9);
//		}
    }

    @Override
    protected void preRenderCallback(InvadingZombieEntity par1EntityLiving, float par2) {
        float f = par1EntityLiving.scaleAmount();
        GL11.glScalef(f, (2.0F + f) / 3.0F, f);
    }
//	@Override
//	protected void renderEquippedItems(EntityLivingBase entity, float par2) {
//		super.renderEquippedItems(entity, par2);
//		ItemStack itemstack = entity.getHeldItem();
//
//		if (itemstack != null) {
//			GL11.glPushMatrix();
//			if (((EntityIMZombie) entity).isBigRenderTempHack())
//				this.modelBigBiped.itemArmPostRender(0.0625F);
//			else {
//				this.modelBiped.bipedRightArm.postRender(0.0625F);
//			}
//			GL11.glTranslatef(-0.0625F, 0.4375F, 0.0625F);
//
//			if (RenderBlocks.renderItemIn3d(Block.getBlockFromItem(itemstack.getItem()).getRenderType())) {
//				float f = 0.5F;
//				GL11.glTranslatef(0.0F, 0.1875F, -0.3125F);
//				f *= 0.75F;
//				GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
//				GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
//				GL11.glScalef(f, -f, f);
//			} else if (itemstack.getItem() == Items.bow) {
//				float f1 = 0.625F;
//				GL11.glTranslatef(0.0F, 0.125F, 0.3125F);
//				GL11.glRotatef(-20.0F, 0.0F, 1.0F, 0.0F);
//				GL11.glScalef(f1, -f1, f1);
//				GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
//				GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
//			} else if (itemstack.getItem().isFull3D()) {
//				float f2 = 0.625F;
//				GL11.glTranslatef(0.0F, 0.1875F, 0.0F);
//				GL11.glScalef(f2, -f2, f2);
//				GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
//				GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
//			} else {
//				float f3 = 0.375F;
//				GL11.glTranslatef(0.25F, 0.1875F, -0.1875F);
//				GL11.glScalef(f3, f3, f3);
//				GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
//				GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
//				GL11.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
//			}
//
//			this.renderManager.itemRenderer.renderItem(entity, itemstack, 0);
//
//			if (itemstack.getItem().requiresMultipleRenderPasses()) {
//				for (int x = 1; x < itemstack.getItem().getRenderPasses(itemstack.getItemDamage()); x++) {
//					this.renderManager.itemRenderer.renderItem(entity, itemstack, x);
//				}
//			}
//
//			GL11.glPopMatrix();
//		}
//	}

    @Override
    protected ResourceLocation getEntityTexture(InvadingZombieEntity entity) {
        switch (entity.getTextureId()) {
            case 0:
                return t_old;
            case 1:
                return t_T1a;
            case 2:
                return t_T2;
            case 3:
                return t_pig;
            case 4:
                return t_T2a;
            case 5:
                return t_tar;
            case 6:
                return t_T3;
        }
        return t_old;
    }
}