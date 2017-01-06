package schr0.chastmob;

import net.minecraft.item.ItemStack;

public class ChastMobVanillaHelper
{

	public static boolean isNotEmptyItemStack(ItemStack stack)
	{
		return (stack != null);
	}

	public static ItemStack getEmptyItemStack()
	{
		return (ItemStack) null;
	}

}
