package schr0.chastmob.item;

import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import schr0.chastmob.ChastMob;

public class ItemHelmetChastIron extends ItemHelmetChast
{

	public ItemHelmetChastIron()
	{
		super(ItemArmor.ArmorMaterial.IRON);
	}

	@Override
	public ResourceLocation getTexture(ItemStack stack)
	{
		return new ResourceLocation(ChastMob.MOD_RESOURCE_DOMAIN + "textures/entity/chast/armour/helmet_chast_iron.png");
	}

}
