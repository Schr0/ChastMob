package schr0.chastmob;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Nullable;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.math.AxisAlignedBB;

public class EntityAIChastCollectItem extends EntityAIChast
{

	private static final int COLLECT_TIME_LIMIT = (5 * 20);

	private double moveSpeed;
	private double maxDistance;
	private EntityItem targetEntityItem;
	private int collectTime;

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
		List<EntityItem> listEntityItem = this.getAIWorld().getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(this.getAIOwnerBlockPos()).expandXyz(this.maxDistance));
		TreeMap<Double, EntityItem> treeMapEntityItem = new TreeMap<Double, EntityItem>();

		for (EntityItem entityItem : listEntityItem)
		{
			if (this.canCollecting(entityItem))
			{
				treeMapEntityItem.put(this.getAIOwnerEntity().getDistanceSqToEntity(entityItem), entityItem);
			}
		}

		for (Map.Entry<Double, EntityItem> entry : treeMapEntityItem.entrySet())
		{
			if (ChastMobHelper.canStoreInventory(this.getAIOwnerInventory(), entry.getValue().getEntityItem()))
			{
				this.setCollecting(entry.getValue(), COLLECT_TIME_LIMIT);

				return true;
			}
		}

		return false;
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

		this.setCollecting(null, 0);
	}

	@Override
	public void updateTask()
	{
		--this.collectTime;

		this.getAIOwnerEntity().getLookHelper().setLookPositionWithEntity(this.targetEntityItem, 10.0F, this.getAIOwnerEntity().getVerticalFaceSpeed());

		if (this.getAIOwnerEntity().getDistanceSqToEntity(this.targetEntityItem) < 1.5D)
		{
			List<EntityItem> listEntityItem = this.getAIWorld().getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(this.getAIOwnerBlockPos()).expandXyz(this.maxDistance));

			for (EntityItem entityItem : listEntityItem)
			{
				if (entityItem.equals(this.targetEntityItem) && this.canCollecting(entityItem))
				{
					TileEntityHopper.putDropInInventoryAllSlots(this.getAIOwnerInventory(), entityItem);

					this.getAIOwnerEntity().setCoverOpen(true);

					this.setCollecting(null, 0);

					return;
				}
			}
		}
		else
		{
			this.getAIOwnerEntity().getNavigator().tryMoveToEntityLiving(this.targetEntityItem, this.moveSpeed);
		}
	}

	// TODO /* ======================================== MOD START =====================================*/

	private boolean isCollecting()
	{
		return (this.targetEntityItem != null) && (0 < this.collectTime);
	}

	private void setCollecting(@Nullable EntityItem entityItem, int collectTime)
	{
		this.targetEntityItem = entityItem;
		this.collectTime = collectTime;
	}

	private boolean canCollecting(EntityItem entityItem)
	{
		if (this.getAIOwnerEntity().getEntitySenses().canSee(entityItem))
		{
			return (entityItem.isEntityAlive() && !entityItem.cannotPickup());
		}

		return false;
	}

}
