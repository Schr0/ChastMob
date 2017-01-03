package schr0.chastmob;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class InventoryChast extends InventoryBasic
{

	private EntityChast entityChast;

	public InventoryChast(EntityChast entityChast)
	{
		super(entityChast.getName(), false, (3 * 9));

		this.entityChast = entityChast;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player)
	{
		if (this.entityChast.isEntityAlive())
		{
			if (this.entityChast.isPanic())
			{
				return false;
			}

			return (player.getDistanceSqToEntity(this.entityChast) <= 64.0D);
		}

		return false;
	}

	@Override
	public void openInventory(EntityPlayer player)
	{
		super.markDirty();

		this.entityChast.setOpen(true);

		this.entityChast.setAITradeFlag(player);
	}

	@Override
	public void closeInventory(EntityPlayer player)
	{
		super.markDirty();

		this.entityChast.setOpen(false);

		this.entityChast.setAITradeFlag(null);
	}

	// TODO /* ======================================== MOD START =====================================*/

	public void readInventoryFromNBT(NBTTagList nbtlist)
	{
		int slotsize;

		for (slotsize = 0; slotsize < this.getSizeInventory(); ++slotsize)
		{
			this.setInventorySlotContents(slotsize, (ItemStack) null);
		}

		for (slotsize = 0; slotsize < nbtlist.tagCount(); ++slotsize)
		{
			NBTTagCompound nbttagcompound = nbtlist.getCompoundTagAt(slotsize);
			int slot = nbttagcompound.getByte("Slot") & 255;

			if ((0 <= slot) && (slot < this.getSizeInventory()))
			{
				this.setInventorySlotContents(slot, ItemStack.loadItemStackFromNBT(nbttagcompound));
			}
		}
	}

	public NBTTagList writeInventoryToNBT()
	{
		NBTTagList nbttaglist = new NBTTagList();

		for (int slotsize = 0; slotsize < this.getSizeInventory(); ++slotsize)
		{
			ItemStack itemstack = this.getStackInSlot(slotsize);

			if (itemstack != null)
			{
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setByte("Slot", (byte) slotsize);
				itemstack.writeToNBT(nbttagcompound);
				nbttaglist.appendTag(nbttagcompound);
			}
		}

		return nbttaglist;
	}

}
