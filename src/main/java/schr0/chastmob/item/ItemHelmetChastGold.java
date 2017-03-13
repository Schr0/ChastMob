package schr0.chastmob.item;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import schr0.chastmob.ChastMob;

public class ItemHelmetChastGold extends ItemHelmetChast
{

	public ItemHelmetChastGold()
	{
		super(ItemHelmetChast.HelmetMaterial.GOLD);
	}

	@Override
	public ResourceLocation getHelmetTexture(ItemStack stack)
	{
		return new ResourceLocation(ChastMob.MOD_RESOURCE_DOMAIN + "textures/entity/chast/armour/helmet_chast_gold.png");
	}

}
