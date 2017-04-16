package schr0.chastmob.entity.render.layer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import schr0.chastmob.ChastMob;
import schr0.chastmob.entity.EntityChast;
import schr0.chastmob.entity.render.RenderChast;

@SideOnly(Side.CLIENT)
public class LayerChastCore extends LayerChast
{

	private static final ResourceLocation RES_CHAST_CORE = new ResourceLocation(ChastMob.MOD_RESOURCE_DOMAIN + "textures/entity/chast/chast_core.png");

	public LayerChastCore(RenderChast chastRendererRendererIn)
	{
		super(chastRendererRendererIn);
	}

	@Override
	public void doRenderLayer(EntityChast entityChast, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
	{
		this.getRender().bindTexture(RES_CHAST_CORE);

		float[] dyeRgb = this.getCoreRgb(entityChast);
		GlStateManager.color(dyeRgb[0], dyeRgb[1], dyeRgb[2]);

		this.getModel().setModelAttributes(this.getModel());
		this.getModel().render(entityChast, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
		this.getModel().setLivingAnimations(entityChast, limbSwing, limbSwingAmount, partialTicks);
	}

	@Override
	public boolean shouldCombineTextures()
	{
		return true;
	}

	// TODO /* ======================================== MOD START =====================================*/

	private float[] getCoreRgb(EntityChast entityChast)
	{
		EnumDyeColor enumDyeColor;

		switch (entityChast.getCondition())
		{
			case HURT :

				enumDyeColor = EnumDyeColor.YELLOW;
				break;

			case DYING :

				enumDyeColor = EnumDyeColor.RED;
				break;

			default :

				enumDyeColor = EnumDyeColor.GREEN;
				break;
		}

		return EntitySheep.getDyeRgb(enumDyeColor);
	}

}
