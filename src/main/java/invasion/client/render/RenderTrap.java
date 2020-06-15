/*package invasion.client.render;

import org.lwjgl.opengl.GL11;
import invasion.Reference;
import invasion.client.render.model.ModelTrap;
import invasion.entity.block.trap.EntityIMTrap;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;


public class RenderTrap extends Render<EntityIMTrap>
{
	private static final ResourceLocation texture = new ResourceLocation(Reference.MODID + ":textures/trap.png");
	private ModelTrap modelTrap;

	public RenderTrap(RenderManager renderManager)
	{
		this(renderManager, new ModelTrap());
	}

	public RenderTrap(RenderManager renderManager, ModelTrap model)
	{
		super(renderManager);
		this.modelTrap = model;
	}

	@Override
	public void doRender(EntityIMTrap entityTrap, double d, double d1, double d2, float f, float f1)
	{
		GL11.glPushMatrix();
		GL11.glTranslatef((float)d, (float)d1, (float)d2);

		GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
		GL11.glEnable(32826);
		GL11.glScalef(1.3F, 1.3F, 1.3F);
		this.bindEntityTexture(entityTrap);
		this.modelTrap.render(entityTrap, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, entityTrap.isEmpty(), entityTrap.getTrapType());
		GL11.glDisable(32826);
		GL11.glPopMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityIMTrap entity)
	{
		return texture;
	}
}

 */