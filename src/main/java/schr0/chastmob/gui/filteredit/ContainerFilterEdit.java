package schr0.chastmob.gui.filteredit;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import schr0.chastmob.ChastMobHelper;
import schr0.chastmob.inventory.InventoryFilterEdit;
import schr0.chastmob.inventory.InventoryFilterResult;

public class ContainerFilterEdit extends Container
{

	private InventoryFilterEdit inventoryFilterEdit;
	private ItemStack stackItemFilter;
	private EntityPlayer entityPlayer;

	public ContainerFilterEdit(ItemStack stack, InventoryFilterEdit inventoryFilterEdit, InventoryFilterResult inventoryFilterResult, EntityPlayer entityPlayer)
	{
		int column;
		int row;
		int index;

		inventoryFilterEdit.openInventory(entityPlayer);

		for (column = 0; column < 3; ++column)
		{
			for (row = 0; row < 3; ++row)
			{
				index = (row + column * 3);

				this.addSlotToContainer(new Slot(inventoryFilterEdit, index, (row * 20) + 12, (column * 20) + 22)
				{

					@Override
					public int getSlotStackLimit()
					{
						return 1;
					}

				});
			}
		}

		entityPlayer.inventory.openInventory(entityPlayer);

		for (column = 0; column < 3; ++column)
		{
			for (row = 0; row < 9; ++row)
			{
				index = (row + column * 9 + 9);

				this.addSlotToContainer(new Slot(entityPlayer.inventory, index, (row * 18) + 8, (column * 18) + 96));
			}
		}

		for (row = 0; row < 9; ++row)
		{
			index = row;

			this.addSlotToContainer(new Slot(entityPlayer.inventory, index, (row * 18) + 8, 154));
		}

		this.inventoryFilterEdit = inventoryFilterEdit;
		this.stackItemFilter = stack;
		this.entityPlayer = entityPlayer;
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn)
	{
		ItemStack stackMainhand = playerIn.getHeldItemMainhand();

		if (stackMainhand.isEmpty() || this.stackItemFilter.isEmpty())
		{
			return false;
		}
		else
		{
			return ItemStack.areItemStackTagsEqual(stackMainhand, this.stackItemFilter);
		}
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

		// int indexCraft = 9;

		if (index < 9)
		{
			if (!this.mergeItemStack(dstItemStack, 9, this.inventorySlots.size(), true))
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
		for (int slot = 0; slot < this.inventoryFilterEdit.getSizeInventory(); ++slot)
		{
			ItemStack stackSlot = this.inventoryFilterEdit.getStackInSlot(slot);

			if (!this.entityPlayer.inventory.addItemStackToInventory(stackSlot))
			{
				this.entityPlayer.dropItem(stackSlot, false);
			}
		}

		this.inventoryFilterEdit.clear();

		super.onContainerClosed(playerIn);
	}

	// TODO /* ======================================== MOD START =====================================*/

	public IInventory getInventoryFilterEdit()
	{
		return this.inventoryFilterEdit;
	}

}
