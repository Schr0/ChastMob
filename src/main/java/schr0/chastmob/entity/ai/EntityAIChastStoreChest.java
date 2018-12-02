package schr0.chastmob.entity.ai;

import javax.annotation.Nullable;

import net.minecraft.block.BlockChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import schr0.chastmob.entity.ChastMode;
import schr0.chastmob.entity.EntityChast;
import schr0.chastmob.inventory.InventoryChastHelper;
import schr0.chastmob.inventory.InventoryChastMain;

public class EntityAIChastStoreChest extends EntityAIChast
{

	private TileEntityChest targetChest;

	public EntityAIChastStoreChest(EntityChast entityChast)
	{
		super(entityChast);
		this.targetChest = null;
	}

	@Override
	public boolean shouldExecute()
	{
		this.targetChest = null;

		if (this.getMode() == ChastMode.FOLLOW)
		{
			return false;
		}

		if (!InventoryChastHelper.canStoreInventory(this.getEntity().getInventoryMain(), ItemStack.EMPTY))
		{
			TileEntityChest nearOpenChest = this.getNearOpenChestTileEntity(this.getEntity(), this.getRange());

			if (this.canStoringTileEntityChest(nearOpenChest))
			{
				this.targetChest = nearOpenChest;

				return true;
			}
		}

		return false;
	}

	@Override
	public boolean shouldContinueExecuting()
	{
		if (this.targetChest != null)
		{
			return this.isExecutingTime();
		}

		return false;
	}

	@Override
	public void resetTask()
	{
		super.resetTask();

		if (this.targetChest != null)
		{
			this.forceMoveToTargetBlockPos(this.targetChest.getPos());
		}

		this.targetChest = null;
	}

	@Override
	public void updateTask()
	{
		super.updateTask();

		BlockPos targetBlockPos = this.targetChest.getPos();

		this.getEntity().getLookHelper().setLookPosition(targetBlockPos.getX(), targetBlockPos.getY(), targetBlockPos.getZ(), 10.0F, this.getEntity().getVerticalFaceSpeed());

		if (this.getEntity().getDistanceSqToCenter(targetBlockPos) < 2.5D)
		{
			TileEntityChest nearChest = this.getNearOpenChestTileEntity(this.getEntity(), this.getRange());

			if ((nearChest != null) && (nearChest == this.targetChest))
			{
				InventoryChastMain inventoryMain = this.getEntity().getInventoryMain();

				for (int slot = 0; slot < inventoryMain.getSizeInventory(); ++slot)
				{
					ItemStack stackSlot = inventoryMain.getStackInSlot(slot);

					if (!stackSlot.isEmpty())
					{
						inventoryMain.setInventorySlotContents(slot, TileEntityHopper.putStackInInventoryAllSlots((IInventory) null, (IInventory) nearChest, stackSlot, EnumFacing.UP));

						this.getEntity().setCoverOpen(true);
					}
				}

				this.targetChest = null;
			}
		}
		else
		{
			this.getEntity().getNavigator().tryMoveToXYZ(targetBlockPos.getX(), targetBlockPos.getY(), targetBlockPos.getZ(), this.getSpeed());
		}
	}

	// TODO /* ======================================== MOD START =====================================*/

	private boolean canStoringTileEntityChest(@Nullable TileEntityChest tileEntityChest)
	{
		if (tileEntityChest != null)
		{
			return this.canBlockBeSeen(tileEntityChest.getPos());
		}

		return false;
	}

	private TileEntityChest getNearOpenChestTileEntity(EntityChast entityChast, int searchXYZ)
	{
		BlockPos blockPos = entityChast.getPosition();
		int entityPosX = blockPos.getX();
		int entityPosY = blockPos.getY();
		int entityPosZ = blockPos.getZ();
		float rangeOrigin = (float) (searchXYZ * searchXYZ * searchXYZ * 2);
		BlockPos.MutableBlockPos blockPosMutable = new BlockPos.MutableBlockPos();
		TileEntityChest tileEntityChest = null;

		for (int x = (entityPosX - searchXYZ); x <= (entityPosX + searchXYZ); ++x)
		{
			for (int y = (entityPosY - searchXYZ); y <= (entityPosY + searchXYZ); ++y)
			{
				for (int z = (entityPosZ - searchXYZ); z <= (entityPosZ + searchXYZ); ++z)
				{
					blockPosMutable.setPos(x, y, z);
					World world = entityChast.getEntityWorld();
					TileEntity tileEntity = world.getTileEntity(blockPosMutable);

					if (tileEntity instanceof TileEntityChest)
					{
						boolean isLockedChest = (((BlockChest) world.getBlockState(blockPosMutable).getBlock()).getLockableContainer(world, blockPosMutable) == null);

						if (isLockedChest)
						{
							continue;
						}

						float range = (float) ((x - entityPosX) * (x - entityPosX) + (y - entityPosY) * (y - entityPosY) + (z - entityPosZ) * (z - entityPosZ));

						if (range < rangeOrigin)
						{
							rangeOrigin = range;

							for (int slot = 0; slot < entityChast.getInventoryMain().getSizeInventory(); ++slot)
							{
								ItemStack stackInv = entityChast.getInventoryMain().getStackInSlot(slot);

								if (InventoryChastHelper.canStoreInventory((IInventory) tileEntity, stackInv))
								{
									tileEntityChest = (TileEntityChest) tileEntity;

									break;
								}
							}
						}
					}
				}
			}
		}

		return tileEntityChest;
	}

}
