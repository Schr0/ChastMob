package schr0.chastmob.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import schr0.chastmob.item.ItemFilter;

public class InventoryFilterResult extends InventoryFilter
{

	public InventoryFilterResult(ItemStack stack)
	{
		super(stack);
	}

	@Override
	public void closeInventory(EntityPlayer player)
	{
		super.markDirty();

		ItemStack stack = this.getContainerItemStack();
		Item item = stack.getItem();

		if (item instanceof ItemFilter)
		{
			((ItemFilter) item).saveInventoryFilterResult(stack, this);
		}
	}

}
