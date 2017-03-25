package schr0.chastmob.entity.render.layer;

import javax.annotation.Nullable;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import schr0.chastmob.api.ItemChastHelmet;
import schr0.chastmob.entity.EntityChast;
import schr0.chastmob.entity.render.RenderChast;

@SideOnly(Side.CLIENT)
public class LayerChastHelmet extends LayerChast
{

	public LayerChastHelmet(RenderChast chastRendererRendererIn)
	{
		super(chastRendererRendererIn);
	}

	@Override
	public void doRenderLayer(EntityChast entityChast, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
	{
		ItemStack stackEqHead = entityChast.getItemStackFromSlot(EntityEquipmentSlot.HEAD);

		if (!stackEqHead.isEmpty())
		{
			this.getRenderChast().bindTexture(this.getArmourTexture(stackEqHead));

			this.getModelChast().setModelAttributes(this.getModelChast());
			this.getModelChast().render(entityChast, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
			this.getModelChast().setLivingAnimations(entityChast, limbSwing, limbSwingAmount, partialTicks);
		}
	}

	@Override
	public boolean shouldCombineTextures()
	{
		return true;
	}

	// TODO /* ======================================== MOD START =====================================*/

	@Nullable
	private ResourceLocation getArmourTexture(ItemStack stack)
	{
		if (stack.getItem() instanceof ItemChastHelmet)
		{
			return ((ItemChastHelmet) stack.getItem()).getHelmetTexture(stack);
		}

		return (ResourceLocation) null;
	}
}
