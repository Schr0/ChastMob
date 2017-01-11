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
		List<EntityItem> listEntityItem = this.getAIWorld().getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(this.getAIBlockPos()).expandXyz(this.maxDistance));
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
				this.setCollecting(TIME_LIMIT, entry.getValue());

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
		this.setCollecting(0, null);
	}

	@Override
	public void updateTask()
	{
		--this.timeCounter;

		this.getAIOwnerEntity().getLookHelper().setLookPositionWithEntity(this.targetEntityItem, 10.0F, this.getAIOwnerEntity().getVerticalFaceSpeed());

		if (this.getAIOwnerEntity().getDistanceSqToEntity(this.targetEntityItem) < 1.5D)
		{
			List<EntityItem> listEntityItem = this.getAIWorld().getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(this.getAIBlockPos()).expandXyz(this.maxDistance));

			for (EntityItem entityItem : listEntityItem)
			{
				if (entityItem.equals(this.targetEntityItem) && this.canCollecting(entityItem))
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
			this.getAIOwnerEntity().getNavigator().tryMoveToEntityLiving(this.targetEntityItem, this.moveSpeed);
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

	private boolean canCollecting(EntityItem entityItem)
	{
		if (this.getAIOwnerEntity().getEntitySenses().canSee(entityItem))
		{
			return (entityItem.isEntityAlive() && !entityItem.cannotPickup());
		}

		return false;
	}

}
