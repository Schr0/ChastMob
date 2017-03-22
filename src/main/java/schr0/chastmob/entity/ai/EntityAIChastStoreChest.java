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
import schr0.chastmob.ChastMobHelper;
import schr0.chastmob.entity.EntityChast;

public class EntityAIChastStoreChest extends EntityAIChast
{

	private static final int TIME_LIMIT = (5 * 20);
	private int timeCounter;

	private double speed;
	private int distance;
	private TileEntityChest targetChest;

	public EntityAIChastStoreChest(EntityChast entityChast, double speed, int distance)
	{
		super(entityChast);

		this.speed = speed;
		this.distance = distance;
	}

	@Override
	public boolean shouldExecute()
	{
		if ((this.getOwnerAIMode() == ChastAIMode.FOLLOW) || (this.getOwnerAIMode() == ChastAIMode.SUPPLY))
		{
			return false;
		}

		if (!ChastMobHelper.canStoreInventory(this.getOwnerInventoryMain(), ChastMobHelper.getEmptyItemStack()))
		{
			TileEntityChest homeChest = (TileEntityChest) this.getOwnerWorld().getTileEntity(this.getOwnerHomePosition());

			if (this.canStoringTileEntityChest(homeChest))
			{
				this.setStoring(TIME_LIMIT, homeChest);

				return true;
			}
			else
			{
				TileEntityChest nearOpenChest = this.getNearOpenChestTileEntity(this.getOwnerEntity(), this.distance);

				if (this.canStoringTileEntityChest(nearOpenChest))
				{
					this.setStoring(TIME_LIMIT, nearOpenChest);

					return true;
				}
			}
		}

		return false;
	}

	@Override
	public boolean continueExecuting()
	{
		if (this.isStoring())
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
		this.setStoring(0, null);
	}

	@Override
	public void updateTask()
	{
		--this.timeCounter;

		BlockPos targetBlockPos = this.targetChest.getPos();

		this.getOwnerEntity().getLookHelper().setLookPosition(targetBlockPos.getX(), targetBlockPos.getY(), targetBlockPos.getZ(), 10.0F, this.getOwnerEntity().getVerticalFaceSpeed());

		if (this.getOwnerEntity().getDistanceSqToCenter(targetBlockPos) < 2.5D)
		{
			TileEntityChest nearChest = this.getNearOpenChestTileEntity(this.getOwnerEntity(), this.distance);

			if ((nearChest != null) && (nearChest == this.targetChest))
			{
				for (int slot = 0; slot < this.getOwnerInventoryMain().getSizeInventory(); ++slot)
				{
					ItemStack stackInv = this.getOwnerInventoryMain().getStackInSlot(slot);

					if (ChastMobHelper.isNotEmptyItemStack(stackInv))
					{
						this.getOwnerInventoryMain().setInventorySlotContents(slot, TileEntityHopper.putStackInInventoryAllSlots((IInventory) null, (IInventory) nearChest, stackInv, EnumFacing.UP));

						this.getOwnerEntity().setCoverOpen(true);
					}
				}

				this.setStoring(0, null);
			}
		}
		else
		{
			this.forceMoveToTargetBlockPos(targetBlockPos, this.speed);
		}
	}

	// TODO /* ======================================== MOD START =====================================*/

	public boolean isStoring()
	{
		return (0 < this.timeCounter) && (this.targetChest != null);
	}

	public void setStoring(int timeCounter, @Nullable TileEntityChest tileEntityChest)
	{
		this.timeCounter = timeCounter;
		this.targetChest = tileEntityChest;
	}

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

							for (int slot = 0; slot < entityChast.getInventoryChastMain().getSizeInventory(); ++slot)
							{
								ItemStack stackInv = entityChast.getInventoryChastMain().getStackInSlot(slot);

								if (ChastMobHelper.canStoreInventory((IInventory) tileEntity, stackInv))
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
