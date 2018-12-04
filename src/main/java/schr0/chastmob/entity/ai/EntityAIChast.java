package schr0.chastmob.entity.ai;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import schr0.chastmob.entity.ChastMode;
import schr0.chastmob.entity.EntityChast;

public abstract class EntityAIChast extends EntityAIBase
{

	private EntityChast entityChast;
	private int timeCount;

	public EntityAIChast(EntityChast entityChast)
	{
		this.entityChast = entityChast;
		this.timeCount = 0;
	}

	@Override
	public void startExecuting()
	{
		this.entityChast.getNavigator().clearPath();
		this.entityChast.setCoverOpen(false);
		this.timeCount = this.getTimeLimit();
	}

	@Override
	public void resetTask()
	{
		this.entityChast.getNavigator().clearPath();
		this.entityChast.setCoverOpen(false);
		this.timeCount = 0;
	}

	@Override
	public void updateTask()
	{
		--this.timeCount;
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

	public BlockPos getHomePosition()
	{
		BlockPos homePosition = this.entityChast.getPosition();

		if (this.entityChast.getMode() == ChastMode.FOLLOW)
		{
			EntityLivingBase ownerEntity = this.entityChast.getOwner();

			if (this.entityChast.isTamed() && (ownerEntity != null))
			{
				homePosition = ownerEntity.getPosition();
			}
		}

		return homePosition;
	}

	public double getSpeed()
	{
		return 1.25D;
	}

	public int getRange()
	{
		return 5;
	}

	public int getTimeLimit()
	{
		return (5 * 20);
	}

	public boolean isExecutingTime()
	{
		return (0 < this.timeCount);
	}

	public boolean canBlockBeSeen(BlockPos blockPos)
	{
		World world = this.entityChast.getEntityWorld();
		IBlockState state = world.getBlockState(blockPos);

		if (state == Blocks.AIR.getDefaultState())
		{
			return false;
		}

		Vec3d entityVec3d = new Vec3d(this.entityChast.posX, this.entityChast.posY + this.entityChast.getEyeHeight(), this.entityChast.posZ);
		Vec3d targetVec3d = new Vec3d(((double) blockPos.getX() + 0.5D), ((double) blockPos.getY() + (state.getCollisionBoundingBox(world, blockPos).minY + state.getCollisionBoundingBox(world, blockPos).maxY) * 0.9D), ((double) blockPos.getZ() + 0.5D));
		RayTraceResult rayTraceResult = world.rayTraceBlocks(entityVec3d, targetVec3d);

		if ((rayTraceResult != null) && (rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK))
		{
			if (rayTraceResult.getBlockPos().equals(blockPos))
			{
				return true;
			}
		}

		return false;
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
