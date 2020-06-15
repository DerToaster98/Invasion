package invasion.client.render.model;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public class BigBipedModel<E extends LivingEntity> extends BipedModel<E> {

    public BigBipedModel() {
        super(1.0f, 0.0f, 64, 32);

        bipedHead = new ModelRenderer(this, 0, 0);
        bipedHead.addBox(-3.533333F, -7.0F, -3.5F, 7, 7, 7);
        bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
        bipedHead.setTextureSize(textureWidth, textureHeight);
        bipedBody = new ModelRenderer(this, 16, 15);
        bipedBody.addBox(-5.0F, 0.0F, -3.0F, 10, 12, 5);
        bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
        bipedBody.setTextureSize(textureWidth, textureHeight);
        bipedRightArm = new ModelRenderer(this, 46, 15);
        bipedRightArm.addBox(-3.0F, -2.0F, -2.0F, 4, 13, 4);
        bipedRightArm.setRotationPoint(-6.0F, 2.0F, 0.0F);
        bipedRightArm.setTextureSize(textureWidth, textureHeight);
        bipedLeftArm = new ModelRenderer(this, 46, 15);
        bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 13, 4);
        bipedLeftArm.setRotationPoint(6.0F, 2.0F, 0.0F);
        bipedLeftArm.setTextureSize(textureWidth, textureHeight);
        bipedRightLeg = new ModelRenderer(this, 0, 16);
        bipedRightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4);
        bipedRightLeg.setRotationPoint(-2.0F, 12.0F, 0.0F);
        bipedRightLeg.setTextureSize(textureWidth, textureHeight);
        bipedLeftLeg = new ModelRenderer(this, 0, 16);
        bipedLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4);
        bipedLeftLeg.setRotationPoint(2.0F, 12.0F, 0.0F);
        bipedLeftLeg.setTextureSize(textureWidth, textureHeight);

        bipedHeadwear = new ModelRenderer(this, 32, 0);
        bipedHeadwear.addBox(-3.533333F, -7.0F, -3.5F, 7, 7, 7, 0.5F);
        bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
    }

    public void setSneaking(boolean flag) {
        //TODO
    }

/*
    @Override
    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity entity) {
        bipedHead.rotateAngleY = (par4 / 57.295776F);
        bipedHead.rotateAngleX = (par5 / 57.295776F);
        bipedHeadwear.rotateAngleY = bipedHead.rotateAngleY;
        bipedHeadwear.rotateAngleX = bipedHead.rotateAngleX;
        bipedRightArm.rotateAngleX = (MathHelper.cos(par1 * 0.6662F + 3.141593F) * 2.0F * par2 * 0.5F);
        bipedLeftArm.rotateAngleX = (MathHelper.cos(par1 * 0.6662F) * 2.0F * par2 * 0.5F);
        bipedRightArm.rotateAngleZ = 0.0F;
        bipedLeftArm.rotateAngleZ = 0.0F;
        bipedRightLeg.rotateAngleX = (MathHelper.cos(par1 * 0.6662F) * 1.4F * par2);
        bipedLeftLeg.rotateAngleX = (MathHelper.cos(par1 * 0.6662F + 3.141593F) * 1.4F * par2);
        bipedRightLeg.rotateAngleY = 0.0F;
        bipedLeftLeg.rotateAngleY = 0.0F;

        if (isRiding) {
            bipedRightArm.rotateAngleX += -0.6283186F;
            bipedLeftArm.rotateAngleX += -0.6283186F;
            bipedRightLeg.rotateAngleX = -1.256637F;
            bipedLeftLeg.rotateAngleX = -1.256637F;
            bipedRightLeg.rotateAngleY = 0.3141593F;
            bipedLeftLeg.rotateAngleY = -0.3141593F;
        }

        if (heldItemLeft != 0) {
            bipedLeftArm.rotateAngleX = (bipedLeftArm.rotateAngleX * 0.5F - 0.3141593F * heldItemLeft);
        }

        if (heldItemRight != 0) {
            bipedRightArm.rotateAngleX = (bipedRightArm.rotateAngleX * 0.5F - 0.3141593F * heldItemRight);
        }

        bipedRightArm.rotateAngleY = 0.0F;
        bipedLeftArm.rotateAngleY = 0.0F;
        //TODO: Removed some logic by changing OnGround
        if (entity.onGround) {
            float f = 0.0F;
            bipedBody.rotateAngleY = (MathHelper.sin(MathHelper.sqrt(f) * 3.141593F * 2.0F) * 0.2F);
            bipedRightArm.rotationPointZ = (MathHelper.sin(bipedBody.rotateAngleY) * 5.0F);
            bipedRightArm.rotationPointX = (-MathHelper.cos(bipedBody.rotateAngleY) * 5.0F);
            bipedLeftArm.rotationPointZ = (-MathHelper.sin(bipedBody.rotateAngleY) * 5.0F);
            bipedLeftArm.rotationPointX = (MathHelper.cos(bipedBody.rotateAngleY) * 5.0F);
            bipedRightArm.rotateAngleY += bipedBody.rotateAngleY;
            bipedLeftArm.rotateAngleY += bipedBody.rotateAngleY;
            bipedLeftArm.rotateAngleX += bipedBody.rotateAngleY;
            f = 1.0F;
            f *= f;
            f *= f;
            f = 1.0F - f;
            float f2 = MathHelper.sin(f * 3.141593F);
            float f4 = MathHelper.sin(3.141593F) * -(bipedHead.rotateAngleX - 0.7F) * 0.75F;
            ModelRenderer tmp570_567 = bipedRightArm;
            tmp570_567.rotateAngleX = ((float) (tmp570_567.rotateAngleX - (f2 * 1.2D + f4)));
            bipedRightArm.rotateAngleY += bipedBody.rotateAngleY * 2.0F;
            bipedRightArm.rotateAngleZ = (MathHelper.sin(3.141593F) * -0.4F);
        }

        if (isSneaking) {
            bipedBody.rotateAngleX = 0.7F;
            bipedBody.rotationPointY = 1.5F;
            bipedRightLeg.rotateAngleX -= 0.0F;
            bipedLeftLeg.rotateAngleX -= 0.0F;
            bipedRightArm.rotateAngleX += 0.4F;
            bipedLeftArm.rotateAngleX += 0.4F;
            bipedRightLeg.rotationPointZ = 7.0F;
            bipedLeftLeg.rotationPointZ = 7.0F;
            bipedRightLeg.rotationPointY = 12.0F;
            bipedLeftLeg.rotationPointY = 12.0F;
            bipedRightArm.rotationPointY = 3.5F;
            bipedLeftArm.rotationPointY = 3.5F;
            bipedHead.rotationPointY = 3.0F;
        } else {
            bipedBody.rotateAngleX = 0.0F;
            bipedBody.rotationPointY = 0.0F;
            bipedRightLeg.rotationPointZ = 0.0F;
            bipedLeftLeg.rotationPointZ = 0.0F;
            bipedRightLeg.rotationPointY = 12.0F;
            bipedLeftLeg.rotationPointY = 12.0F;
            bipedRightArm.rotationPointY = 2.0F;
            bipedLeftArm.rotationPointY = 2.0F;
            bipedHead.rotationPointY = 0.0F;
            bipedRightArm.rotationPointX = -6.0F;
            bipedLeftArm.rotationPointX = 6.0F;
        }

        bipedRightArm.rotateAngleZ += MathHelper.cos(par3 * 0.09F) * 0.05F + 0.05F;
        bipedLeftArm.rotateAngleZ -= MathHelper.cos(par3 * 0.09F) * 0.05F + 0.05F;
        bipedRightArm.rotateAngleX += MathHelper.sin(par3 * 0.067F) * 0.05F;
        bipedLeftArm.rotateAngleX -= MathHelper.sin(par3 * 0.067F) * 0.05F;

        if (aimedBow) {
            float f1 = 0.0F;
            float f3 = 0.0F;
            bipedRightArm.rotateAngleZ = 0.0F;
            bipedLeftArm.rotateAngleZ = 0.0F;
            bipedRightArm.rotateAngleY = (-(0.1F - f1 * 0.6F) + bipedHead.rotateAngleY);
            bipedLeftArm.rotateAngleY = (0.1F - f1 * 0.6F + bipedHead.rotateAngleY + 0.4F);
            bipedRightArm.rotateAngleX = (-1.570796F + bipedHead.rotateAngleX);
            bipedLeftArm.rotateAngleX = (-1.570796F + bipedHead.rotateAngleX);
            bipedRightArm.rotateAngleX -= f1 * 1.2F - f3 * 0.4F;
            bipedLeftArm.rotateAngleX -= f1 * 1.2F - f3 * 0.4F;
            bipedRightArm.rotateAngleZ += MathHelper.cos(par3 * 0.09F) * 0.05F + 0.05F;
            bipedLeftArm.rotateAngleZ -= MathHelper.cos(par3 * 0.09F) * 0.05F + 0.05F;
            bipedRightArm.rotateAngleX += MathHelper.sin(par3 * 0.067F) * 0.05F;
            bipedLeftArm.rotateAngleX -= MathHelper.sin(par3 * 0.067F) * 0.05F;
        }
    }

 */
/*
    public void itemArmPostRender(float scale) {
        bipedRightArm.postRender(scale);
    }

 */
}