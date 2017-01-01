package schr0.chastmob;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderChast extends RenderLiving<EntityChast>
{

	private static final ResourceLocation RES_CHAST = new ResourceLocation(ChastMob.MOD_RESOURCE_DOMAIN + "textures/entity/chast/chast.png");

	public RenderChast(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn)
	{
		super(renderManagerIn, modelBaseIn, shadowSizeIn);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityChast entity)
	{
		return RES_CHAST;
	}

}
