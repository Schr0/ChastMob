package schr0.chastmob;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class InventoryChast extends InventoryBasic
{

	private static final int INVENTORY_SIZE = (3 * 9);

	private EntityChast entityChast;

	public InventoryChast(EntityChast entityChast)
	{
		super(entityChast.getName(), false, INVENTORY_SIZE);

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

		this.entityChast.setTrading(player);
	}

	@Override
	public void closeInventory(EntityPlayer player)
	{
		super.markDirty();

		this.entityChast.setOpen(false);

		this.entityChast.setTrading(null);
	}

	// TODO /* ======================================== MOD START =====================================*/

	public void readInventoryFromNBT(NBTTagList nbtlist)
	{
		int slot;

		for (slot = 0; slot < this.getSizeInventory(); ++slot)
		{
			this.setInventorySlotContents(slot, ChastMobHelper.getEmptyItemStack());
		}

		for (slot = 0; slot < nbtlist.tagCount(); ++slot)
		{
			NBTTagCompound nbttagCompound = nbtlist.getCompoundTagAt(slot);
			slot = nbttagCompound.getByte("Slot") & 255;

			if ((0 <= slot) && (slot < this.getSizeInventory()))
			{
				this.setInventorySlotContents(slot, ItemStack.loadItemStackFromNBT(nbttagCompound));
			}
		}
	}

	public NBTTagList writeInventoryToNBT()
	{
		NBTTagList nbttagList = new NBTTagList();

		for (int slotsize = 0; slotsize < this.getSizeInventory(); ++slotsize)
		{
			ItemStack itemStack = this.getStackInSlot(slotsize);

			if (ChastMobHelper.isNotEmptyItemStack(itemStack))
			{
				NBTTagCompound nbttagCompound = new NBTTagCompound();
				nbttagCompound.setByte("Slot", (byte) slotsize);
				itemStack.writeToNBT(nbttagCompound);
				nbttagList.appendTag(nbttagCompound);
			}
		}

		return nbttagList;
	}

}
