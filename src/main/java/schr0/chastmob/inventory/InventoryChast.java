package schr0.chastmob.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import schr0.chastmob.ChastMobHelper;
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

			return (player.getDistanceSq(this.entityChast) < 64.0D);
		}

		return false;
	}

	// TODO /* ======================================== MOD START =====================================*/

	public EntityChast getContainerEntityChast()
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
