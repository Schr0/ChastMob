package schr0.chastmob;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ChastMobHelper
{

	public static boolean isNotEmptyItemStack(ItemStack stack)
	{
		return (stack != null);
	}

	public static ItemStack getEmptyItemStack()
	{
		return (ItemStack) null;
	}

	public static boolean canStoreInventory(IInventory inventory, @Nullable ItemStack stack)
	{
		boolean hasEmptySlot = (getFirstEmptySlot(inventory) != -1);

		if (isNotEmptyItemStack(stack))
		{
			boolean hasCanStoreSlot = (getCanStoreSlot(inventory, stack) != -1);

			if (hasEmptySlot)
			{
				return true;
			}
			else
			{
				return hasCanStoreSlot;
			}
		}
		else
		{
			return hasEmptySlot;
		}
	}

	public static int getFirstEmptySlot(IInventory inventory)
	{
		for (int slot = 0; slot < inventory.getSizeInventory(); ++slot)
		{
			if (!isNotEmptyItemStack(inventory.getStackInSlot(slot)))
			{
				return slot;
			}
		}

		return -1;
	}

	public static int getCanStoreSlot(IInventory inventory, ItemStack stack)
	{
		for (int slot = 0; slot < inventory.getSizeInventory(); ++slot)
		{
			ItemStack stackInv = inventory.getStackInSlot(slot);

			if (isNotEmptyItemStack(stackInv))
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

	public static boolean canBlockBeSeen(Entity entity, BlockPos blockPos)
	{
		World world = entity.getEntityWorld();
		IBlockState state = world.getBlockState(blockPos);

		if (state == null)
		{
			return false;
		}

		Vec3d entityVec3d = new Vec3d(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
		Vec3d targetVec3d = new Vec3d(((double) blockPos.getX() + 0.5D), ((double) blockPos.getY() + (state.getCollisionBoundingBox(world, blockPos).minY + state.getCollisionBoundingBox(world, blockPos).maxY) * 0.9D), ((double) blockPos.getZ() + 0.5D));
		RayTraceResult rayTraceResult = world.rayTraceBlocks(entityVec3d, targetVec3d);

		if (rayTraceResult != null && rayTraceResult.typeOfHit.equals(RayTraceResult.Type.BLOCK))
		{
			if (rayTraceResult.getBlockPos().equals(blockPos))
			{
				return true;
			}
		}

		return false;
	}

}
