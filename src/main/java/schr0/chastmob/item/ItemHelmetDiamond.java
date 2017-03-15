package schr0.chastmob.item;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import schr0.chastmob.ChastMob;
import schr0.chastmob.api.ItemChastHelmet;

public class ItemHelmetDiamond extends ItemChastHelmet
{

	public ItemHelmetDiamond()
	{
		super(ItemChastHelmet.ChastHelmetMaterial.DIAMOND);
	}

	@Override
	public ResourceLocation getHelmetTexture(ItemStack stack)
	{
		return new ResourceLocation(ChastMob.MOD_RESOURCE_DOMAIN + "textures/entity/chast/armour/chast_helmet_diamond.png");
	}

}
