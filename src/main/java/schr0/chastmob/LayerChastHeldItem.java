package schr0.chastmob;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerChastHeldItem implements LayerRenderer<EntityChast>
{

	protected final RenderChast chastRenderer;

	public LayerChastHeldItem(RenderChast chastRendererRendererIn)
	{
		this.chastRenderer = chastRendererRendererIn;
	}

	@Override
	public void doRenderLayer(EntityChast entityChast, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
	{
		boolean isPrimaryHand = (entityChast.getPrimaryHand() == EnumHandSide.RIGHT);
		ItemStack stackMain = isPrimaryHand ? entityChast.getHeldItemOffhand() : entityChast.getHeldItemMainhand();
		ItemStack stackOff = isPrimaryHand ? entityChast.getHeldItemMainhand() : entityChast.getHeldItemOffhand();

		if (ChastMobHelper.isNotEmptyItemStack(stackMain) || ChastMobHelper.isNotEmptyItemStack(stackOff))
		{
			GlStateManager.pushMatrix();

			this.renderHeldItem(entityChast, stackOff, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, EnumHandSide.RIGHT);
			this.renderHeldItem(entityChast, stackMain, ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, EnumHandSide.LEFT);

			GlStateManager.popMatrix();
		}
	}

	@Override
	public boolean shouldCombineTextures()
	{
		return false;
	}

	// TODO /* ======================================== MOD START =====================================*/

	private void renderHeldItem(EntityChast entityChast, ItemStack stack, ItemCameraTransforms.TransformType transformType, EnumHandSide handSide)
	{
		if (ChastMobHelper.isNotEmptyItemStack(stack))
		{
			GlStateManager.pushMatrix();

			// ((ModelBiped) this.livingEntityRenderer.getMainModel()).postRenderArm(0.0625F, handSide);
			((ModelChast) this.chastRenderer.getMainModel()).postRenderArm(0.05F, handSide);

			GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
			boolean flag = (handSide == EnumHandSide.LEFT);
			GlStateManager.translate((float) (flag ? -1 : 1) / 16.0F, 0.125F, -0.625F);
			Minecraft.getMinecraft().getItemRenderer().renderItemSide(entityChast, stack, transformType, flag);

			GlStateManager.popMatrix();
		}
	}

}
