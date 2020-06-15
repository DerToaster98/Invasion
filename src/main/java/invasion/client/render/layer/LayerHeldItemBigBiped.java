package invasion.client.render.layer;

import invasion.client.render.model.BigBipedModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;


public class LayerHeldItemBigBiped implements LayerRenderer<EntityLivingBase> {

    private final RenderLivingBase renderLivingEntity;

    public LayerHeldItemBigBiped(RenderLivingBase renderLivingEntity) {
        this.renderLivingEntity = renderLivingEntity;
    }

    @Override
    public void doRenderLayer(EntityLivingBase entityLiving, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        ItemStack itemstack = entityLiving.getActiveItemStack(); //.getHeldItem();

        if (itemstack != null && this.renderLivingEntity.getMainModel() instanceof BigBipedModel) {
            GlStateManager.pushMatrix();

            if (this.renderLivingEntity.getMainModel().isChild) {
                float f7 = 0.5F;
                GlStateManager.translate(0.0F, 0.625F, 0.0F);
                GlStateManager.rotate(-20.0F, -1.0F, 0.0F, 0.0F);
                GlStateManager.scale(f7, f7, f7);
            }

            ((BigBipedModel) this.renderLivingEntity.getMainModel()).itemArmPostRender(1.0F);
            GlStateManager.translate(-0.0625F, 0.4375F, 0.0625F);

            if (entityLiving instanceof EntityPlayer && ((EntityPlayer) entityLiving).fishEntity != null) {
                itemstack = new ItemStack(Items.FISHING_ROD, 0);
            }

            Item item = itemstack.getItem();
            Minecraft minecraft = Minecraft.getMinecraft();

            if (item instanceof ItemBlock) // && Block.getBlockFromItem(item).getRenderType() == 2)
            {
                GlStateManager.translate(0.0F, 0.1875F, -0.3125F);
                GlStateManager.rotate(20.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
                float f8 = 0.375F;
                GlStateManager.scale(-f8, -f8, f8);
            }
            float f9 = 0.5F;
            GlStateManager.scale(f9, f9, f9);
            //GlStateManager.translate(-0.25F, 0.15F, 0.0625F);
            GlStateManager.translate(0.0F, -0.425F, -0.2F);
            if (item instanceof ItemBow) {
                GlStateManager.translate(-0.15F, -0.025F, 0.0F);
            }
            minecraft.getItemRenderer().renderItem(entityLiving, itemstack, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND);
            GlStateManager.popMatrix();
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }

}
