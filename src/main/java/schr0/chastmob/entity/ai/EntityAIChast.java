package schr0.chastmob.entity.ai;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import schr0.chastmob.entity.EntityChast;
import schr0.chastmob.entity.EnumAIMode;
import schr0.chastmob.entity.inventory.InventoryChastMain;
import schr0.chastmob.init.ChastMobItems;
import schr0.chastmob.item.ItemSpecificationPatrol;

public abstract class EntityAIChast extends EntityAIBase
{

	private EntityChast entityChast;

	public EntityAIChast(EntityChast entityChast)
	{
		this.setMutexBits(1);

		this.entityChast = entityChast;
	}

	@Override
	public void startExecuting()
	{
		this.entityChast.getNavigator().clearPathEntity();
	}

	@Override
	public void resetTask()
	{
		this.entityChast.getNavigator().clearPathEntity();
	}

	// TODO /* ======================================== MOD START =====================================*/

	public EntityChast getOwnerEntity()
	{
		return this.entityChast;
	}

	public World getOwnerWorld()
	{
		return this.entityChast.getEntityWorld();
	}

	public EnumAIMode getOwnerAIMode()
	{
		return this.entityChast.getAIMode();
	}

	public InventoryChastMain getOwnerInventoryMain()
	{
		return this.entityChast.getInventoryChastMain();
	}

	public BlockPos getOwnerHomePosition()
	{
		BlockPos homePosition = this.entityChast.getPosition();

		switch (this.entityChast.getAIMode())
		{
			case PATROL :

				ItemStack stackSpecificationItem = this.entityChast.getInventoryChastEquipment().getSpecificationItem();

				if (stackSpecificationItem.getItem() == ChastMobItems.SPECIFICATION_PATROL)
				{
					BlockPos homePositionChest = ((ItemSpecificationPatrol) stackSpecificationItem.getItem()).getHomeChestPosition(stackSpecificationItem);

					if (homePositionChest != null)
					{
						homePosition = homePositionChest;
					}
				}
				break;

			case FOLLOW :

				EntityLivingBase ownerEntity = this.entityChast.getOwnerEntity();

				if (this.entityChast.isOwnerTame() && (ownerEntity != null))
				{
					homePosition = ownerEntity.getPosition();
				}
				break;

			default :
				break;
		}

		return homePosition;
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

	public void forceMoveToTargetBlockPos(BlockPos blockPos, double moveSpeed)
	{
		if (!this.entityChast.getNavigator().tryMoveToXYZ(blockPos.getX(), blockPos.getY(), blockPos.getZ(), moveSpeed))
		{
			int targetPosX = MathHelper.floor(blockPos.getX()) - 2;
			int targetPosY = MathHelper.floor(blockPos.getY());
			int targetPosZ = MathHelper.floor(blockPos.getZ()) - 2;

			if (!this.teleportTargetPosition(targetPosX, targetPosY, targetPosZ))
			{
				// none
			}
		}
	}

	public void forceMoveToTargetEntity(Entity entitiy, double moveSpeed)
	{
		if (!this.entityChast.getNavigator().tryMoveToEntityLiving(entitiy, moveSpeed))
		{
			int targetPosX = MathHelper.floor(entitiy.posX) - 2;
			int targetPosY = MathHelper.floor(entitiy.getEntityBoundingBox().minY);
			int targetPosZ = MathHelper.floor(entitiy.posZ) - 2;

			if (!this.teleportTargetPosition(targetPosX, targetPosY, targetPosZ))
			{
				// none
			}
		}
	}

	private boolean teleportTargetPosition(int targetPosX, int targetPosY, int targetPosZ)
	{
		World world = this.entityChast.getEntityWorld();

		for (int x = 0; x <= 4; ++x)
		{
			for (int z = 0; z <= 4; ++z)
			{
				if ((x < 1 || z < 1 || x > 3 || z > 3) && world.getBlockState(new BlockPos(targetPosX + x, targetPosY - 1, targetPosZ + z)).isFullyOpaque() && this.isEmptyBlock(new BlockPos(targetPosX + x, targetPosY, targetPosZ + z)) && this.isEmptyBlock(new BlockPos(targetPosX + x, targetPosY + 1, targetPosZ + z)))
				{
					this.entityChast.setLocationAndAngles((double) ((float) (targetPosX + x) + 0.5F), (double) targetPosY, (double) ((float) (targetPosZ + z) + 0.5F), this.entityChast.rotationYaw, this.entityChast.rotationPitch);

					return true;
				}
			}
		}

		return false;
	}

	private boolean isEmptyBlock(BlockPos pos)
	{
		IBlockState state = this.entityChast.getEntityWorld().getBlockState(pos);

		return (state.getMaterial() == Material.AIR) ? true : !state.isFullCube();
	}
}
