package schr0.chastmob.item;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import schr0.chastmob.ChastMob;
import schr0.chastmob.api.ItemChastHelmet;

public class ItemChastHelmetGold extends ItemChastHelmet
{

	public ItemChastHelmetGold()
	{
		super(ItemChastHelmet.ChastHelmetMaterial.GOLD);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ResourceLocation getHelmetTexture(ItemStack stack)
	{
		return new ResourceLocation(ChastMob.MOD_RESOURCE_DOMAIN + "textures/entity/chast/armour/helmet_gold.png");
	}

}
