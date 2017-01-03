package schr0.chastmob;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityAIChastPanic extends EntityAIChast
{

	private static final double MOVE_SPEED = 2.5D;
	private int panicTime;
	private double randPosX;
	private double randPosY;
	private double randPosZ;

	public EntityAIChastPanic(EntityChast entityChast)
	{
		super(entityChast);
		this.setMutexBits(1);
	}

	@Override
	public boolean shouldExecute()
	{
		if (0 < this.panicTime)
		{
			return true;
		}

		return false;
	}

	@Override
	public void startExecuting()
	{
		this.getAIOwnerEntity().getNavigator().clearPathEntity();
		this.getAIOwnerEntity().setPanic(true);
	}

	@Override
	public void resetTask()
	{
		this.getAIOwnerEntity().getNavigator().clearPathEntity();
		this.getAIOwnerEntity().setPanic(false);

		this.getAIOwnerEntity().setOpen(false);
		this.setPanicking(0);
		this.setRandPos(0.0D, 0.0D, 0.0D);
	}

	@Override
	public void updateTask()
	{
		--this.panicTime;

		if (this.getAIOwnerEntity().isBurning())
		{
			BlockPos blockpos = this.getRandBlockPos(this.getAIOwnerWorld(), this.getAIOwnerEntity(), 5, 4);

			if (blockpos == null)
			{
				return;
			}
			else
			{
				this.setRandPos((double) blockpos.getX(), (double) blockpos.getY(), (double) blockpos.getZ());
			}
		}
		else
		{
			Vec3d vec3d = RandomPositionGenerator.findRandomTarget(this.getAIOwnerEntity(), 5, 4);

			if (vec3d == null)
			{
				return;
			}
			else
			{
				this.setRandPos(vec3d.xCoord, vec3d.yCoord, vec3d.zCoord);
			}
		}

		if (this.getAIOwnerEntity().getRNG().nextInt(2) == 0)
		{
			this.getAIOwnerEntity().setOpen(!this.getAIOwnerEntity().isOpen());
		}

		this.getAIOwnerEntity().getNavigator().tryMoveToXYZ(this.randPosX, this.randPosY, this.randPosZ, MOVE_SPEED);
	}

	// TODO /* ======================================== MOD START =====================================*/

	public void setPanicking(int panicTime)
	{
		this.panicTime = panicTime;
	}

	private void setRandPos(double x, double y, double z)
	{
		this.randPosX = x;
		this.randPosY = y;
		this.randPosZ = z;
	}

	private BlockPos getRandBlockPos(World worldIn, Entity entityIn, int horizontalRange, int verticalRange)
	{
		BlockPos blockpos = new BlockPos(entityIn);
		BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
		int pX = blockpos.getX();
		int pY = blockpos.getY();
		int pZ = blockpos.getZ();
		float range = (float) (horizontalRange * horizontalRange * verticalRange * 2);
		BlockPos blockpos1 = null;

		for (int x = pX - horizontalRange; x <= pX + horizontalRange; ++x)
		{
			for (int y = pY - verticalRange; y <= pY + verticalRange; ++y)
			{
				for (int z = pZ - horizontalRange; z <= pZ + horizontalRange; ++z)
				{
					blockpos$mutableblockpos.setPos(x, y, z);
					IBlockState iblockstate = worldIn.getBlockState(blockpos$mutableblockpos);
					Block block = iblockstate.getBlock();

					if (block == Blocks.WATER || block == Blocks.FLOWING_WATER)
					{
						float range1 = (float) ((x - pX) * (x - pX) + (y - pY) * (y - pY) + (z - pZ) * (z - pZ));

						if (range1 < range)
						{
							range = range1;
							blockpos1 = new BlockPos(blockpos$mutableblockpos);
						}
					}
				}
			}
		}

		return blockpos1;
	}

}
