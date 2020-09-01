package invasion.client.render.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.model.IHasArm;
import net.minecraft.client.renderer.entity.model.IHasHead;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public class ThrowerModel<T extends Entity> extends SegmentedModel<T> implements IHasArm, IHasHead {
    private final ModelRenderer throwerHead;
    private final ModelRenderer throwerBody;
    private final ModelRenderer throwerBody2;
    private final ModelRenderer throwerRightArm;
    private final ModelRenderer throwerLeftArm;
    private final ModelRenderer throwerRightLeg;
    private final ModelRenderer throwerLeftLeg;
    public boolean heldItemLeft;
    public boolean heldItemRight;

    public ThrowerModel() {
        throwerHead = new ModelRenderer(this, 16, 14);
        throwerHead.addBox(-2.0F, -2.0F, -2.0F, 4, 2, 4, 0.0F);
        throwerHead.setRotationPoint(0.0F, 16.0F, 4.0F);
        throwerHead.rotateAngleX = 0.0F;
        throwerHead.rotateAngleY = 0.0F;
        throwerHead.rotateAngleZ = 0.0F;
        throwerHead.mirror = false;
        throwerBody = new ModelRenderer(this, 0, 1);
        throwerBody.addBox(-7.0F, 2.0F, -4.0F, 12, 4, 9, 0.0F);
        throwerBody.setRotationPoint(-0.4F, 16.0F, 3.0F);
        throwerBody.rotateAngleX = 0.0F;
        throwerBody.rotateAngleY = 0.0F;
        throwerBody.rotateAngleZ = 0.0F;
        throwerBody.mirror = false;
        throwerRightArm = new ModelRenderer(this, 39, 22);
        throwerRightArm.addBox(-3.0F, 0.0F, -1.466667F, 3, 7, 3, 0.0F);
        throwerRightArm.setRotationPoint(-6.566667F, 16.0F, 5.0F);
        throwerRightArm.rotateAngleX = 0.0F;
        throwerRightArm.rotateAngleY = 0.0F;
        throwerRightArm.rotateAngleZ = 0.0F;
        throwerRightArm.mirror = false;
        throwerLeftArm = new ModelRenderer(this, 40, 16);
        throwerLeftArm.addBox(0.0F, 0.0F, -1.0F, 2, 4, 2, 0.0F);
        throwerLeftArm.setRotationPoint(5.0F, 16.0F, 5.0F);
        throwerLeftArm.rotateAngleX = 0.0F;
        throwerLeftArm.rotateAngleY = 0.0F;
        throwerLeftArm.rotateAngleZ = 0.0F;
        throwerLeftArm.mirror = false;
        throwerRightLeg = new ModelRenderer(this, 0, 14);
        throwerRightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 2, 4, 0.0F);
        throwerRightLeg.setRotationPoint(-4.066667F, 22.0F, 4.0F);
        throwerRightLeg.rotateAngleX = 0.0F;
        throwerRightLeg.rotateAngleY = 0.0F;
        throwerRightLeg.rotateAngleZ = 0.0F;
        throwerRightLeg.mirror = false;
        throwerLeftLeg = new ModelRenderer(this, 0, 14);
        throwerLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 2, 4, 0.0F);
        throwerLeftLeg.setRotationPoint(3.0F, 22.0F, 4.0F);
        throwerLeftLeg.rotateAngleX = 0.0F;
        throwerLeftLeg.rotateAngleY = 0.0F;
        throwerLeftLeg.rotateAngleZ = 0.0F;
        throwerLeftLeg.mirror = false;
        throwerBody2 = new ModelRenderer(this, 0, 23);
        throwerBody2.addBox(-3.666667F, 0.0F, 0.0F, 12, 2, 7, 0.0F);
        throwerBody2.setRotationPoint(-3.0F, 16.0F, 0.0F);
        throwerBody2.rotateAngleX = 0.0F;
        throwerBody2.rotateAngleY = 0.0F;
        throwerBody2.rotateAngleZ = 0.0F;
        throwerBody2.mirror = false;
    }

    @Override
    public void setRotationAngles(T thrower, float t, float v1, float v2, float headPitch, float headYaw) {
        throwerHead.rotateAngleY = (headPitch / 57.29578F);
        throwerHead.rotateAngleX = (headYaw / 57.29578F);
        throwerRightArm.rotateAngleX = (MathHelper.cos(t * 0.6662F + 3.141593F) * 2.0F * v1 * 0.5F);
        throwerLeftArm.rotateAngleX = (MathHelper.cos(t * 0.6662F) * 2.0F * v1 * 0.5F);
        throwerRightArm.rotateAngleZ = 0.0F;
        throwerLeftArm.rotateAngleZ = 0.0F;
        throwerRightLeg.rotateAngleX = (MathHelper.cos(t * 0.6662F) * 1.4F * v1);
        throwerLeftLeg.rotateAngleX = (MathHelper.cos(t * 0.6662F + 3.141593F) * 1.4F * v1);
        throwerRightLeg.rotateAngleY = 0.0F;
        throwerLeftLeg.rotateAngleY = 0.0F;
        if (false /* TODO if the thrower is riding */) {
            throwerRightArm.rotateAngleX += -0.6283185F;
            throwerLeftArm.rotateAngleX += -0.6283185F;
            throwerRightLeg.rotateAngleX = -1.256637F;
            throwerLeftLeg.rotateAngleX = -1.256637F;
            throwerRightLeg.rotateAngleY = 0.314159F;
            throwerLeftLeg.rotateAngleY = -0.314159F;
        }
        if (heldItemLeft) {
            throwerLeftArm.rotateAngleX = (throwerLeftArm.rotateAngleX * 0.5F - 0.314159F);
        }
        if (heldItemRight) {
            throwerRightArm.rotateAngleX = (throwerRightArm.rotateAngleX * 0.5F - 0.314159F);
        }
        throwerRightArm.rotateAngleY = 0.0F;
        throwerLeftArm.rotateAngleY = 0.0F;

        throwerRightArm.rotateAngleZ += MathHelper.cos(v2 * 0.09F) * 0.05F + 0.05F;
        throwerLeftArm.rotateAngleZ -= MathHelper.cos(v2 * 0.09F) * 0.05F + 0.05F;
        throwerRightArm.rotateAngleX += MathHelper.sin(v2 * 0.067F) * 0.05F;
        throwerLeftArm.rotateAngleX -= MathHelper.sin(v2 * 0.067F) * 0.05F;
    }

    @Override
    public Iterable<ModelRenderer> getParts() {
        return ImmutableList.of(throwerBody, throwerBody2, throwerHead, throwerLeftArm, throwerLeftLeg, throwerRightArm, throwerRightLeg);
    }

    @Override
    public void translateHand(HandSide handSide, MatrixStack matrixStack) {
        if (handSide == HandSide.RIGHT) {
            throwerRightArm.translateRotate(matrixStack);
        } else {
            throwerLeftArm.translateRotate(matrixStack);
        }
    }

    @Override
    public ModelRenderer getModelHead() {
        return throwerHead;
    }
}