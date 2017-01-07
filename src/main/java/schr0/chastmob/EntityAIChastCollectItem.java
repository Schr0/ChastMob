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
	private static final double SEARCH_XYZ = 5.0D;
	private static final double MOVE_SPEED = 1.25D;

	private EntityItem targetEntityItem;
	private int collectTime;

	public EntityAIChastCollectItem(EntityChast entityChast)
	{
		super(entityChast);
		this.setMutexBits(1);
	}

	@Override
	public boolean shouldExecute()
	{
		List<EntityItem> listEntityItem = this.getAIOwnerWorld().getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(this.getAIOwnerBlockPos()).expandXyz(SEARCH_XYZ));
		TreeMap<Double, EntityItem> treeMapEntityItem = new TreeMap<Double, EntityItem>();

		for (EntityItem entityItem : listEntityItem)
		{
			if (this.canCollectEntityItem(entityItem))
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
		this.getAIOwnerEntity().getNavigator().clearPathEntity();
		this.getAIOwnerEntity().setOpen(false);
	}

	@Override
	public void resetTask()
	{
		this.getAIOwnerEntity().getNavigator().clearPathEntity();
		this.getAIOwnerEntity().setOpen(false);

		this.setCollecting(null, 0);
	}

	@Override
	public void updateTask()
	{
		if (!ChastMobHelper.canStoreInventory(this.getAIOwnerInventory(), this.targetEntityItem.getEntityItem()))
		{
			this.setCollecting(null, 0);

			return;
		}

		--this.collectTime;

		this.getAIOwnerEntity().getLookHelper().setLookPositionWithEntity(this.targetEntityItem, 10.0F, this.getAIOwnerEntity().getVerticalFaceSpeed());

		if (this.getAIOwnerEntity().getDistanceSqToEntity(this.targetEntityItem) < 1.5D)
		{
			List<EntityItem> listEntityItem = this.getAIOwnerWorld().getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(this.getAIOwnerBlockPos()).expandXyz(SEARCH_XYZ));

			for (EntityItem entityItem : listEntityItem)
			{
				if (entityItem.equals(this.targetEntityItem) && this.canCollectEntityItem(entityItem))
				{
					TileEntityHopper.putDropInInventoryAllSlots(this.getAIOwnerInventory(), entityItem);

					this.getAIOwnerEntity().setOpen(true);

					this.setCollecting(null, 0);

					return;
				}
			}
		}
		else
		{
			this.getAIOwnerEntity().getNavigator().tryMoveToEntityLiving(this.targetEntityItem, MOVE_SPEED);
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

	private boolean canCollectEntityItem(EntityItem entityItem)
	{
		if (this.getAIOwnerEntity().getEntitySenses().canSee(entityItem))
		{
			return (entityItem.isEntityAlive() && !entityItem.cannotPickup());
		}

		return false;
	}

}
