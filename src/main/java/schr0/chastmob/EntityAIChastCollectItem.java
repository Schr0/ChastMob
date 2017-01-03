package schr0.chastmob;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Nullable;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class EntityAIChastCollectItem extends EntityAIChast
{

	private static final double MOVE_SPEED = 1.25D;
	private static final double SEARCH_XYZ = 5.0D;
	private EntityItem targetItem;
	private int collectTime;

	public EntityAIChastCollectItem(EntityChast entityChast)
	{
		super(entityChast);
		this.setMutexBits(1);
	}

	@Override
	public boolean shouldExecute()
	{
		BlockPos pos = new BlockPos(this.getAIOwnerEntity());
		List<EntityItem> listEntityItem = this.getAIOwnerWorld().getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos).expandXyz(SEARCH_XYZ));
		TreeMap<Double, EntityItem> treeMapEntityItem = new TreeMap<Double, EntityItem>();

		for (EntityItem entityitem : listEntityItem)
		{
			if (entityitem.isEntityAlive() && this.getAIOwnerEntity().getEntitySenses().canSee(entityitem))
			{
				treeMapEntityItem.put(this.getAIOwnerEntity().getDistanceSqToEntity(entityitem), entityitem);
			}
		}

		for (Map.Entry<Double, EntityItem> entry : treeMapEntityItem.entrySet())
		{
			if (this.isNotFullInventory(this.getAIOwnerEntity().getInventoryChast(), entry.getValue().getEntityItem()))
			{
				this.setTargetEntityItem(entry.getValue(), (10 * 20));

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

		this.setTargetEntityItem(null, 0);
	}

	@Override
	public void updateTask()
	{
		--this.collectTime;

		if (!this.isNotFullInventory(this.getAIOwnerEntity().getInventoryChast(), this.targetItem.getEntityItem()))
		{
			this.setTargetEntityItem(null, 0);

			return;
		}

		if (this.getAIOwnerEntity().getDistanceSqToEntity(this.targetItem) < 1.5D)
		{
			BlockPos pos = new BlockPos(this.getAIOwnerEntity());
			List<EntityItem> listEntityItem = this.getAIOwnerWorld().getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos).expandXyz(SEARCH_XYZ));

			for (EntityItem entityItem : listEntityItem)
			{
				if (entityItem.isEntityAlive() && this.getAIOwnerEntity().getEntitySenses().canSee(entityItem))
				{
					if (this.targetItem.equals(entityItem))
					{
						TileEntityHopper.putDropInInventoryAllSlots(this.getAIOwnerEntity().getInventoryChast(), entityItem);

						this.getAIOwnerEntity().setOpen(true);

						this.setTargetEntityItem(null, 0);

						return;
					}
				}
			}
		}

		this.getAIOwnerEntity().getNavigator().tryMoveToEntityLiving(this.targetItem, MOVE_SPEED);

		this.getAIOwnerEntity().getLookHelper().setLookPositionWithEntity(this.targetItem, 10.0F, this.getAIOwnerEntity().getVerticalFaceSpeed());
	}

	// TODO /* ======================================== MOD START =====================================*/

	private void setTargetEntityItem(@Nullable EntityItem entityItem, int collectTime)
	{
		this.targetItem = entityItem;
		this.collectTime = collectTime;
	}

	private boolean isCollecting()
	{
		return (this.targetItem != null) && (0 < this.collectTime);
	}

	private boolean isNotFullInventory(IInventory inventory, @Nullable ItemStack stack)
	{
		if (stack == null)
		{
			return !(this.getFirstEmptySlot(inventory) == -1);
		}
		else
		{
			return !((this.getFirstEmptySlot(inventory) == -1) && (this.getCanStoreSlot(inventory, stack) == -1));
		}
	}

	private int getFirstEmptySlot(IInventory inventory)
	{
		for (int slot = 0; slot < inventory.getSizeInventory(); ++slot)
		{
			if (inventory.getStackInSlot(slot) == null)
			{
				return slot;
			}
		}

		return -1;
	}

	private int getCanStoreSlot(IInventory inventory, ItemStack stack)
	{
		for (int slot = 0; slot < inventory.getSizeInventory(); ++slot)
		{
			ItemStack stackInv = inventory.getStackInSlot(slot);

			if (stackInv != null)
			{
				if (stackInv.getItem().equals(stack.getItem()) && stackInv.isStackable() && stackInv.stackSize < stackInv.getMaxStackSize() && stackInv.stackSize < inventory.getInventoryStackLimit() && (!stackInv.getHasSubtypes() || stackInv.getItemDamage() == stack.getItemDamage()) && ItemStack.areItemStackTagsEqual(stackInv, stack))
				{
					return slot;
				}
			}
		}

		return -1;
	}

}
