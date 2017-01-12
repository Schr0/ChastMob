package schr0.chastmob;

import javax.annotation.Nullable;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntityAIChastGoHome extends EntityAIChast
{

	private static final int TIME_LIMIT = (5 * 20);

	private double moveSpeed;
	private int maxDistance;
	private int timeCounter;
	private TileEntityChest targetChest;

	public EntityAIChastGoHome(EntityChast entityChast, double speed, int distance)
	{
		super(entityChast);
		this.setMutexBits(1);

		this.moveSpeed = speed;
		this.maxDistance = distance;
	}

	@Override
	public boolean shouldExecute()
	{
		if (this.getAIOwnerEntity().isOwnerFollow())
		{
			return false;
		}
		else
		{
			TileEntityChest homeChest = (TileEntityChest) this.getAIOwnerWorld().getTileEntity(this.getAIOwnerPosition(true));

			if (this.canGoingTileEntityChest(homeChest))
			{
				this.setGoing(TIME_LIMIT, homeChest);

				return true;
			}
		}

		return false;
	}

	@Override
	public boolean continueExecuting()
	{
		if (this.isGoing())
		{
			return true;
		}

		return false;
	}

	@Override
	public void startExecuting()
	{
		super.startExecuting();

		this.getAIOwnerEntity().setCoverOpen(false);
	}

	@Override
	public void resetTask()
	{
		super.resetTask();

		this.getAIOwnerEntity().setCoverOpen(false);

		this.setGoing(0, null);
	}

	@Override
	public void updateTask()
	{
		--this.timeCounter;

		BlockPos targetBlockPos = this.targetChest.getPos();

		this.getAIOwnerEntity().getLookHelper().setLookPosition(targetBlockPos.getX(), targetBlockPos.getY(), targetBlockPos.getZ(), 10.0F, this.getAIOwnerEntity().getVerticalFaceSpeed());

		if (this.getAIOwnerEntity().getDistanceSqToCenter(targetBlockPos) < 2.5D)
		{
			this.setGoing(0, null);
		}
		else
		{
			if (!this.getAIOwnerEntity().getNavigator().tryMoveToXYZ(targetBlockPos.getX(), targetBlockPos.getY(), targetBlockPos.getZ(), this.moveSpeed))
			{
				int ownerPosX = MathHelper.floor_double(targetBlockPos.getX()) - 2;
				int ownerPosY = MathHelper.floor_double(targetBlockPos.getY());
				int ownerPosZ = MathHelper.floor_double(targetBlockPos.getZ()) - 2;

				for (int x = 0; x <= 4; ++x)
				{
					for (int z = 0; z <= 4; ++z)
					{
						if ((x < 1 || z < 1 || x > 3 || z > 3) && this.getAIOwnerWorld().getBlockState(new BlockPos(ownerPosX + x, ownerPosY - 1, ownerPosZ + z)).isFullyOpaque() && this.isEmptyBlock(new BlockPos(ownerPosX + x, ownerPosY, ownerPosZ + z)) && this.isEmptyBlock(new BlockPos(ownerPosX + x, ownerPosY + 1, ownerPosZ + z)))
						{
							this.getAIOwnerEntity().setLocationAndAngles((double) ((float) (ownerPosX + x) + 0.5F), (double) ownerPosY, (double) ((float) (ownerPosZ + z) + 0.5F), this.getAIOwnerEntity().rotationYaw, this.getAIOwnerEntity().rotationPitch);

							this.setGoing(0, null);

							return;
						}
					}
				}
			}
		}
	}

	// TODO /* ======================================== MOD START =====================================*/

	public boolean isGoing()
	{
		return (0 < this.timeCounter) && (this.targetChest != null);
	}

	public void setGoing(int timeCounter, @Nullable TileEntityChest tileEntityChest)
	{
		this.timeCounter = timeCounter;
		this.targetChest = tileEntityChest;
	}

	private boolean canGoingTileEntityChest(TileEntityChest tileEntityChest)
	{
		if (tileEntityChest != null)
		{
			TileEntityChest nearChest = this.getNearChestTileEntity(this.getAIOwnerEntity(), this.maxDistance);

			if (nearChest != null && nearChest.equals(tileEntityChest))
			{
				return false;
			}

			return ChastMobHelper.canBlockBeSeen(this.getAIOwnerEntity(), tileEntityChest.getPos());
		}

		return false;
	}

	private TileEntityChest getNearChestTileEntity(EntityChast entityChast, int searchXYZ)
	{
		BlockPos blockPos = entityChast.getPosition();
		int pX = blockPos.getX();
		int pY = blockPos.getY();
		int pZ = blockPos.getZ();
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
					World world = entityChast.worldObj;
					TileEntity tileEntity = world.getTileEntity(blockPosMutable);

					if (tileEntity instanceof TileEntityChest)
					{
						float range = (float) ((x - pX) * (x - pX) + (y - pY) * (y - pY) + (z - pZ) * (z - pZ));

						if (range < rangeOrigin)
						{
							rangeOrigin = range;

							tileEntityChest = (TileEntityChest) tileEntity;
						}
					}
				}
			}
		}

		return tileEntityChest;
	}

}
