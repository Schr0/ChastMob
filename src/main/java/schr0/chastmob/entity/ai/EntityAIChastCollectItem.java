package schr0.chastmob.entity.ai;

import java.util.List;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.math.AxisAlignedBB;
import schr0.chastmob.entity.EntityChast;
import schr0.chastmob.inventory.InventoryChastHelper;

public class EntityAIChastCollectItem extends EntityAIChast
{

	private double speed;
	private double distance;
	private EntityItem targetEntityItem;

	public EntityAIChastCollectItem(EntityChast entityChast, double speed, double distance)
	{
		super(entityChast);
		this.speed = speed;
		this.distance = distance;
		this.targetEntityItem = null;
	}

	@Override
	public boolean shouldExecute()
	{
		this.targetEntityItem = null;

		float rangeOrigin = (float) (this.distance * this.distance * this.distance * 2);

		for (EntityItem entityItem : this.getAroundEntityItem())
		{
			if (this.canCollectEntityItem(entityItem))
			{
				float range = (float) this.getEntity().getDistanceSq(entityItem);

				if (range < rangeOrigin)
				{
					rangeOrigin = range;

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
		if (this.targetEntityItem != null)
		{
			return this.isExecutingTime();
		}

		return false;
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

		if (this.getEntity().getDistanceSq(this.targetEntityItem) < 1.5D)
		{
			for (EntityItem entityItem : this.getAroundEntityItem())
			{
				if (entityItem.equals(this.targetEntityItem) && this.canCollectEntityItem(entityItem))
				{
					TileEntityHopper.putDropInInventoryAllSlots((IInventory) null, this.getEntity().getInventoryMain(), entityItem);

					this.getEntity().setCoverOpen(true);

					this.targetEntityItem = null;

					return;
				}
			}
		}
		else
		{
			this.getEntity().getNavigator().tryMoveToEntityLiving(this.targetEntityItem, this.speed);
		}
	}

	// TODO /* ======================================== MOD START =====================================*/

	private List<EntityItem> getAroundEntityItem()
	{
		return this.getWorld().getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(this.getHomePosition()).grow(this.distance, this.distance, this.distance));
	}

	private boolean canCollectEntityItem(EntityItem entityItem)
	{
		if (this.getEntity().getEntitySenses().canSee(entityItem) && entityItem.isEntityAlive() && !entityItem.cannotPickup())
		{
			return true;
		}

		return false;
	}

}
