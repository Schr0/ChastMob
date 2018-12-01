package schr0.chastmob.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class InventoryChastHelper
{

	public static boolean canStoreInventory(IInventory inventory, ItemStack stack)
	{
		boolean hasEmptySlot = (getFirstEmptySlot(inventory) != -1);

		if (!stack.isEmpty())
		{
			boolean hasCanStoreSlot = (getCanStoreSlot(inventory, stack) != -1);

			if (hasEmptySlot)
			{
				return true;
			}
			else
			{
				return hasCanStoreSlot;
			}
		}
		else
		{
			return hasEmptySlot;
		}
	}

	// TODO /* ======================================== MOD START =====================================*/

	private static int getFirstEmptySlot(IInventory inventory)
	{
		for (int slot = 0; slot < inventory.getSizeInventory(); ++slot)
		{
			if (inventory.getStackInSlot(slot).isEmpty())
			{
				return slot;
			}
		}

		return -1;
	}

	private static int getCanStoreSlot(IInventory inventory, ItemStack stack)
	{
		for (int slot = 0; slot < inventory.getSizeInventory(); ++slot)
		{
			ItemStack stackSlot = inventory.getStackInSlot(slot);

			if (!stackSlot.isEmpty())
			{
				boolean isItemEqual = (stackSlot.getItem() == stack.getItem() && (!stackSlot.getHasSubtypes() || stackSlot.getItemDamage() == stack.getItemDamage()) && ItemStack.areItemStackTagsEqual(stackSlot, stack));
				boolean isStackSizeEqual = (stackSlot.isStackable() && (stackSlot.getCount() < stackSlot.getMaxStackSize()) && (stackSlot.getCount() < inventory.getInventoryStackLimit()));

				if (isItemEqual && isStackSizeEqual)
				{
					return slot;
				}
			}
		}

		return -1;
	}

}
