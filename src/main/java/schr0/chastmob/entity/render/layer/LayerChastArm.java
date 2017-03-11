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
public class LayerChastArm extends LayerChast
{

	private static final ResourceLocation RES_CHAST_ARM = new ResourceLocation(ChastMob.MOD_RESOURCE_DOMAIN + "textures/entity/chast/chast_arm.png");

	public LayerChastArm(RenderChast chastRendererRendererIn)
	{
		super(chastRendererRendererIn);
	}

	@Override
	public void doRenderLayer(EntityChast entityChast, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
	{
		this.getRenderChast().bindTexture(RES_CHAST_ARM);

		float[] dyeRgb = this.getArmRgb(entityChast);
		GlStateManager.color(dyeRgb[0], dyeRgb[1], dyeRgb[2]);

		this.getModelChast().setModelAttributes(this.getRenderChast().getMainModel());
		this.getModelChast().render(entityChast, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
		this.getModelChast().setLivingAnimations(entityChast, limbSwing, limbSwingAmount, partialTicks);
	}

	@Override
	public boolean shouldCombineTextures()
	{
		return true;
	}

	// TODO /* ======================================== MOD START =====================================*/

	private float[] getArmRgb(EntityChast entityChast)
	{
		EnumDyeColor enumDyeColor = EnumDyeColor.byMetadata(entityChast.getArmColor().getMetadata());

		return EntitySheep.getDyeRgb(enumDyeColor);
	}

}
