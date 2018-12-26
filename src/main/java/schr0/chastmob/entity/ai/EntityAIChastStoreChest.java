package schr0.chastmob.entity.ai;

import javax.annotation.Nullable;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import schr0.chastmob.entity.ChastMode;
import schr0.chastmob.entity.EntityChast;
import schr0.chastmob.inventory.InventoryChast;
import schr0.chastmob.inventory.InventoryChastMain;

public class EntityAIChastStoreChest extends EntityAIChast
{

	private static final double STORE_RANGE = 2.5D;
	private TileEntityChest targetHomeChest;

	public EntityAIChastStoreChest(EntityChast entityChast)
	{
		super(entityChast);

		this.targetHomeChest = null;
	}

	@Override
	public boolean shouldExecute()
	{
		if (this.getMode() == ChastMode.PATROL)
		{
			if (this.getEntity().getInventoryMain().isEmpty())
			{
				return false;
			}

			this.targetHomeChest = this.getCanStoreChest();

			if (this.targetHomeChest != null)
			{
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean shouldContinueExecuting()
	{
		if (this.isTimeOut())
		{
			return false;
		}

		return (this.targetHomeChest != null);
	}

	@Override
	public void resetTask()
	{
		super.resetTask();

		this.targetHomeChest = null;
	}

	@Override
	public void updateTask()
	{
		super.updateTask();

		BlockPos targetPos = this.targetHomeChest.getPos();

		this.getEntity().getLookHelper().setLookPosition(targetPos.getX(), targetPos.getY(), targetPos.getZ(), 10.0F, this.getEntity().getVerticalFaceSpeed());

		if (this.getEntity().getDistanceSqToCenter(targetPos) < STORE_RANGE)
		{
			TileEntityChest homeChest = this.getCanStoreChest();

			if (homeChest != null)
			{
				InventoryChastMain inventoryMain = this.getEntity().getInventoryMain();

				for (int slot = 0; slot < inventoryMain.getSizeInventory(); ++slot)
				{
					ItemStack stackSlot = inventoryMain.getStackInSlot(slot);

					if (!stackSlot.isEmpty())
					{
						if (!this.getEntity().isCoverOpen())
						{
							this.getEntity().setCoverOpen(true);

							return;
						}

						inventoryMain.setInventorySlotContents(slot, TileEntityHopper.putStackInInventoryAllSlots((IInventory) null, (IInventory) homeChest, stackSlot, EnumFacing.UP));
					}
				}

				this.targetHomeChest = null;

				return;

			}
		}
		else
		{
			this.getEntity().getNavigator().tryMoveToXYZ(targetPos.getX(), targetPos.getY(), targetPos.getZ(), this.getSpeed());
		}
	}

	// TODO /* ======================================== MOD START =====================================*/

	@Nullable
	private TileEntityChest getCanStoreChest()
	{
		TileEntityChest homeChest = this.getEntity().getCanSeeHomeChest(true);

		if (homeChest != null)
		{
			InventoryChastMain inventoryMain = this.getEntity().getInventoryMain();
			IInventory inventory = (IInventory) homeChest;

			for (int slot = 0; slot < inventoryMain.getSizeInventory(); ++slot)
			{
				ItemStack stackSlot = inventoryMain.getStackInSlot(slot);

				if (InventoryChast.canStoreInventory(inventory, stackSlot))
				{
					return homeChest;
				}
			}
		}

		return (TileEntityChest) null;
	}

}
