package schr0.chastmob.item;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import schr0.chastmob.ChastMob;

public class ItemArmourWood extends Item implements IArmourItem
{

	public ItemArmourWood()
	{
		this.setMaxStackSize(1);
	}

	@Override
	public ResourceLocation getArmourTexture()
	{
		return new ResourceLocation(ChastMob.MOD_RESOURCE_DOMAIN + "textures/entity/chast/armour/armour_wood.png");
	}

}
