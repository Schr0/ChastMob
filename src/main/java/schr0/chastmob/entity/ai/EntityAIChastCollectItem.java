package schr0.chastmob.entity.ai;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.math.AxisAlignedBB;
import schr0.chastmob.ChastMobHelper;
import schr0.chastmob.entity.EntityChast;
import schr0.chastmob.inventory.InventoryFilter;
import schr0.chastmob.item.ItemFilter;

public class EntityAIChastCollectItem extends EntityAIChast
{

	private static final int TIME_LIMIT = (5 * 20);
	private int timeCounter;

	private double speed;
	private double distance;
	private EntityItem targetEntityItem;

	public EntityAIChastCollectItem(EntityChast entityChast, double speed, double distance)
	{
		super(entityChast);

		this.speed = speed;
		this.distance = distance;
	}

	@Override
	public boolean shouldExecute()
	{
		float rangeOrigin = (float) (this.distance * this.distance * this.distance * 2);

		for (EntityItem entityItem : this.getAroundEntityItem())
		{
			if (this.canCollectEntityItem(entityItem))
			{
				float range = (float) this.getOwnerEntity().getDistanceSqToEntity(entityItem);

				if (range < rangeOrigin)
				{
					rangeOrigin = range;

					if (ChastMobHelper.canStoreInventory(this.getOwnerInventoryMain(), entityItem.getItem()))
					{
						this.setCollecting(TIME_LIMIT, entityItem);
					}
				}
			}
		}

		return this.isCollecting();
	}

	@Override
	public boolean shouldContinueExecuting()
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

		this.getOwnerEntity().setCoverOpen(false);
	}

	@Override
	public void resetTask()
	{
		super.resetTask();

		this.getOwnerEntity().setCoverOpen(false);
		this.setCollecting(0, null);
	}

	@Override
	public void updateTask()
	{
		--this.timeCounter;

		this.getOwnerEntity().getLookHelper().setLookPositionWithEntity(this.targetEntityItem, this.getOwnerEntity().getHorizontalFaceSpeed(), this.getOwnerEntity().getVerticalFaceSpeed());

		if (this.getOwnerEntity().getDistanceSqToEntity(this.targetEntityItem) < 1.5D)
		{
			for (EntityItem entityItem : this.getAroundEntityItem())
			{
				if (entityItem.equals(this.targetEntityItem) && this.canCollectEntityItem(entityItem))
				{
					TileEntityHopper.putDropInInventoryAllSlots((IInventory) null, this.getOwnerInventoryMain(), entityItem);

					this.getOwnerEntity().setCoverOpen(true);

					this.setCollecting(0, null);

					return;
				}
			}
		}
		else
		{
			this.getOwnerEntity().getNavigator().tryMoveToEntityLiving(this.targetEntityItem, this.speed);
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
		return this.getOwnerWorld().getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(this.getOwnerHomePosition()).expand(this.distance, this.distance, this.distance));
	}

	private boolean canCollectEntityItem(EntityItem entityItem)
	{
		if (this.getOwnerEntity().getEntitySenses().canSee(entityItem) && entityItem.isEntityAlive() && !entityItem.cannotPickup())
		{
			InventoryFilter inventoryFilter = this.getOwnerEquipmentInventoryFilter();

			if (inventoryFilter != null)
			{
				ItemStack stackEntityItem = entityItem.getItem().copy();

				if (stackEntityItem.isItemStackDamageable())
				{
					stackEntityItem.setItemDamage(0);
				}

				stackEntityItem.setCount(1);

				for (int slot = 0; slot < inventoryFilter.getSizeInventory(); ++slot)
				{
					ItemStack stackSlot = inventoryFilter.getStackInSlot(slot);

					if (stackSlot.isEmpty())
					{
						continue;
					}

					if (ItemStack.areItemStacksEqual(stackEntityItem, stackSlot))
					{
						return (inventoryFilter.getType() == ItemFilter.Type.WHITE);
					}
				}

				return (inventoryFilter.getType() == ItemFilter.Type.BLACK);
			}
			else
			{
				return true;
			}
		}

		return false;
	}

}
