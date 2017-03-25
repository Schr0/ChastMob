package schr0.chastmob.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import schr0.chastmob.entity.EntityChast;

public abstract class InventoryChast extends InventoryBasic
{

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
			if (this.entityChast.isStateKnockback())
			{
				return false;
			}

			return (player.getDistanceSqToEntity(this.entityChast) < 64.0D);
		}

		return false;
	}

	@Override
	public void openInventory(EntityPlayer player)
	{
		super.markDirty();

		this.entityChast.setAITrading(player);
	}

	@Override
	public void closeInventory(EntityPlayer player)
	{
		super.markDirty();

		this.entityChast.setAITrading(null);
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
