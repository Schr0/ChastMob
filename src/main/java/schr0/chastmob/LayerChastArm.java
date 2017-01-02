package schr0.chastmob;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerChastArm implements LayerRenderer<EntityChast>
{

	private static final ResourceLocation RES_CHAST_ARM = new ResourceLocation(ChastMob.MOD_RESOURCE_DOMAIN + "textures/entity/chast/chast_arm.png");

	private final RenderChast chastRenderer;
	private final ModelChast chastModel;

	public LayerChastArm(RenderChast chastRendererRendererIn)
	{
		this.chastRenderer = chastRendererRendererIn;
		this.chastModel = new ModelChast();
	}

	@Override
	public void doRenderLayer(EntityChast entityChast, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
	{
		this.chastRenderer.bindTexture(RES_CHAST_ARM);

		float[] dyeRgb = this.getArmRgb(entityChast);
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

	private float[] getArmRgb(EntityChast entityChast)
	{
		EnumDyeColor enumDyeColor = EnumDyeColor.byMetadata(entityChast.getColor().getMetadata());

		return EntitySheep.getDyeRgb(enumDyeColor);
	}

}
