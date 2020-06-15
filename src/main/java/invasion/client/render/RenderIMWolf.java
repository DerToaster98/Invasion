package invasion.client.render;

import org.lwjgl.opengl.GL11;
import invasion.Reference;
import net.minecraft.client.model.ModelWolf;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderWolf;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.util.ResourceLocation;


public class RenderIMWolf extends RenderWolf
{
	public RenderIMWolf(RenderManager renderManager)
	{
		super(renderManager, new ModelWolf(), 1.0F);
	}

	private static final ResourceLocation wolf = new ResourceLocation(Reference.MODID + ":textures/wolf_tame_nexus.png");

	@Override
	protected void preRenderCallback(EntityWolf par1EntityLiving, float par2)
	{
		float f = 1.3F;
		GL11.glScalef(f, (2.0F + f) / 3.0F, f);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityWolf entity)
	{
		return wolf;
	}
}