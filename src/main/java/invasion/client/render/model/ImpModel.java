package invasion.client.render.model;

import com.google.common.collect.ImmutableList;
import invasion.entity.monster.ImpEntity;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;


public class ImpModel extends SegmentedModel<ImpEntity> {
    ModelRenderer head;
    ModelRenderer body;
    ModelRenderer rightArm;
    ModelRenderer leftArm;
    ModelRenderer rightLeg;
    ModelRenderer leftLeg;
    ModelRenderer rshin;
    ModelRenderer rightFoot;
    ModelRenderer lshin;
    ModelRenderer leftFoot;
    ModelRenderer rightHorn;
    ModelRenderer leftHorn;
    ModelRenderer bodymid;
    ModelRenderer neck;
    ModelRenderer bodyChest;
    ModelRenderer tail;
    ModelRenderer tail2;

    public ImpModel() {
        this(0.0F);
    }


    public ImpModel(float f) {
        this(f, 0.0F);
    }

    public ImpModel(float f, float f1) {
        head = new ModelRenderer(this, 44, 0);
        head.addBox(-2.733333F, -3.0F, -2.0F, 5, 3, 4);
        head.setRotationPoint(-0.4F, 9.8F, -3.3F);
        head.rotateAngleX = 0.15807F;
        head.rotateAngleY = 0.0F;
        head.rotateAngleZ = 0.0F;
        head.mirror = false;
        body = new ModelRenderer(this, 23, 1);
        body.addBox(-4.0F, 0.0F, -4.0F, 7, 4, 3);
        body.setRotationPoint(0.0F, 9.1F, -0.8666667F);
        body.rotateAngleX = 0.64346F;
        body.rotateAngleY = 0.0F;
        body.rotateAngleZ = 0.0F;
        body.mirror = false;
        rightArm = new ModelRenderer(this, 26, 9);
        rightArm.addBox(-2.0F, -0.7333333F, -1.133333F, 2, 7, 2);
        rightArm.setRotationPoint(-4.0F, 10.8F, -2.066667F);
        rightArm.rotateAngleX = 0.0F;
        rightArm.rotateAngleY = 0.0F;
        rightArm.rotateAngleZ = 0.0F;
        rightArm.mirror = false;
        leftArm = new ModelRenderer(this, 18, 9);
        leftArm.addBox(0.0F, -0.8666667F, -1.0F, 2, 7, 2);
        leftArm.setRotationPoint(3.0F, 10.8F, -2.1F);
        leftArm.rotateAngleX = 0.0F;
        leftArm.rotateAngleY = 0.0F;
        leftArm.rotateAngleZ = 0.0F;
        leftArm.mirror = false;
        rightLeg = new ModelRenderer(this, 0, 17);
        rightLeg.addBox(-1.0F, 0.0F, -2.0F, 2, 4, 3);
        rightLeg.setRotationPoint(-2.0F, 16.9F, -1.0F);
        rightLeg.rotateAngleX = -0.15807F;
        rightLeg.rotateAngleY = 0.0F;
        rightLeg.rotateAngleZ = 0.0F;
        rightLeg.mirror = false;
        leftLeg = new ModelRenderer(this, 0, 24);
        leftLeg.addBox(-1.0F, 0.0F, -2.0F, 2, 4, 3);
        leftLeg.setRotationPoint(1.0F, 17.0F, -1.0F);
        leftLeg.rotateAngleX = -0.15919F;
        leftLeg.rotateAngleY = 0.0F;
        leftLeg.rotateAngleZ = 0.0F;
        leftLeg.mirror = false;
        rshin = new ModelRenderer(this, 10, 17);
        rshin.addBox(-2.0F, 0.6F, -4.4F, 2, 3, 2);
        rshin.setRotationPoint(-1.0F, 16.9F, -1.0F);
        rshin.rotateAngleX = 0.82623F;
        rshin.rotateAngleY = 0.0F;
        rshin.rotateAngleZ = 0.0F;
        rshin.mirror = false;
        rightFoot = new ModelRenderer(this, 18, 18);
        rightFoot.addBox(-2.0F, 4.2F, -1.0F, 2, 3, 2);
        rightFoot.setRotationPoint(-1.0F, 16.9F, -1.0F);
        rightFoot.rotateAngleX = -0.01403F;
        rightFoot.rotateAngleY = 0.0F;
        rightFoot.rotateAngleZ = 0.0F;
        rightFoot.mirror = false;
        lshin = new ModelRenderer(this, 10, 22);
        lshin.addBox(-1.0F, 0.6F, -4.433333F, 2, 3, 2);
        lshin.setRotationPoint(1.0F, 17.0F, -1.0F);
        lshin.rotateAngleX = 0.82461F;
        lshin.rotateAngleY = 0.0F;
        lshin.rotateAngleZ = 0.0F;
        lshin.mirror = false;
        leftFoot = new ModelRenderer(this, 10, 27);
        leftFoot.addBox(-1.0F, 4.2F, -1.0F, 2, 3, 2);
        leftFoot.setRotationPoint(1.0F, 17.0F, -1.0F);
        leftFoot.rotateAngleX = -0.01214F;
        leftFoot.rotateAngleY = 0.0F;
        leftFoot.rotateAngleZ = 0.0F;
        leftFoot.mirror = false;

        rightHorn = new ModelRenderer(this, 0, 0);
        rightHorn.addBox(1.0F, -4.0F, 1.5F, 1, 1, 1);
        rightHorn.setRotationPoint(-0.4F, 0F, -3.3F);
        rightHorn.mirror = false;

        leftHorn = new ModelRenderer(this, 0, 2);
        leftHorn.addBox(-1.0F, -4.0F, 1.5F, 1, 1, 1);
        leftHorn.setRotationPoint(-0.4F, 0F, -3.3F);
        leftHorn.mirror = false;

        bodymid = new ModelRenderer(this, 1, 1);
        bodymid.addBox(0.0F, 0.0F, 0.0F, 7, 5, 3);
        bodymid.setRotationPoint(-4.0F, 12.46667F, -2.266667F);
        bodymid.rotateAngleX = -0.15807F;
        bodymid.rotateAngleY = 0.0F;
        bodymid.rotateAngleZ = 0.0F;
        bodymid.mirror = false;
        neck = new ModelRenderer(this, 44, 7);
        neck.addBox(0.0F, 0.0F, 0.0F, 3, 2, 2);
        neck.setRotationPoint(-2.0F, 9.6F, -4.033333F);
        neck.rotateAngleX = 0.27662F;
        neck.rotateAngleY = 0.0F;
        neck.rotateAngleZ = 0.0F;
        neck.mirror = false;
        bodyChest = new ModelRenderer(this, 0, 9);
        bodyChest.addBox(0.0F, -1.0F, 0.0F, 7, 6, 2);
        bodyChest.setRotationPoint(-4.0F, 12.36667F, -3.8F);
        bodyChest.rotateAngleX = 0.31614F;
        bodyChest.rotateAngleY = 0.0F;
        bodyChest.rotateAngleZ = 0.0F;
        bodyChest.mirror = false;
        tail = new ModelRenderer(this, 18, 23);
        tail.addBox(0.0F, 0.0F, 0.0F, 1, 8, 1);
        tail.setRotationPoint(-1.0F, 15.0F, -0.6666667F);
        tail.rotateAngleX = 0.47304F;
        tail.rotateAngleY = 0.0F;
        tail.rotateAngleZ = 0.0F;
        tail.mirror = false;
        tail2 = new ModelRenderer(this, 22, 23);
        tail2.addBox(0.0F, 0.0F, 0.0F, 1, 4, 1);
        tail2.setRotationPoint(-1.0F, 22.1F, 2.9F);
        tail2.rotateAngleX = 1.38309F;
        tail2.rotateAngleY = 0.0F;
        tail2.rotateAngleZ = 0.0F;
        tail2.mirror = false;

        head.addChild(leftHorn);
        head.addChild(rightHorn);
    }


    @Override
    public void setRotationAngles(ImpEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        head.rotateAngleY = (netHeadYaw / 57.29578F);
        head.rotateAngleX = (headPitch / 57.29578F);
    }

    @Override
    public Iterable<ModelRenderer> getParts() {
        return ImmutableList.of();
    }

    @Override
    public void l(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) {


        rightArm.rotateAngleX = (MathHelper.cos(f * 0.6662F + 3.141593F) * 2.0F * f1 * 0.5F);
        leftArm.rotateAngleX = (MathHelper.cos(f * 0.6662F) * 2.0F * f1 * 0.5F);
        rightArm.rotateAngleZ = 0.0F;
        leftArm.rotateAngleZ = 0.0F;

        rightLeg.rotateAngleX = (MathHelper.cos(f * 0.6662F) * 1.4F * f1 - 0.158F);
        rshin.rotateAngleX = (MathHelper.cos(f * 0.6662F) * 1.4F * f1 + 0.82623F);
        rightFoot.rotateAngleX = (MathHelper.cos(f * 0.6662F) * 1.4F * f1 - 0.01403F);

        leftLeg.rotateAngleX = (MathHelper.cos(f * 0.6662F + 3.141593F) * 1.4F * f1 - 0.15919F);
        lshin.rotateAngleX = (MathHelper.cos(f * 0.6662F + 3.141593F) * 1.4F * f1 + 0.82461F);
        leftFoot.rotateAngleX = (MathHelper.cos(f * 0.6662F + 3.141593F) * 1.4F * f1 - 0.01214F);

        rightLeg.rotateAngleY = 0.0F;
        rshin.rotateAngleY = 0.0F;
        rightFoot.rotateAngleY = 0.0F;

        leftLeg.rotateAngleY = 0.0F;
        lshin.rotateAngleY = 0.0F;
        leftFoot.rotateAngleY = 0.0F;

        rightArm.rotateAngleY = 0.0F;
        leftArm.rotateAngleY = 0.0F;

        rightArm.rotateAngleZ += MathHelper.cos(f2 * 0.09F) * 0.05F + 0.05F;
        leftArm.rotateAngleZ -= MathHelper.cos(f2 * 0.09F) * 0.05F + 0.05F;
        rightArm.rotateAngleX += MathHelper.sin(f2 * 0.067F) * 0.05F;
        leftArm.rotateAngleX -= MathHelper.sin(f2 * 0.067F) * 0.05F;
    }
}