package schr0.chastmob.inventory;

import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import schr0.chastmob.item.ItemFilter;

public abstract class InventoryFilter extends InventoryBasic
{

	private ItemStack stackItemFilter;

	public InventoryFilter(ItemStack stack)
	{
		super(stack.getDisplayName(), true, 9);

		this.stackItemFilter = stack;
	}

	// TODO /* ======================================== MOD START =====================================*/

	public ItemFilter.Type getType()
	{
		Item item = this.stackItemFilter.getItem();

		if (item instanceof ItemFilter)
		{
			return ((ItemFilter) item).getFilterType(stackItemFilter);
		}

		return ItemFilter.Type.WHITE;
	}

	public ItemStack getContainerItem()
	{
		return this.stackItemFilter;
	}

	public void readInventoryFromNBT(NBTTagList nbtList)
	{
		this.clear();

		for (int slot = 0; slot < nbtList.tagCount(); ++slot)
		{
			NBTTagCompound nbt = nbtList.getCompoundTagAt(slot);
			slot = nbt.getByte("Slot") & 255;

			if ((0 <= slot) && (slot < this.getSizeInventory()))
			{
				this.setInventorySlotContents(slot, new ItemStack(nbt));
			}
		}
	}

	public NBTTagList writeInventoryToNBT()
	{
		NBTTagList nbtList = new NBTTagList();

		for (int slot = 0; slot < this.getSizeInventory(); ++slot)
		{
			ItemStack stackSlot = this.getStackInSlot(slot);

			if (!stackSlot.isEmpty())
			{
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setByte("Slot", (byte) slot);
				stackSlot.writeToNBT(nbt);
				nbtList.appendTag(nbt);
			}
		}

		return nbtList;
	}

}
