package schr0.chastmob.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import schr0.chastmob.api.ItemChastHelmet;
import schr0.chastmob.entity.EntityChast;

public class ContainerChastInventory extends Container
{

	private EntityChast entityChast;
	private EntityPlayer entityPlayer;

	public ContainerChastInventory(EntityChast entityChast, EntityPlayer entityPlayer)
	{
		int column;
		int row;
		int index;

		entityChast.getInventoryEquipments().openInventory(entityPlayer);

		for (index = 0; index < 3; ++index)
		{
			switch (index)
			{
				case 0 :

					this.addSlotToContainer(new Slot(entityChast.getInventoryEquipments(), index, 8, 18)
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

					this.addSlotToContainer(new Slot(entityChast.getInventoryEquipments(), index, 8, 36));

					break;

				case 2 :

					this.addSlotToContainer(new Slot(entityChast.getInventoryEquipments(), index, 8, 54));

					break;
			}
		}

		entityChast.getInventoryMain().openInventory(entityPlayer);

		for (column = 0; column < 3; ++column)
		{
			for (row = 0; row < 9; ++row)
			{
				index = (row + column * 9);

				this.addSlotToContainer(new Slot(entityChast.getInventoryMain(), index, (row * 18) + 8, (column * 18) + 74));
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
		return this.entityChast.getInventoryMain().isUsableByPlayer(playerIn);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index)
	{
		ItemStack stackEmpty = ItemStack.EMPTY;
		Slot slot = (Slot) this.inventorySlots.get(index);

		if (slot == null || slot.getHasStack() == false)
		{
			return stackEmpty;
		}

		ItemStack srcItemStack = slot.getStack();
		ItemStack dstItemStack = srcItemStack.copy();

		// mergeItemStack(移動するItemStack, 移動先の最小スロット番号, 移動先の最大スロット番号, 昇順or降順)

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
			slot.putStack(stackEmpty);
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
		this.entityChast.getInventoryMain().closeInventory(playerIn);
		this.entityChast.getInventoryEquipments().closeInventory(playerIn);
		this.entityPlayer.inventory.openInventory(playerIn);

		super.onContainerClosed(playerIn);
	}

}
