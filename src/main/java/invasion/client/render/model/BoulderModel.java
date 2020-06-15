package invasion.client.render.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import invasion.entity.projectile.BoulderEntity;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public class BoulderModel extends EntityModel<BoulderEntity> {
    public ModelRenderer boulder;

    public BoulderModel() {
        boulder = new ModelRenderer(this, 0, 0);
        boulder.addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8);
        boulder.setRotationPoint(0.0F, 0.0F, 0.0F);
        boulder.rotateAngleX = 0.0F;
        boulder.rotateAngleY = 0.0F;
        boulder.rotateAngleZ = 0.0F;
        boulder.mirror = false;
    }

    @Override
    public void setRotationAngles(BoulderEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        boulder.rotateAngleX = headPitch;
        boulder.rotateAngleY = netHeadYaw;
        boulder.rotateAngleZ = limbSwing;

    }

    @Override
    public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        boulder.render(matrixStack, buffer, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }
}