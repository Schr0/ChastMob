package schr0.chastmob.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import schr0.chastmob.ChastMobHelper;
import schr0.chastmob.item.ItemFilter;

public class InventoryFilter extends InventoryBasic
{

	private ItemStack stackFilter;

	public InventoryFilter(ItemStack stack)
	{
		super(stack.getDisplayName(), true, 9);

		this.stackFilter = stack;
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 1;
	}

	@Override
	public void closeInventory(EntityPlayer player)
	{
		Item item = this.stackFilter.getItem();

		if (item instanceof ItemFilter)
		{
			((ItemFilter) item).setInventoryFilter(this.stackFilter, this);
		}
	}

	// TODO /* ======================================== MOD START =====================================*/

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

			if (ChastMobHelper.isNotEmptyItemStack(stackSlot))
			{
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setByte("Slot", (byte) slot);
				stackSlot.writeToNBT(nbt);
				nbtList.appendTag(nbt);
			}
		}

		return nbtList;
	}

	public ItemFilter.Type getFilterType()
	{
		Item item = this.stackFilter.getItem();

		if (item instanceof ItemFilter)
		{
			return ((ItemFilter) item).getFilterType(stackFilter);
		}

		return ItemFilter.Type.WHITE;
	}

}
