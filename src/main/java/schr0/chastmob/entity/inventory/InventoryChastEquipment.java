package schr0.chastmob.entity.inventory;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import schr0.chastmob.entity.EntityChast;

public class InventoryChastEquipment extends InventoryChast
{

	public InventoryChastEquipment(EntityChast entityChast)
	{
		super(entityChast, 4);
	}

	// TODO /* ======================================== MOD START =====================================*/

	@Nullable
	public ItemStack getHeadItem()
	{
		return this.getStackInSlot(0);
	}

	@Nullable
	public ItemStack getMainhandItem()
	{
		return this.getStackInSlot(1);
	}

	@Nullable
	public ItemStack getOffhandItem()
	{
		return this.getStackInSlot(2);
	}

	@Nullable
	public ItemStack getSpecificationItem()
	{
		return this.getStackInSlot(3);
	}

}
