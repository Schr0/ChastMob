package schr0.chastmob.entity.ai;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import schr0.chastmob.ChastMobHelper;
import schr0.chastmob.entity.EntityChast;
import schr0.chastmob.entity.InventoryChast;
import schr0.chastmob.item.ItemHomeChestMap;

public abstract class EntityAIChast extends EntityAIBase
{

	private EntityChast theChast;

	public EntityAIChast(EntityChast entityChast)
	{
		this.theChast = entityChast;
	}

	@Override
	public void startExecuting()
	{
		this.theChast.getNavigator().clearPathEntity();
	}

	@Override
	public void resetTask()
	{
		this.theChast.getNavigator().clearPathEntity();
	}

	// TODO /* ======================================== MOD START =====================================*/

	public EntityChast getAIOwnerEntity()
	{
		return this.theChast;
	}

	public World getAIOwnerWorld()
	{
		return this.theChast.getEntityWorld();
	}

	public InventoryChast getAIOwnerInventory()
	{
		return this.theChast.getInventoryChast();
	}

	public BlockPos getAIPosition()
	{
		BlockPos blockPos = this.theChast.getPosition();

		if (this.theChast.getAIMode() == EnumAIMode.FOLLOW)
		{
			EntityLivingBase entityLivingBase = this.theChast.getOwnerEntity();

			if (this.theChast.isOwnerTame() && (entityLivingBase != null))
			{
				blockPos = entityLivingBase.getPosition();
			}
		}
		else
		{
			ItemStack stackMainhand = this.theChast.getHeldItemMainhand();

			if (ChastMobHelper.isNotEmptyItemStack(stackMainhand) && (stackMainhand.getItem() instanceof ItemHomeChestMap))
			{
				BlockPos blockPosHome = ((ItemHomeChestMap) stackMainhand.getItem()).getHomeChestBlockPos(stackMainhand);

				if (blockPosHome != null)
				{
					blockPos = blockPosHome;
				}
			}
		}

		return blockPos;
	}

	public void tryMoveToTargetBlockPos(BlockPos blockPos, double moveSpeed)
	{
		if (!this.theChast.getNavigator().tryMoveToXYZ(blockPos.getX(), blockPos.getY(), blockPos.getZ(), moveSpeed))
		{
			int targetPosX = MathHelper.floor_double(blockPos.getX()) - 2;
			int targetPosY = MathHelper.floor_double(blockPos.getY());
			int targetPosZ = MathHelper.floor_double(blockPos.getZ()) - 2;

			this.teleportTargetPosition(targetPosX, targetPosY, targetPosZ);
		}
	}

	public void tryMoveToTargetEntity(Entity entitiy, double moveSpeed)
	{
		if (!this.theChast.getNavigator().tryMoveToEntityLiving(entitiy, moveSpeed))
		{
			int targetPosX = MathHelper.floor_double(entitiy.posX) - 2;
			int targetPosY = MathHelper.floor_double(entitiy.getEntityBoundingBox().minY);
			int targetPosZ = MathHelper.floor_double(entitiy.posZ) - 2;

			this.teleportTargetPosition(targetPosX, targetPosY, targetPosZ);
		}
	}

	private void teleportTargetPosition(int targetPosX, int targetPosY, int targetPosZ)
	{
		for (int x = 0; x <= 4; ++x)
		{
			for (int z = 0; z <= 4; ++z)
			{
				if ((x < 1 || z < 1 || x > 3 || z > 3) && this.getAIOwnerWorld().getBlockState(new BlockPos(targetPosX + x, targetPosY - 1, targetPosZ + z)).isFullyOpaque() && this.isEmptyBlock(new BlockPos(targetPosX + x, targetPosY, targetPosZ + z)) && this.isEmptyBlock(new BlockPos(targetPosX + x, targetPosY + 1, targetPosZ + z)))
				{
					this.getAIOwnerEntity().setLocationAndAngles((double) ((float) (targetPosX + x) + 0.5F), (double) targetPosY, (double) ((float) (targetPosZ + z) + 0.5F), this.getAIOwnerEntity().rotationYaw, this.getAIOwnerEntity().rotationPitch);

					return;
				}
			}
		}
	}

	private boolean isEmptyBlock(BlockPos pos)
	{
		IBlockState state = this.getAIOwnerWorld().getBlockState(pos);

		return state.getMaterial().equals(Material.AIR) ? true : !state.isFullCube();
	}
}
