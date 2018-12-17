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
		this.targetEntityItem = this.getNearestEntityItem();

		if (this.targetEntityItem != null)
		{
			return true;
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
			for (EntityItem aroundEntityItem : this.getAroundEntityItems())
			{
				if (this.areEntityItemEqual(this.targetEntityItem, aroundEntityItem))
				{
					if (!this.getEntity().isCoverOpen())
					{
						this.getEntity().setCoverOpen(true);

						return;
					}

					TileEntityHopper.putDropInInventoryAllSlots((IInventory) null, this.getEntity().getInventoryMain(), aroundEntityItem);

					this.targetEntityItem = null;
				}
			}
		}
		else
		{
			this.getEntity().getNavigator().tryMoveToEntityLiving(this.targetEntityItem, this.getSpeed());
		}
	}

	// TODO /* ======================================== MOD START =====================================*/

	@Nullable
	private EntityItem getNearestEntityItem()
	{
		EntityItem nearestEntityItem = null;
		double rangeOrigin = 0;

		for (EntityItem aroundEntityItem : this.getAroundEntityItems())
		{
			if (this.canCollectItem(aroundEntityItem))
			{
				double range = this.getEntity().getDistanceSq(aroundEntityItem);

				if ((range < rangeOrigin) || (rangeOrigin == 0))
				{
					rangeOrigin = range;

					nearestEntityItem = aroundEntityItem;
				}
			}
		}

		return nearestEntityItem;
	}

	private List<EntityItem> getAroundEntityItems()
	{
		BlockPos pos = this.getEntity().getCenterPosition();

		return this.getWorld().getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos).grow(this.getRange(), this.getRange(), this.getRange()));
	}

	private boolean canCollectItem(EntityItem entityItem)
	{
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
				return InventoryChastHelper.canStoreInventory(this.getEntity().getInventoryMain(), entityItem.getItem());
			}
		}

		return false;
	}

	private boolean areEntityItemEqual(EntityItem entityItemA, EntityItem entityItemB)
	{
		if ((entityItemA != null) && (entityItemB != null))
		{
			return ItemStack.areItemsEqual(entityItemA.getItem(), entityItemB.getItem());
		}

		return false;
	}

}
