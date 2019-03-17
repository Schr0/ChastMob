package schr0.chastmob.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import schr0.chastmob.entity.EntityChast;

public abstract class InventoryChast extends InventoryBasic
{

	private static final double MAX_RANGE = 16.0D;
	private EntityChast entityChast;

	public InventoryChast(EntityChast entityChast, int slotCount)
	{
		super(entityChast.getName(), true, slotCount);

		this.entityChast = entityChast;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player)
	{
		if (this.entityChast.isEntityAlive())
		{
			if (this.entityChast.isPanic())
			{
				return false;
			}

			return (player.getDistanceSq(this.entityChast) < MAX_RANGE);
		}

		return false;
	}

	// TODO /* ======================================== MOD START =====================================*/

	public EntityChast getContainerEntity()
	{
		return this.entityChast;
	}

	public void readInventoryFromNBT(NBTTagList nbtList)
	{
		this.clear();

		for (int i = 0; i < nbtList.tagCount(); ++i)
		{
			NBTTagCompound nbt = nbtList.getCompoundTagAt(i);
			int slot = nbt.getByte("Slot") & 255;

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
