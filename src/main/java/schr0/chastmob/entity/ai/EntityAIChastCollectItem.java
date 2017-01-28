package schr0.chastmob.entity.ai;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.math.AxisAlignedBB;
import schr0.chastmob.entity.EntityChast;

public class EntityAIChastCollectItem extends EntityAIChast
{

	private static final int TIME_LIMIT = (5 * 20);

	private double moveSpeed;
	private double maxDistance;
	private int timeCounter;
	private EntityItem targetEntityItem;

	public EntityAIChastCollectItem(EntityChast entityChast, double speed, double distance)
	{
		super(entityChast);
		this.setMutexBits(1);

		this.moveSpeed = speed;
		this.maxDistance = distance;
	}

	@Override
	public boolean shouldExecute()
	{
		float rangeOrigin = (float) (this.maxDistance * this.maxDistance * this.maxDistance * 2);

		for (EntityItem entityItem : this.getAroundEntityItem())
		{
			if (this.canCollectEntityItem(entityItem))
			{
				float range = (float) this.getAIOwnerEntity().getDistanceSqToEntity(entityItem);

				if (range < rangeOrigin)
				{
					rangeOrigin = range;

					this.setCollecting(TIME_LIMIT, entityItem);
				}
			}
		}

		return this.isCollecting();
	}

	@Override
	public boolean continueExecuting()
	{
		if (this.isCollecting())
		{
			return true;
		}

		return false;
	}

	@Override
	public void startExecuting()
	{
		super.startExecuting();

		this.getAIOwnerEntity().setCoverOpen(false);
	}

	@Override
	public void resetTask()
	{
		super.resetTask();

		this.getAIOwnerEntity().setCoverOpen(false);
		this.setCollecting(0, null);
	}

	@Override
	public void updateTask()
	{
		--this.timeCounter;

		this.getAIOwnerEntity().getLookHelper().setLookPositionWithEntity(this.targetEntityItem, 10.0F, this.getAIOwnerEntity().getVerticalFaceSpeed());

		if (this.getAIOwnerEntity().getDistanceSqToEntity(this.targetEntityItem) < 1.5D)
		{
			for (EntityItem entityItem : this.getAroundEntityItem())
			{
				if (entityItem.equals(this.targetEntityItem) && this.canCollectEntityItem(entityItem))
				{
					TileEntityHopper.putDropInInventoryAllSlots(this.getAIOwnerInventory(), entityItem);

					this.getAIOwnerEntity().setCoverOpen(true);

					this.setCollecting(0, null);

					return;
				}
			}
		}
		else
		{
			this.tryMoveToTargetEntity(this.targetEntityItem, this.moveSpeed);
		}
	}

	// TODO /* ======================================== MOD START =====================================*/

	public boolean isCollecting()
	{
		return (0 < this.timeCounter) && (this.targetEntityItem != null);
	}

	public void setCollecting(int timeCounter, @Nullable EntityItem entityItem)
	{
		this.timeCounter = timeCounter;
		this.targetEntityItem = entityItem;
	}

	private List<EntityItem> getAroundEntityItem()
	{
		return this.getAIOwnerWorld().getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(this.getAIHomePosition()).expandXyz(this.maxDistance));
	}

	private boolean canCollectEntityItem(EntityItem entityItem)
	{
		if (this.getAIOwnerEntity().getEntitySenses().canSee(entityItem))
		{
			return (entityItem.isEntityAlive() && !entityItem.cannotPickup());
		}

		return false;
	}

}
