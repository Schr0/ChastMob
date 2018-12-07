package schr0.chastmob.entity.ai;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import schr0.chastmob.entity.ChastMode;
import schr0.chastmob.entity.EntityChast;

public class EntityAIChastGoHome extends EntityAIChast
{

	private TileEntityChest targetChest;

	public EntityAIChastGoHome(EntityChast entityChast)
	{
		super(entityChast);
		this.targetChest = null;
	}

	@Override
	public boolean shouldExecute()
	{
		if (this.getMode() == ChastMode.PATROL)
		{
			this.targetChest = null;

			TileEntityChest homeChest = (TileEntityChest) this.getWorld().getTileEntity(this.getHomePosition());

			if (this.canGoHomeTileEntityChest(homeChest))
			{
				this.targetChest = homeChest;

				return true;
			}
		}

		return false;
	}

	@Override
	public boolean shouldContinueExecuting()
	{
		if (this.isTimeOut())
		{
			return false;
		}

		return (this.targetChest != null);
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

		if (this.getEntity().getDistanceSqToCenter(targetBlockPos) < this.getRange())
		{
			this.targetChest = null;
		}
		else
		{
			this.getEntity().getNavigator().tryMoveToXYZ(targetBlockPos.getX(), targetBlockPos.getY(), targetBlockPos.getZ(), this.getSpeed());
		}
	}

	// TODO /* ======================================== MOD START =====================================*/

	private boolean canGoHomeTileEntityChest(TileEntityChest tileEntityChest)
	{
		if (tileEntityChest != null)
		{
			TileEntityChest nearChest = this.getNearChestTileEntity(this.getEntity(), this.getRange());

			if ((nearChest != null) && (nearChest == tileEntityChest))
			{
				return false;
			}

			return this.canBlockBeSeen(tileEntityChest.getPos());
		}

		return false;
	}

	private TileEntityChest getNearChestTileEntity(EntityChast entityChast, int searchXYZ)
	{
		BlockPos blockPos = entityChast.getPosition();
		int blockPosX = blockPos.getX();
		int blockPosY = blockPos.getY();
		int blockPosZ = blockPos.getZ();
		float rangeOrigin = (float) (searchXYZ * searchXYZ * searchXYZ * 2);
		BlockPos.MutableBlockPos blockPosMutable = new BlockPos.MutableBlockPos();
		TileEntityChest tileEntityChest = null;

		for (int x = (blockPosX - searchXYZ); x <= (blockPosX + searchXYZ); ++x)
		{
			for (int y = (blockPosY - searchXYZ); y <= (blockPosY + searchXYZ); ++y)
			{
				for (int z = (blockPosZ - searchXYZ); z <= (blockPosZ + searchXYZ); ++z)
				{
					blockPosMutable.setPos(x, y, z);
					World world = entityChast.getEntityWorld();
					TileEntity tileEntity = world.getTileEntity(blockPosMutable);

					if (tileEntity instanceof TileEntityChest)
					{
						float range = (float) ((x - blockPosX) * (x - blockPosX) + (y - blockPosY) * (y - blockPosY) + (z - blockPosZ) * (z - blockPosZ));

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
