package schr0.chastmob.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import schr0.chastmob.ChastMobHelper;

public class InventoryChast extends InventoryBasic
{

	private EntityChast theChast;

	public InventoryChast(EntityChast entityChast)
	{
		super(entityChast.getName(), true, (9 * 3));

		this.theChast = entityChast;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player)
	{
		if (this.theChast.isEntityAlive())
		{
			if (this.theChast.isStatePanic())
			{
				return false;
			}

			return (player.getDistanceSqToEntity(this.theChast) <= 64.0D);
		}

		return false;
	}

	@Override
	public void openInventory(EntityPlayer player)
	{
		super.markDirty();

		this.theChast.setAITrading(player);
	}

	@Override
	public void closeInventory(EntityPlayer player)
	{
		super.markDirty();

		this.theChast.setAITrading(null);
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
				this.setInventorySlotContents(slot, ItemStack.loadItemStackFromNBT(nbt));
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

}
