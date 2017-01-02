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

	private double speed;
	private double randPosX;
	private double randPosY;
	private double randPosZ;

	public EntityAIChastPanic(EntityChast entityChast)
	{
		super(entityChast);
		this.setMutexBits(1);

		this.speed = 2.5D;
	}

	@Override
	public boolean shouldExecute()
	{
		if (this.getAIOwnerChast().getAITarget() == null && !this.getAIOwnerChast().isBurning())
		{
			return false;
		}
		else if (!this.getAIOwnerChast().isBurning())
		{
			Vec3d vec3d = RandomPositionGenerator.findRandomTarget(this.getAIOwnerChast(), 5, 4);

			if (vec3d == null)
			{
				return false;
			}
			else
			{
				this.randPosX = vec3d.xCoord;
				this.randPosY = vec3d.yCoord;
				this.randPosZ = vec3d.zCoord;

				return true;
			}
		}
		else
		{
			BlockPos blockpos = this.getRandPos(this.getAIOwnerChast().worldObj, this.getAIOwnerChast(), 5, 4);

			if (blockpos == null)
			{
				return false;
			}
			else
			{
				this.randPosX = (double) blockpos.getX();
				this.randPosY = (double) blockpos.getY();
				this.randPosZ = (double) blockpos.getZ();

				return true;
			}
		}
	}

	@Override
	public boolean continueExecuting()
	{
		return !this.getAIOwnerChast().getNavigator().noPath();
	}

	@Override
	public void startExecuting()
	{
		this.getAIOwnerChast().getNavigator().clearPathEntity();
		this.getAIOwnerChast().setPanicking(true);
		this.getAIOwnerChast().setAISitFlag(false);
		this.getAIOwnerChast().setAITradeFlag(null);

		this.getAIOwnerChast().getNavigator().tryMoveToXYZ(this.randPosX, this.randPosY, this.randPosZ, this.speed);
	}

	@Override
	public void resetTask()
	{
		this.getAIOwnerChast().getNavigator().clearPathEntity();
		this.getAIOwnerChast().setPanicking(false);
		this.getAIOwnerChast().setOpen(false);
	}

	@Override
	public void updateTask()
	{
		if (this.getAIOwnerChast().getRNG().nextInt(2) == 0)
		{
			this.getAIOwnerChast().setOpen(!this.getAIOwnerChast().isOpen());
		}
	}

	// TODO /* ======================================== MOD START =====================================*/

	private BlockPos getRandPos(World worldIn, Entity entityIn, int horizontalRange, int verticalRange)
	{
		BlockPos blockpos = new BlockPos(entityIn);
		BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
		int i = blockpos.getX();
		int j = blockpos.getY();
		int k = blockpos.getZ();
		float f = (float) (horizontalRange * horizontalRange * verticalRange * 2);
		BlockPos blockpos1 = null;

		for (int l = i - horizontalRange; l <= i + horizontalRange; ++l)
		{
			for (int i1 = j - verticalRange; i1 <= j + verticalRange; ++i1)
			{
				for (int j1 = k - horizontalRange; j1 <= k + horizontalRange; ++j1)
				{
					blockpos$mutableblockpos.setPos(l, i1, j1);
					IBlockState iblockstate = worldIn.getBlockState(blockpos$mutableblockpos);
					Block block = iblockstate.getBlock();

					if (block == Blocks.WATER || block == Blocks.FLOWING_WATER)
					{
						float f1 = (float) ((l - i) * (l - i) + (i1 - j) * (i1 - j) + (j1 - k) * (j1 - k));

						if (f1 < f)
						{
							f = f1;
							blockpos1 = new BlockPos(blockpos$mutableblockpos);
						}
					}
				}
			}
		}

		return blockpos1;
	}

}
