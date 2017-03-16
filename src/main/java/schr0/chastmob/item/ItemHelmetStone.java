package schr0.chastmob.item;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import schr0.chastmob.ChastMob;
import schr0.chastmob.api.ItemChastHelmet;

public class ItemHelmetStone extends ItemChastHelmet
{

	public ItemHelmetStone()
	{
		super(ItemChastHelmet.ChastHelmetMaterial.STONE);
	}

	@Override
	public ResourceLocation getHelmetTexture(ItemStack stack)
	{
		return new ResourceLocation(ChastMob.MOD_RESOURCE_DOMAIN + "textures/entity/chast/armour/helmet_stone.png");
	}

}
