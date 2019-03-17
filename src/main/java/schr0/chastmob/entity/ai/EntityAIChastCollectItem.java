package schr0.chastmob.entity.ai;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import schr0.chastmob.entity.EntityChast;
import schr0.chastmob.inventory.InventoryChast;

public class EntityAIChastCollectItem extends EntityAIChast
{

	private static final double COLLECT_RANGE = 1.5D;
	private static final int LIMIT_COLLECT_INTERVAL = 5;
	private EntityItem targetEntityItem;
	private int collectInterval;

	public EntityAIChastCollectItem(EntityChast entityChast)
	{
		super(entityChast);
	}

	@Override
	public boolean shouldExecute()
	{
		this.targetEntityItem = this.getCanCollectEntityItem();

		if (this.targetEntityItem != null)
		{
			this.collectInterval = 0;

			return true;
		}

		return false;
	}

	@Override
	public boolean shouldContinueExecuting()
	{
		if (0 < this.collectInterval)
		{
			return true;
		}

		if (this.canCollectEntityItem(this.targetEntityItem))
		{
			return true;
		}

		return false;
	}

	@Override
	public void updateTask()
	{
		super.updateTask();

		if (this.isTimeOut())
		{
			if (this.shouldExecute())
			{
				this.resetTime();
			}

			return;
		}

		this.getEntity().getLookHelper().setLookPositionWithEntity(this.targetEntityItem, this.getEntity().getHorizontalFaceSpeed(), this.getEntity().getVerticalFaceSpeed());

		if (0 < this.collectInterval)
		{
			this.getEntity().setCoverOpen(true);

			--this.collectInterval;
		}
		else
		{
			this.getEntity().setCoverOpen(false);

			if (this.getEntity().getDistanceSq(this.targetEntityItem) < COLLECT_RANGE)
			{
				for (EntityItem aroundEntityItem : this.getAroundEntityItems())
				{
					if (this.areEntityItemEqual(this.targetEntityItem, aroundEntityItem))
					{
						TileEntityHopper.putDropInInventoryAllSlots((IInventory) null, this.getEntity().getInventoryMain(), aroundEntityItem);

						this.collectInterval = LIMIT_COLLECT_INTERVAL;

						return;
					}
				}
			}
			else
			{
				this.getEntity().getNavigator().tryMoveToEntityLiving(this.targetEntityItem, this.getMoveSpeed());
			}
		}
	}

	// TODO /* ======================================== MOD START =====================================*/

	private boolean canCollectEntityItem(@Nullable EntityItem entityItem)
	{
		if (entityItem == null)
		{
			return false;
		}

		ItemStack stack = entityItem.getItem().copy();
		ItemStack stackMainhand = this.getEntity().getHeldItemMainhand().copy();
		ItemStack stackOffhand = this.getEntity().getHeldItemOffhand().copy();

		stack.setCount(1);
		stackMainhand.setCount(1);
		stackOffhand.setCount(1);

		if (!stackMainhand.isEmpty() && !ItemStack.areItemStacksEqual(stack, stackMainhand))
		{
			return false;
		}

		if (!stackOffhand.isEmpty() && ItemStack.areItemStacksEqual(stack, stackOffhand))
		{
			return false;
		}

		if (this.getEntity().getEntitySenses().canSee(entityItem))
		{
			if (entityItem.isEntityAlive() && !entityItem.cannotPickup())
			{
				return InventoryChast.canStoreInventory(this.getEntity().getInventoryMain(), entityItem.getItem());
			}
		}

		return false;
	}

	@Nullable
	private EntityItem getCanCollectEntityItem()
	{
		EntityItem canCollectEntityItem = null;
		double rangeOrigin = 0;

		for (EntityItem aroundEntityItem : this.getAroundEntityItems())
		{
			if (this.canCollectEntityItem(aroundEntityItem))
			{
				double range = this.getEntity().getDistanceSq(aroundEntityItem);

				if ((range < rangeOrigin) || (rangeOrigin == 0))
				{
					rangeOrigin = range;

					canCollectEntityItem = aroundEntityItem;
				}
			}
		}

		return canCollectEntityItem;
	}

	private List<EntityItem> getAroundEntityItems()
	{
		BlockPos pos = this.getEntity().getCenterPosition();
		double range = this.getSearchRange();

		return this.getWorld().getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos).grow(range, range, range));
	}

	private boolean areEntityItemEqual(EntityItem entityItemA, EntityItem entityItemB)
	{
		if ((entityItemA != null) && (entityItemB != null))
		{
			double rangeA = this.getEntity().getDistanceSq(entityItemA);
			double rangeB = this.getEntity().getDistanceSq(entityItemB);

			if (rangeA == rangeB)
			{
				return ItemStack.areItemsEqual(entityItemA.getItem(), entityItemB.getItem());
			}
		}

		return false;
	}

}
