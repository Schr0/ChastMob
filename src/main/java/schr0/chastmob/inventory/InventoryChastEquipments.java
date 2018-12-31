package schr0.chastmob.inventory;

import net.minecraft.item.ItemStack;
import schr0.chastmob.entity.EntityChast;

public class InventoryChastEquipments extends InventoryChast
{

	public InventoryChastEquipments(EntityChast entityChast)
	{
		super(entityChast, 3);
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

}
