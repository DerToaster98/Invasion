package invasion.client.render.model;


import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.projectile.EggEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EggModel extends EntityModel<EggEntity> /*SegmentedModel<EggEntity>*/ {
    public ModelRenderer egg;
    ModelRenderer base;
    ModelRenderer l3s4;
    ModelRenderer l3s2;
    ModelRenderer l3s3;
    ModelRenderer l3s1;
    ModelRenderer top;
    ModelRenderer l4s1;
    ModelRenderer l4s4;
    ModelRenderer l2s3;
    ModelRenderer l2s4;
    ModelRenderer l2s1;
    ModelRenderer l4s2;
    ModelRenderer l4s3;
    ModelRenderer l2s2;
    ModelRenderer l1s4;
    ModelRenderer l1s1;
    ModelRenderer l1s2;
    ModelRenderer l1s3;

    public EggModel() {
        textureWidth = 64;
        textureHeight = 32;
        egg = new ModelRenderer(this, 0, 8);
        egg.mirror = true;
        // egg.addBox()
        egg.setRotationPoint(0f, 0f, 0f);


/*
        this.top = new ModelRenderer(this, 0, 8);
        this.top.addBox(2F, -11F, 2F, 5, 1, 5);
        this.top.setRotationPoint(0F, 0F, 0F);
        this.top.setTextureSize(64, 32);
        this.top.mirror = true;
        this.setRotation(this.top, 0F, 0F, 0F);
        this.l4s4 = new ModelRenderer(this, 28, 23);
        this.l4s4.addBox(1F, -10F, 2F, 1, 3, 6);
        this.l4s4.setRotationPoint(0F, 0F, 0F);
        this.l4s4.setTextureSize(64, 32);
        this.l4s4.mirror = true;
        this.setRotation(this.l4s4, 0F, 0F, 0F);
        this.l4s3 = new ModelRenderer(this, 0, 24);
        this.l4s3.addBox(1F, -10F, 1F, 6, 3, 1);
        this.l4s3.setRotationPoint(0F, 0F, 0F);
        this.l4s3.setTextureSize(64, 32);
        this.l4s3.mirror = true;
        this.setRotation(this.l4s3, 0F, 0F, 0F);
        this.l4s2 = new ModelRenderer(this, 28, 23);
        this.l4s2.addBox(7F, -10F, 1F, 1, 3, 6);
        this.l4s2.setRotationPoint(0F, 0F, 0F);
        this.l4s2.setTextureSize(64, 32);
        this.l4s2.mirror = true;
        this.setRotation(this.l4s2, 0F, 0F, 0F);
        this.l4s1 = new ModelRenderer(this, 0, 24);
        this.l4s1.addBox(2F, -10F, 7F, 6, 3, 1);
        this.l4s1.setRotationPoint(0F, 0F, 0F);
        this.l4s1.setTextureSize(64, 32);
        this.l4s1.mirror = true;
        this.setRotation(this.l4s1, 0F, 0F, 0F);
        this.l3s4 = new ModelRenderer(this, 10, 22);
        this.l3s4.addBox(0F, -7F, 1F, 1, 2, 8);
        this.l3s4.setRotationPoint(0F, 0F, 0F);
        this.l3s4.setTextureSize(64, 32);
        this.l3s4.mirror = true;
        this.setRotation(this.l3s4, 0F, 0F, 0F);
        this.l3s3 = new ModelRenderer(this, 0, 21);
        this.l3s3.addBox(0F, -7F, 0F, 8, 2, 1);
        this.l3s3.setRotationPoint(0F, 0F, 0F);
        this.l3s3.setTextureSize(64, 32);
        this.l3s3.mirror = true;
        this.setRotation(this.l3s3, 0F, 0F, 0F);
        this.l3s2 = new ModelRenderer(this, 10, 22);
        this.l3s2.addBox(8F, -7F, 0F, 1, 2, 8);
        this.l3s2.setRotationPoint(0F, 0F, 0F);
        this.l3s2.setTextureSize(64, 32);
        this.l3s2.mirror = true;
        this.setRotation(this.l3s2, 0F, 0F, 0F);
        this.l3s1 = new ModelRenderer(this, 0, 21);
        this.l3s1.addBox(1F, -7F, 8F, 8, 2, 1);
        this.l3s1.setRotationPoint(0F, 0F, 0F);
        this.l3s1.setTextureSize(64, 32);
        this.l3s1.mirror = true;
        this.setRotation(this.l3s1, 0F, 0F, 0F);
        this.l2s4 = new ModelRenderer(this, 20, 10);
        this.l2s4.addBox(-1F, -5F, 0F, 1, 4, 9);
        this.l2s4.setRotationPoint(0F, 0F, 0F);
        this.l2s4.setTextureSize(64, 32);
        this.l2s4.mirror = true;
        this.setRotation(this.l2s4, 0F, 0F, 0F);
        this.l2s3 = new ModelRenderer(this, 0, 16);
        this.l2s3.addBox(0F, -5F, -1F, 9, 4, 1);
        this.l2s3.setRotationPoint(0F, 0F, 0F);
        this.l2s3.setTextureSize(64, 32);
        this.l2s3.mirror = true;
        this.setRotation(this.l2s3, 0F, 0F, 0F);
        this.l2s2 = new ModelRenderer(this, 20, 10);
        this.l2s2.addBox(9F, -5F, 0F, 1, 4, 9);
        this.l2s2.setRotationPoint(0F, 0F, 0F);
        this.l2s2.setTextureSize(64, 32);
        this.l2s2.mirror = true;
        this.setRotation(this.l2s2, 0F, 0F, 0F);
        this.l2s1 = new ModelRenderer(this, 0, 16);
        this.l2s1.addBox(0F, -5F, 9F, 9, 4, 1);
        this.l2s1.setRotationPoint(0F, 0F, 0F);
        this.l2s1.setTextureSize(64, 32);
        this.l2s1.mirror = true;
        this.setRotation(this.l2s1, 0F, 0F, 0F);
        this.l1s4 = new ModelRenderer(this, 28, 0);
        this.l1s4.addBox(0F, -1F, 1F, 1, 1, 8);
        this.l1s4.setRotationPoint(0F, 0F, 0F);
        this.l1s4.setTextureSize(64, 32);
        this.l1s4.mirror = true;
        this.setRotation(this.l1s4, 0F, 0F, 0F);
        this.l1s3 = new ModelRenderer(this, 0, 14);
        this.l1s3.addBox(0F, -1F, 0F, 8, 1, 1);
        this.l1s3.setRotationPoint(0F, 0F, 0F);
        this.l1s3.setTextureSize(64, 32);
        this.l1s3.mirror = true;
        this.setRotation(this.l1s3, 0F, 0F, 0F);
        this.l1s2 = new ModelRenderer(this, 28, 0);
        this.l1s2.addBox(8F, -1F, 0F, 1, 1, 8);
        this.l1s2.setRotationPoint(0F, 0F, 0F);
        this.l1s2.setTextureSize(64, 32);
        this.l1s2.mirror = true;
        this.setRotation(this.l1s2, 0F, 0F, 0F);
        this.l1s1 = new ModelRenderer(this, 0, 14);
        this.l1s1.addBox(0F, 0F, 0F, 8, 1, 1);
        this.l1s1.setRotationPoint(1F, -1F, 8F);
        this.l1s1.setTextureSize(64, 32);
        this.l1s1.mirror = true;
        this.setRotation(this.l1s1, 0F, 0F, 0F);
        this.base = new ModelRenderer(this, 0, 0);
        this.base.addBox(1F, 0F, 1F, 7, 1, 7);
        this.base.setRotationPoint(0F, 0F, 0F);
        this.base.setTextureSize(64, 32);
        this.base.mirror = true;
        this.setRotation(this.base, 0F, 0F, 0F);

 */
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        egg.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }

    @Override
    public void setRotationAngles(EggEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }
}