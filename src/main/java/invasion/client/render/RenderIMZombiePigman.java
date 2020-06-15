package invasion.client.render;

import org.lwjgl.opengl.GL11;
import invasion.Reference;
import invasion.client.render.layer.LayerHeldItemBigBiped;
import invasion.client.render.model.BigBipedModel;
import invasion.entity.monster.EntityIMZombiePigman;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelZombie;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.util.ResourceLocation;


public class RenderIMZombiePigman extends RenderBiped<EntityIMZombiePigman>
{

	private static final ResourceLocation t_T1 = new ResourceLocation(Reference.MODID + ":textures/pigzombie64x32.png");
	private static final ResourceLocation t_T3 = new ResourceLocation(Reference.MODID + ":textures/zombiePigmanT3.png");

	protected ModelBiped modelBiped;
	protected BigBipedModel bigBipedModel = new BigBipedModel();

	protected LayerHeldItem layerHeldItem = new LayerHeldItem(this);
	protected LayerHeldItemBigBiped layerHeldItemBigBiped = new LayerHeldItemBigBiped(this);
	protected LayerBipedArmor layerBipedArmor = new LayerBipedArmor(this)
	{
		@Override
		protected void initArmor()
		{
			this.modelLeggings = new ModelZombie(0.5F, true);
			this.modelArmor = new ModelZombie(1.0F, true);
		}
	};

	public RenderIMZombiePigman(RenderManager renderManager)
	{
		super(renderManager, new ModelZombie(0.0F, true), 0.5F);
		this.modelBiped = (ModelBiped)this.mainModel;
		this.addLayer(this.layerHeldItem);
		this.addLayer(this.layerBipedArmor);
	}

	@Override
	public void doRender(EntityIMZombiePigman entity, double x, double y, double z, float entityYaw, float partialTicks)
	{
		this.removeLayer(this.layerHeldItem);
		this.removeLayer(this.layerHeldItemBigBiped);
		if (entity.isBigRenderTempHack())
		{
			this.addLayer(this.layerHeldItemBigBiped);
			this.mainModel = this.bigBipedModel;
			this.bigBipedModel.setSneaking(entity.isSneaking());
			this.doRenderBigBiped(entity, x, y, z, entityYaw, partialTicks);
		}
		else
		{
			this.addLayer(this.layerHeldItem);
			this.mainModel = this.modelBiped;
			super.doRender(entity, x, y, z, entityYaw, partialTicks);
		}

	}

	public void doRenderBigBiped(EntityIMZombiePigman entity, double x, double y, double z, float entityYaw, float partialTicks)
	{
	}

	@Override
	protected void preRenderCallback(EntityIMZombiePigman entity, float partialTickTime)
	{
		float f = entity.scaleAmount();
		GL11.glScalef(f, (2.0F + f) / 3.0F, f);
	}

	protected ResourceLocation getTexture(EntityIMZombiePigman entity)
	{
		switch (entity.getTextureId())
		{
			case 0:
				return t_T1;
			case 2:
				return t_T3;
			default:
				return t_T1;
		}
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityIMZombiePigman entity)
	{
		return this.getTexture((EntityIMZombiePigman)entity);
	}
}