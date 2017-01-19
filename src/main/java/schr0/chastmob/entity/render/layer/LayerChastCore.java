package schr0.chastmob.entity.render.layer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import schr0.chastmob.ChastMob;
import schr0.chastmob.entity.EntityChast;
import schr0.chastmob.entity.render.ModelChast;
import schr0.chastmob.entity.render.RenderChast;

@SideOnly(Side.CLIENT)
public class LayerChastCore implements LayerRenderer<EntityChast>
{

	private static final ResourceLocation RES_CHAST_CORE = new ResourceLocation(ChastMob.MOD_RESOURCE_DOMAIN + "textures/entity/chast/chast_core.png");

	private final RenderChast chastRenderer;
	private final ModelChast chastModel;

	public LayerChastCore(RenderChast chastRendererRendererIn)
	{
		this.chastRenderer = chastRendererRendererIn;
		this.chastModel = new ModelChast();
	}

	@Override
	public void doRenderLayer(EntityChast entityChast, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
	{
		this.chastRenderer.bindTexture(RES_CHAST_CORE);

		float[] dyeRgb = this.getCoreRgb(entityChast);
		GlStateManager.color(dyeRgb[0], dyeRgb[1], dyeRgb[2]);

		this.chastModel.setModelAttributes(this.chastRenderer.getMainModel());
		this.chastModel.render(entityChast, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
		this.chastModel.setLivingAnimations(entityChast, limbSwing, limbSwingAmount, partialTicks);
	}

	@Override
	public boolean shouldCombineTextures()
	{
		return true;
	}

	// TODO /* ======================================== MOD START =====================================*/

	private float[] getCoreRgb(EntityChast entityChast)
	{
		EnumDyeColor enumDyeColor = EnumDyeColor.GREEN;
		int health = (int) entityChast.getHealth();
		int healthMax = (int) entityChast.getMaxHealth();

		if (health < (healthMax / 2))
		{
			enumDyeColor = EnumDyeColor.YELLOW;

			if (health < (healthMax / 4))
			{
				enumDyeColor = EnumDyeColor.RED;
			}
		}

		return EntitySheep.getDyeRgb(enumDyeColor);
	}

}
