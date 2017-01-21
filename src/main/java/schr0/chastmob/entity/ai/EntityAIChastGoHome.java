package schr0.chastmob.entity.ai;

import javax.annotation.Nullable;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import schr0.chastmob.ChastMobHelper;
import schr0.chastmob.entity.EntityChast;

public class EntityAIChastGoHome extends EntityAIChast
{

	private static final int TIME_LIMIT = (5 * 20);

	private double moveSpeed;
	private int minDistance;
	private int timeCounter;
	private TileEntityChest targetChest;

	public EntityAIChastGoHome(EntityChast entityChast, double speed, int distance)
	{
		super(entityChast);
		this.setMutexBits(1);

		this.moveSpeed = speed;
		this.minDistance = distance;
	}

	@Override
	public boolean shouldExecute()
	{
		if (this.getAIOwnerEntity().getAIMode() == EnumAIMode.FOLLOW)
		{
			return false;
		}
		else
		{
			TileEntityChest homeChest = (TileEntityChest) this.getAIOwnerWorld().getTileEntity(this.getAIPosition());

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

		if (this.getAIOwnerEntity().getDistanceSqToCenter(targetBlockPos) < (this.minDistance * this.minDistance))
		{
			this.setGoing(0, null);
		}
		else
		{
			this.tryMoveToTargetBlockPos(targetBlockPos, this.moveSpeed);
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
			TileEntityChest nearChest = this.getNearChestTileEntity(this.getAIOwnerEntity(), this.minDistance);

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
