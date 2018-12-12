package schr0.chastmob.entity.ai;

import javax.annotation.Nullable;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import schr0.chastmob.entity.EntityChast;
import schr0.chastmob.inventory.InventoryChastHelper;

public class EntityAIChastCollectItem extends EntityAIChast
{

	private static final double COLLECT_RANGE = 1.5D;
	private EntityItem targetItem;

	public EntityAIChastCollectItem(EntityChast entityChast)
	{
		super(entityChast);

		this.targetItem = null;
	}

	@Override
	public boolean shouldExecute()
	{
		this.targetItem = this.getNearestItem();

		if (this.targetItem == null)
		{
			return false;
		}

		if (this.canCollectItem(this.targetItem))
		{
			if (InventoryChastHelper.canStoreInventory(this.getEntity().getInventoryMain(), this.targetItem.getItem()))
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

		return (this.targetItem != null);
	}

	@Override
	public void resetTask()
	{
		super.resetTask();

		this.targetItem = null;
	}

	@Override
	public void updateTask()
	{
		super.updateTask();

		this.getEntity().getLookHelper().setLookPositionWithEntity(this.targetItem, this.getEntity().getHorizontalFaceSpeed(), this.getEntity().getVerticalFaceSpeed());

		if (this.getEntity().getDistanceSq(this.targetItem) < COLLECT_RANGE)
		{
			EntityItem nearestEntityItem = this.getNearestItem();

			if (nearestEntityItem == null)
			{
				return;
			}

			if (nearestEntityItem.equals(this.targetItem) && this.canCollectItem(nearestEntityItem))
			{
				if (InventoryChastHelper.canStoreInventory(this.getEntity().getInventoryMain(), nearestEntityItem.getItem()))
				{
					if (!this.getEntity().isCoverOpen())
					{
						this.getEntity().setCoverOpen(true);

						return;
					}

					TileEntityHopper.putDropInInventoryAllSlots((IInventory) null, this.getEntity().getInventoryMain(), nearestEntityItem);

					this.targetItem = null;

					return;
				}
			}
		}
		else
		{
			this.getEntity().getNavigator().tryMoveToEntityLiving(this.targetItem, this.getSpeed());
		}
	}

	// TODO /* ======================================== MOD START =====================================*/

	private boolean canCollectItem(EntityItem entityItem)
	{
		if (entityItem.isEntityAlive() && !entityItem.cannotPickup())
		{
			return this.getEntity().getEntitySenses().canSee(entityItem);
		}

		return false;
	}

	@Nullable
	private EntityItem getNearestItem()
	{
		BlockPos pos = this.getEntity().getCenterPosition();
		int range = this.getRange();

		return (EntityItem) this.getWorld().findNearestEntityWithinAABB(EntityItem.class, new AxisAlignedBB(pos).grow(range, range, range), this.getEntity());
	}

}
