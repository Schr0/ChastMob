package schr0.chastmob.gui.chastinventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import schr0.chastmob.ChastMobHelper;
import schr0.chastmob.api.ItemChastHelmet;
import schr0.chastmob.entity.EntityChast;
import schr0.chastmob.item.ItemFilter;
import schr0.chastmob.item.ItemMode;

public class ContainerChastInventory extends Container
{

	private EntityChast entityChast;
	private EntityPlayer entityPlayer;

	public ContainerChastInventory(EntityChast entityChast, EntityPlayer entityPlayer)
	{
		int column;
		int row;
		int index;

		entityChast.getInventoryChastEquipment().openInventory(entityPlayer);

		for (index = 0; index < 5; ++index)
		{
			switch (index)
			{
				case 0 :

					this.addSlotToContainer(new Slot(entityChast.getInventoryChastEquipment(), index, 8, 18)
					{

						@Override
						public boolean isItemValid(ItemStack stack)
						{
							if (stack.getItem() instanceof ItemChastHelmet)
							{
								return true;
							}

							return false;
						}

					});
					break;

				case 1 :

					this.addSlotToContainer(new Slot(entityChast.getInventoryChastEquipment(), index, 8, 36));
					break;

				case 2 :

					this.addSlotToContainer(new Slot(entityChast.getInventoryChastEquipment(), index, 80, 36));
					break;

				case 3 :

					this.addSlotToContainer(new Slot(entityChast.getInventoryChastEquipment(), index, 80, 54)
					{

						@Override
						public boolean isItemValid(ItemStack stack)
						{
							if (stack.getItem() instanceof ItemFilter)
							{
								if (((ItemFilter) stack.getItem()).hasInventoryFilter(stack))
								{
									return true;
								}
							}

							return false;
						}

					});
					break;

				case 4 :

					this.addSlotToContainer(new Slot(entityChast.getInventoryChastEquipment(), index, 149, 46)
					{

						@Override
						public boolean isItemValid(ItemStack stack)
						{
							if (stack.getItem() instanceof ItemMode)
							{
								if (((ItemMode) stack.getItem()).isItemValidForSlot(stack))
								{
									return true;
								}
							}

							return false;
						}

					});
					break;
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

		this.entityChast = entityChast;
		this.entityPlayer = entityPlayer;
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn)
	{
		return this.entityChast.getInventoryChastMain().isUsableByPlayer(playerIn);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index)
	{
		ItemStack stackEmpty = ChastMobHelper.getEmptyItemStack();
		Slot slot = (Slot) this.inventorySlots.get(index);

		if (slot == null || slot.getHasStack() == false)
		{
			return stackEmpty;
		}

		ItemStack srcItemStack = slot.getStack();
		ItemStack dstItemStack = srcItemStack.copy();

		// mergeItemStack(移動するItemStack, 移動先の最小スロット番号, 移動先の最大スロット番号, 昇順or降順)

		// int indexEquipment = 5;
		// int indexMain = 27;

		if (index < 32)
		{
			if (!this.mergeItemStack(dstItemStack, 32, this.inventorySlots.size(), true))
			{
				return stackEmpty;
			}
		}
		else
		{
			if (!this.mergeItemStack(dstItemStack, 5, 32, false))
			{
				return stackEmpty;
			}
		}

		if (dstItemStack.isEmpty())
		{
			slot.putStack(ChastMobHelper.getEmptyItemStack());
		}
		else
		{
			slot.onSlotChanged();
		}

		if (dstItemStack.getCount() == srcItemStack.getCount())
		{
			return stackEmpty;
		}

		slot.onTake(player, dstItemStack);

		return srcItemStack;
	}

	@Override
	public void onContainerClosed(EntityPlayer playerIn)
	{
		this.entityChast.getInventoryChastMain().closeInventory(playerIn);
		this.entityChast.getInventoryChastEquipment().closeInventory(playerIn);
		this.entityPlayer.inventory.openInventory(playerIn);

		super.onContainerClosed(playerIn);
	}

}
