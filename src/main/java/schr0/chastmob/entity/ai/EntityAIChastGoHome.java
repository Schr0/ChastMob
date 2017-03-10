package schr0.chastmob.entity.ai;

import javax.annotation.Nullable;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import schr0.chastmob.ChastMobHelper;
import schr0.chastmob.entity.EntityChast;
import schr0.chastmob.entity.EnumAIMode;

public class EntityAIChastGoHome extends EntityAIChast
{

	private static final int TIME_LIMIT = (5 * 20);
	private int timeCounter;

	private double speed;
	private int distance;
	private TileEntityChest targetChest;

	public EntityAIChastGoHome(EntityChast entityChast, double speed, int distance)
	{
		super(entityChast);
		this.setMutexBits(1);

		this.speed = speed;
		this.distance = distance;
	}

	@Override
	public boolean shouldExecute()
	{
		if (this.getOwnerAIMode() != EnumAIMode.PATROL)
		{
			return false;
		}

		TileEntityChest homeChest = (TileEntityChest) this.getOwnerWorld().getTileEntity(this.getOwnerHomePosition());

		if (this.canGoingTileEntityChest(homeChest))
		{
			this.setGoing(TIME_LIMIT, homeChest);

			return true;
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

		this.getOwnerEntity().setCoverOpen(false);
	}

	@Override
	public void resetTask()
	{
		super.resetTask();

		this.getOwnerEntity().setCoverOpen(false);
		this.setGoing(0, null);
	}

	@Override
	public void updateTask()
	{
		--this.timeCounter;

		BlockPos targetBlockPos = this.targetChest.getPos();

		this.getOwnerEntity().getLookHelper().setLookPosition(targetBlockPos.getX(), targetBlockPos.getY(), targetBlockPos.getZ(), 10.0F, this.getOwnerEntity().getVerticalFaceSpeed());

		if (this.getOwnerEntity().getDistanceSqToCenter(targetBlockPos) < (this.distance * this.distance))
		{
			this.setGoing(0, null);
		}
		else
		{
			if (this.timeCounter < 20)
			{
				this.forceMoveToTargetBlockPos(targetBlockPos, this.speed);
			}
			else
			{
				this.getOwnerEntity().getNavigator().tryMoveToXYZ(targetBlockPos.getX(), targetBlockPos.getY(), targetBlockPos.getZ(), this.speed);
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
			TileEntityChest nearChest = this.getNearChestTileEntity(this.getOwnerEntity(), this.distance);

			if (nearChest != null && nearChest.equals(tileEntityChest))
			{
				return false;
			}

			return ChastMobHelper.canBlockBeSeen(this.getOwnerEntity(), tileEntityChest.getPos());
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
