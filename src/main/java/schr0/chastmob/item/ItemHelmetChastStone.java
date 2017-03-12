package schr0.chastmob.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import schr0.chastmob.ChastMob;

public class ItemHelmetChastStone extends ItemHelmetChast
{

	public ItemHelmetChastStone()
	{
		super(Item.ToolMaterial.STONE);
	}

	@Override
	public ResourceLocation getTexture(ItemStack stack)
	{
		return new ResourceLocation(ChastMob.MOD_RESOURCE_DOMAIN + "textures/entity/chast/armour/helmet_chast_stone.png");
	}

}
