package schr0.chastmob.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public abstract class ItemMode extends Item
{

	public abstract boolean isItemValidForSlot(ItemStack stack);

}