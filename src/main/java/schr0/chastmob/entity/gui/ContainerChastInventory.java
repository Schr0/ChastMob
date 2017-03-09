package schr0.chastmob.entity.gui;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import schr0.chastmob.ChastMobHelper;
import schr0.chastmob.entity.EntityChast;
import schr0.chastmob.init.ChastMobItems;
import schr0.chastmob.item.ItemMapHomeChest;

public class ContainerChastInventory extends Container
{

	private EntityChast theChast;
	private EntityPlayer thePlayer;

	public ContainerChastInventory(EntityChast entityChast, EntityPlayer entityPlayer)
	{
		int column;
		int row;
		int index;

		entityChast.getInventoryChastEquipment().openInventory(entityPlayer);

		for (column = 0; column < 4; ++column)
		{
			index = column;

			if (index == 3)
			{
				this.addSlotToContainer(new Slot(entityChast.getInventoryChastEquipment(), index, 149, 46)
				{

					@Override
					public boolean isItemValid(@Nullable ItemStack stack)
					{
						if (ChastMobHelper.isNotEmptyItemStack(stack) && (stack.getItem().equals(ChastMobItems.MAP_HOME_CHEST)))
						{
							ItemMapHomeChest itemMapHomeChest = (ItemMapHomeChest) stack.getItem();

							if (itemMapHomeChest.hasHomeChest(stack))
							{
								return true;
							}
						}

						return false;
					}

				});

			}
			else
			{
				this.addSlotToContainer(new Slot(entityChast.getInventoryChastEquipment(), index, 8, (column * 18) + 18));
			}
		}

		entityChast.getInventoryChastMain().openInventory(entityPlayer);

		for (column = 0; column < 3; ++column)
		{
			for (row = 0; row < 9; ++row)
			{
				index = (row + column * 9);

				this.addSlotToContainer(new Slot(entityChast.getInventoryChastMain(), index, (row * 18) + 8, (column * 18) + 74));
			}
		}

		entityPlayer.inventory.openInventory(entityPlayer);

		for (column = 0; column < 3; ++column)
		{
			for (row = 0; row < 9; ++row)
			{
				index = (row + column * 9 + 9);

				this.addSlotToContainer(new Slot(entityPlayer.inventory, index, (row * 18) + 8, (column * 18) + 140));
			}
		}

		for (row = 0; row < 9; ++row)
		{
			index = row;

			this.addSlotToContainer(new Slot(entityPlayer.inventory, index, (row * 18) + 8, 198));
		}

		this.theChast = entityChast;
		this.thePlayer = entityPlayer;
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn)
	{
		return this.theChast.getInventoryChastMain().isUseableByPlayer(playerIn);
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

		if (index < 31)
		{
			if (!this.mergeItemStack(dstItemStack, 31, this.inventorySlots.size(), true))
			{
				return null;
			}
		}
		else
		{
			if (!this.mergeItemStack(dstItemStack, 4, 31, false))
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
		this.theChast.getInventoryChastMain().closeInventory(playerIn);

		super.onContainerClosed(playerIn);
	}

}
