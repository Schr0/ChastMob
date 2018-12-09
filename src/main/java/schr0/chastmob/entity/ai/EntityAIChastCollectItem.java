package schr0.chastmob.entity.ai;

import java.util.List;

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
	private EntityItem targetEntityItem;

	public EntityAIChastCollectItem(EntityChast entityChast)
	{
		super(entityChast);
		this.targetEntityItem = null;
	}

	@Override
	public boolean shouldExecute()
	{
		this.targetEntityItem = null;

		float rangeOrigin = 0.0F;

		for (EntityItem entityItem : this.getAroundItems())
		{
			float range = (float) this.getEntity().getDistanceSq(entityItem);

			if ((range < rangeOrigin) || (rangeOrigin == 0.0F))
			{
				rangeOrigin = range;

				if (this.canCollectItem(entityItem))
				{
					if (InventoryChastHelper.canStoreInventory(this.getEntity().getInventoryMain(), entityItem.getItem()))
					{
						this.targetEntityItem = entityItem;

						return true;
					}
				}
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

		return (this.targetEntityItem != null);
	}

	@Override
	public void resetTask()
	{
		super.resetTask();

		this.targetEntityItem = null;
	}

	@Override
	public void updateTask()
	{
		super.updateTask();

		this.getEntity().getLookHelper().setLookPositionWithEntity(this.targetEntityItem, this.getEntity().getHorizontalFaceSpeed(), this.getEntity().getVerticalFaceSpeed());

		if (this.getEntity().getDistanceSq(this.targetEntityItem) < COLLECT_RANGE)
		{
			for (EntityItem entityItem : this.getAroundItems())
			{
				if (entityItem.equals(this.targetEntityItem) && this.canCollectItem(entityItem))
				{
					if (!this.getEntity().isCoverOpen())
					{
						this.getEntity().setCoverOpen(true);

						return;
					}

					TileEntityHopper.putDropInInventoryAllSlots((IInventory) null, this.getEntity().getInventoryMain(), entityItem);

					this.targetEntityItem = null;

					return;
				}
			}
		}
		else
		{
			this.getEntity().getNavigator().tryMoveToEntityLiving(this.targetEntityItem, this.getSpeed());
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

	private List<EntityItem> getAroundItems()
	{
		BlockPos pos = this.getEntity().getCenterPosition();

		return this.getWorld().getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos).grow(this.getRange(), this.getRange(), this.getRange()));
	}

}
