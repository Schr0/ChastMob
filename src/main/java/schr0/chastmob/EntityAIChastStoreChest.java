package schr0.chastmob;

import javax.annotation.Nullable;

import net.minecraft.block.BlockChest;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityAIChastStoreChest extends EntityAIChast
{

	private static final int STORE_TIME_LIMIT = (5 * 20);
	private static final int SEARCH_XYZ = 5;
	private static final double MOVE_SPEED = 1.25D;

	private TileEntityChest targetChest;
	private int storeTime;

	public EntityAIChastStoreChest(EntityChast entityChast)
	{
		super(entityChast);
		this.setMutexBits(1);
	}

	@Override
	public boolean shouldExecute()
	{
		if (ChastMobHelper.canStoreInventory(this.getAIOwnerInventory(), ChastMobHelper.getEmptyItemStack()))
		{
			return false;
		}

		TileEntityChest tileEntityChest = this.getNearChestTileEntity(this.getAIOwnerEntity(), SEARCH_XYZ);

		if (tileEntityChest != null)
		{
			this.setStoring(tileEntityChest, STORE_TIME_LIMIT);

			return true;
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
		this.getAIOwnerEntity().getNavigator().clearPathEntity();
		this.getAIOwnerEntity().setOpen(false);
	}

	@Override
	public void resetTask()
	{
		this.getAIOwnerEntity().getNavigator().clearPathEntity();
		this.getAIOwnerEntity().setOpen(false);

		this.setStoring(null, 0);
	}

	@Override
	public void updateTask()
	{
		--this.storeTime;

		BlockPos targetBlockPos = this.targetChest.getPos();

		this.getAIOwnerEntity().getLookHelper().setLookPosition(targetBlockPos.getX(), targetBlockPos.getY(), targetBlockPos.getZ(), 10.0F, this.getAIOwnerEntity().getVerticalFaceSpeed());

		if (this.getAIOwnerEntity().getDistanceSqToCenter(targetBlockPos) < 2.5D)
		{
			TileEntityChest tileEntityChest = this.getNearChestTileEntity(this.getAIOwnerEntity(), SEARCH_XYZ);

			if ((tileEntityChest != null) && tileEntityChest.equals(this.targetChest))
			{
				for (int slot = 0; slot < this.getAIOwnerInventory().getSizeInventory(); ++slot)
				{
					ItemStack stackInv = this.getAIOwnerInventory().getStackInSlot(slot);

					if (ChastMobHelper.isNotEmptyItemStack(stackInv))
					{
						this.getAIOwnerInventory().setInventorySlotContents(slot, TileEntityHopper.putStackInInventoryAllSlots((IInventory) tileEntityChest, stackInv, EnumFacing.UP));

						this.getAIOwnerEntity().setOpen(true);

						this.setStoring(null, 0);

						return;
					}
				}
			}
		}
		else
		{
			this.getAIOwnerEntity().getNavigator().tryMoveToXYZ(targetBlockPos.getX(), targetBlockPos.getY(), targetBlockPos.getZ(), MOVE_SPEED);
		}
	}

	// TODO /* ======================================== MOD START =====================================*/

	public boolean isStoring()
	{
		return (this.targetChest != null) && (0 < this.storeTime);
	}

	private void setStoring(@Nullable TileEntityChest tileEntityChest, int storeTime)
	{
		this.targetChest = tileEntityChest;
		this.storeTime = storeTime;
	}

	private TileEntityChest getNearChestTileEntity(Entity owner, int searchXYZ)
	{
		World world = owner.worldObj;
		BlockPos blockPosOwner = new BlockPos(owner);
		int pX = blockPosOwner.getX();
		int pY = blockPosOwner.getY();
		int pZ = blockPosOwner.getZ();
		float rangeOrigin = (float) (searchXYZ * searchXYZ * searchXYZ * 2);
		BlockPos.MutableBlockPos blockPosMutable = new BlockPos.MutableBlockPos();
		TileEntityChest tileEntityChest = null;

		for (int x = (pX - searchXYZ); x <= (pX + searchXYZ); ++x)
		{
			for (int y = (pY - searchXYZ); y <= (pY + searchXYZ); ++y)
			{
				for (int z = (pZ - searchXYZ); z <= (pZ + searchXYZ); ++z)
				{
					blockPosMutable.setPos(x, y, z);
					TileEntity tileEntity = world.getTileEntity(blockPosMutable);

					if ((tileEntity instanceof TileEntityChest) && ChastMobHelper.canBlockBeSeen(owner, blockPosMutable))
					{
						boolean isLockedChest = (((BlockChest) world.getBlockState(blockPosMutable).getBlock()).getLockableContainer(world, blockPosMutable) == null);

						if (isLockedChest)
						{
							continue;
						}

						float range = (float) ((x - pX) * (x - pX) + (y - pY) * (y - pY) + (z - pZ) * (z - pZ));

						if (range < rangeOrigin)
						{
							rangeOrigin = range;

							for (int slot = 0; slot < this.getAIOwnerInventory().getSizeInventory(); ++slot)
							{
								ItemStack stackInv = this.getAIOwnerInventory().getStackInSlot(slot);

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
