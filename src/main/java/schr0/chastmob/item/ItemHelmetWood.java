package schr0.chastmob.item;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import schr0.chastmob.ChastMob;
import schr0.chastmob.api.ItemChastHelmet;

public class ItemHelmetWood extends ItemChastHelmet
{

	public ItemHelmetWood()
	{
		super(ItemChastHelmet.ChastHelmetMaterial.WOOD);
	}

	@Override
	public ResourceLocation getHelmetTexture(ItemStack stack)
	{
		return new ResourceLocation(ChastMob.MOD_RESOURCE_DOMAIN + "textures/entity/chast/armour/chast_helmet_wood.png");
	}

}
