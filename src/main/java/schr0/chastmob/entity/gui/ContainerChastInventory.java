package schr0.chastmob.entity.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import schr0.chastmob.ChastMobHelper;
import schr0.chastmob.entity.EntityChast;

public class ContainerChastInventory extends Container
{

	private EntityChast theChast;
	private EntityPlayer thePlayer;

	public ContainerChastInventory(EntityChast entityChast, EntityPlayer entityPlayer)
	{
		int column;
		int row;

		entityChast.getInventoryChast().openInventory(entityPlayer);

		for (column = 0; column < 3; ++column)
		{
			for (row = 0; row < 9; ++row)
			{
				this.addSlotToContainer(new Slot(entityChast.getInventoryChast(), (row + column * 9), (row * 18) + 8, (column * 18) + 74));
			}
		}

		for (column = 0; column < 3; ++column)
		{
			for (row = 0; row < 9; ++row)
			{
				this.addSlotToContainer(new Slot(entityPlayer.inventory, (row + column * 9 + 9), (row * 18) + 8, (column * 18) + 140));
			}
		}

		for (row = 0; row < 9; ++row)
		{
			this.addSlotToContainer(new Slot(entityPlayer.inventory, row, (row * 18) + 8, 198));
		}

		this.theChast = entityChast;
		this.thePlayer = entityPlayer;
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn)
	{
		return this.theChast.getInventoryChast().isUseableByPlayer(playerIn);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index)
	{
		Slot slot = (Slot) this.inventorySlots.get(index);

		if (slot == null || slot.getHasStack() == false)
		{
			return null;
		}

		ItemStack srcItemStack = slot.getStack();
		ItemStack dstItemStack = srcItemStack.copy();

		// mergeItemStack(移動するItemStack, 移動先の最小スロット番号, 移動先の最大スロット番号, 昇順or降順)

		if (index < 27)
		{
			if (!this.mergeItemStack(dstItemStack, 27, this.inventorySlots.size(), true))
			{
				return null;
			}
		}
		else
		{
			if (!this.mergeItemStack(dstItemStack, 0, 27, false))
			{
				return null;
			}
		}

		if (dstItemStack.stackSize == 0)
		{
			slot.putStack(ChastMobHelper.getEmptyItemStack());
		}
		else
		{
			slot.onSlotChanged();
		}

		if (dstItemStack.stackSize == srcItemStack.stackSize)
		{
			return null;
		}

		slot.onPickupFromSlot(player, dstItemStack);

		return srcItemStack;
	}

	@Override
	public void onContainerClosed(EntityPlayer playerIn)
	{
		this.theChast.getInventoryChast().closeInventory(playerIn);

		super.onContainerClosed(playerIn);
	}

}
