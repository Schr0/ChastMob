package schr0.chastmob.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import schr0.chastmob.ChastMobHelper;

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
			if (this.entityChast.isStatePanic())
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
		this.entityChast.setCoverOpen(true);
		this.entityChast.setAITrading(player);
	}

	@Override
	public void closeInventory(EntityPlayer player)
	{
		super.markDirty();
		this.entityChast.setCoverOpen(false);
		this.entityChast.setAITrading(null);
	}

	// TODO /* ======================================== MOD START =====================================*/

	public void readInventoryFromNBT(NBTTagList nbtlist)
	{
		this.clear();

		for (int slot = 0; slot < nbtlist.tagCount(); ++slot)
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

		for (int slot = 0; slot < this.getSizeInventory(); ++slot)
		{
			ItemStack stack = this.getStackInSlot(slot);

			if (ChastMobHelper.isNotEmptyItemStack(stack))
			{
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setByte("Slot", (byte) slot);
				stack.writeToNBT(nbttagcompound);
				nbttagList.appendTag(nbttagcompound);
			}
		}

		return nbttagList;
	}

}
