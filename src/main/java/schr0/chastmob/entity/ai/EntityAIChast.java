package schr0.chastmob.entity.ai;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import schr0.chastmob.entity.ChastMode;
import schr0.chastmob.entity.EntityChast;

public abstract class EntityAIChast extends EntityAIBase
{

	private EntityChast entityChast;
	private int time;

	public EntityAIChast(EntityChast entityChast)
	{
		this.entityChast = entityChast;
		this.time = 0;
	}

	@Override
	public void startExecuting()
	{
		this.entityChast.getNavigator().clearPath();
		this.entityChast.setCoverOpen(false);
		this.time = this.getTimeLimit();
	}

	@Override
	public void resetTask()
	{
		this.entityChast.getNavigator().clearPath();
		this.entityChast.setCoverOpen(false);
		this.time = 0;
	}

	@Override
	public void updateTask()
	{
		--this.time;
	}

	// TODO /* ======================================== MOD START =====================================*/

	public World getWorld()
	{
		return this.entityChast.getEntityWorld();
	}

	public EntityChast getEntity()
	{
		return this.entityChast;
	}

	public ChastMode getMode()
	{
		return this.entityChast.getMode();
	}

	public double getSpeed()
	{
		return this.entityChast.getAISpeed();
	}

	public int getRange()
	{
		return this.entityChast.getAIRange();
	}

	public int getBlockRange()
	{
		int range = this.entityChast.getAIRange();

		return (range * range) * 2;
	}

	public int getTimeLimit()
	{
		return (5 * 20);
	}

	public boolean isTimeOut()
	{
		return (this.time < 0);
	}

	public void forceMoveToTargetBlockPos(BlockPos blockPos)
	{
		int targetPosX = MathHelper.floor(blockPos.getX()) - 2;
		int targetPosY = MathHelper.floor(blockPos.getY());
		int targetPosZ = MathHelper.floor(blockPos.getZ()) - 2;

		this.teleportTargetPosition(targetPosX, targetPosY, targetPosZ);
	}

	public void forceMoveToTargetEntity(Entity entitiy)
	{
		int targetPosX = MathHelper.floor(entitiy.posX) - 2;
		int targetPosY = MathHelper.floor(entitiy.getEntityBoundingBox().minY);
		int targetPosZ = MathHelper.floor(entitiy.posZ) - 2;

		this.teleportTargetPosition(targetPosX, targetPosY, targetPosZ);
	}

	private boolean teleportTargetPosition(int targetPosX, int targetPosY, int targetPosZ)
	{
		World world = this.entityChast.getEntityWorld();
		int xP = MathHelper.floor(this.entityChast.getOwner().posX) - 2;
		int yP = MathHelper.floor(this.entityChast.getOwner().posZ) - 2;
		int zP = MathHelper.floor(this.entityChast.getOwner().getEntityBoundingBox().minY);

		for (int x = 0; x <= 4; ++x)
		{
			for (int z = 0; z <= 4; ++z)
			{
				if ((x < 1 || z < 1 || x > 3 || z > 3) && this.isTeleportFriendlyBlock(xP, yP, zP, x, z))
				{
					this.entityChast.setLocationAndAngles((double) ((float) (targetPosX + x) + 0.5F), (double) targetPosY, (double) ((float) (targetPosZ + z) + 0.5F), this.entityChast.rotationYaw, this.entityChast.rotationPitch);

					return true;
				}
			}
		}

		return false;
	}

	private boolean isTeleportFriendlyBlock(int xP, int yP, int zP, int x, int z)
	{
		World world = this.entityChast.getEntityWorld();
		BlockPos blockpos = new BlockPos(xP + x, zP - 1, yP + z);
		IBlockState iblockstate = world.getBlockState(blockpos);
		return iblockstate.getBlockFaceShape(world, blockpos, EnumFacing.DOWN) == BlockFaceShape.SOLID && iblockstate.canEntitySpawn(this.entityChast) && world.isAirBlock(blockpos.up()) && world.isAirBlock(blockpos.up(2));
	}
}
