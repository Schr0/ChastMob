package schr0.chastmob.entity.inventory;

import net.minecraft.item.ItemStack;
import schr0.chastmob.entity.EntityChast;

public class InventoryChastEquipment extends InventoryChast
{

	public InventoryChastEquipment(EntityChast entityChast)
	{
		super(entityChast, 5);
	}

	// TODO /* ======================================== MOD START =====================================*/

	public ItemStack getHeadItem()
	{
		return this.getStackInSlot(0);
	}

	public ItemStack getMainhandItem()
	{
		return this.getStackInSlot(1);
	}

	public ItemStack getOffhandItem()
	{
		return this.getStackInSlot(2);
	}

	public ItemStack getFilterItem()
	{
		return this.getStackInSlot(3);
	}

	public ItemStack getModeItem()
	{
		return this.getStackInSlot(4);
	}

}
