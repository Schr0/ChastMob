package schr0.chastmob.entity.render.layer;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import schr0.chastmob.ChastMob;
import schr0.chastmob.ChastMobHelper;
import schr0.chastmob.entity.EntityChast;
import schr0.chastmob.entity.render.RenderChast;
import schr0.chastmob.item.IArmourItem;

@SideOnly(Side.CLIENT)
public class LayerChastArmour extends LayerChast
{

	private static final ResourceLocation RES_ARMOUR_CHAST_DEFAULT = new ResourceLocation(ChastMob.MOD_RESOURCE_DOMAIN + "textures/entity/chast/armour/armour_wood.png");

	public LayerChastArmour(RenderChast chastRendererRendererIn)
	{
		super(chastRendererRendererIn);
	}

	@Override
	public void doRenderLayer(EntityChast entityChast, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
	{
		ItemStack stackEqHead = entityChast.getItemStackFromSlot(EntityEquipmentSlot.HEAD);

		if (ChastMobHelper.isNotEmptyItemStack(stackEqHead))
		{
			this.getRenderChast().bindTexture(this.getArmourTexture(stackEqHead));
			this.getModelChast().setModelAttributes(this.getRenderChast().getMainModel());
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

	private ResourceLocation getArmourTexture(ItemStack stack)
	{
		if (stack.getItem() instanceof IArmourItem)
		{
			return ((IArmourItem) stack.getItem()).getArmourTexture();
		}

		return RES_ARMOUR_CHAST_DEFAULT;
	}
}
