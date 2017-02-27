package schr0.chastmob.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextComponentTranslation;
import schr0.chastmob.ChastMobHelper;
import schr0.chastmob.init.ChastMobLang;

public class InventoryChast extends InventoryBasic
{

	private static final int INVENTORY_SIZE = (3 * 9);

	private EntityChast entityChast;

	public InventoryChast(EntityChast entityChast)
	{
		super(getInventoryName(entityChast), false, INVENTORY_SIZE);

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

	private static String getInventoryName(EntityChast entityChast)
	{
		return entityChast.getName() + " " + new TextComponentTranslation(ChastMobLang.ENTITY_CHAST_INVENTORY, new Object[0]).getFormattedText();
	}

}
