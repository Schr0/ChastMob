package schr0.chastmob.entity.inventory;

import javax.annotation.Nullable;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import schr0.chastmob.entity.EntityChast;

public class InventoryChastEquipment extends InventoryChast
{

	public InventoryChastEquipment(EntityChast entityChast)
	{
		super(entityChast, 4);
	}

	@Override
	public void setInventorySlotContents(int index, @Nullable ItemStack stack)
	{
		switch (index)
		{
			case 0 :

				this.getOwnerEntity().setItemStackToSlot(EntityEquipmentSlot.HEAD, stack);
				break;

			case 1 :

				this.getOwnerEntity().setItemStackToSlot(EntityEquipmentSlot.MAINHAND, stack);
				break;

			case 2 :

				this.getOwnerEntity().setItemStackToSlot(EntityEquipmentSlot.OFFHAND, stack);
				break;
		}

		super.setInventorySlotContents(index, stack);
	}

	// TODO /* ======================================== MOD START =====================================*/

	@Nullable
	public ItemStack getSpecialItem()
	{
		return this.getStackInSlot(3);
	}

}
