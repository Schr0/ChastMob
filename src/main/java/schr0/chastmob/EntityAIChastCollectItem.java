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
			if (this.canStoreInventory(this.getAIOwnerInventory(), entry.getValue().getEntityItem()))
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
		if (!this.canStoreInventory(this.getAIOwnerInventory(), this.targetEntityItem.getEntityItem()))
		{
			this.setCollecting(null, 0);

			return;
		}

		--this.collectTime;

		this.getAIOwnerEntity().getNavigator().tryMoveToEntityLiving(this.targetEntityItem, MOVE_SPEED);

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

			// TODO BUG FIX
			// this.setCollecting(null, 0);
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

	private boolean canStoreInventory(IInventory inventory, @Nullable ItemStack stack)
	{
		boolean hasEmptySlot = (this.getFirstEmptySlot(inventory) != -1);

		if (stack == null)
		{
			return hasEmptySlot;
		}
		else
		{
			boolean hasCanStoreSlot = (this.getCanStoreSlot(inventory, stack) != -1);

			if (hasEmptySlot)
			{
				return true;
			}
			else
			{
				return hasCanStoreSlot;
			}
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

			if (ChastMobVanillaHelper.isNotEmptyItemStack(stackInv))
			{
				boolean isItemEqual = (stackInv.getItem().equals(stack.getItem()) && (!stackInv.getHasSubtypes() || stackInv.getItemDamage() == stack.getItemDamage()) && ItemStack.areItemStackTagsEqual(stackInv, stack));
				boolean isStackSizeEqual = (stackInv.isStackable() && (stackInv.stackSize < stackInv.getMaxStackSize()) && (stackInv.stackSize < inventory.getInventoryStackLimit()));

				if (isItemEqual && isStackSizeEqual)
				{
					return slot;
				}
			}
		}

		return -1;
	}

}
