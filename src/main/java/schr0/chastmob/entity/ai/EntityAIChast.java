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
import schr0.chastmob.entity.EnumAIMode;
import schr0.chastmob.entity.inventory.InventoryChastEquipment;
import schr0.chastmob.entity.inventory.InventoryChastMain;
import schr0.chastmob.init.ChastMobItems;
import schr0.chastmob.item.ItemSpecificationPatrol;

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

	public World getOwnerWorld()
	{
		return this.theChast.getEntityWorld();
	}

	public EntityChast getOwnerEntity()
	{
		return this.theChast;
	}

	public EnumAIMode getOwnerAIMode()
	{
		return this.theChast.getAIMode();
	}

	public InventoryChastMain getOwnerInventoryMain()
	{
		return this.theChast.getInventoryChastMain();
	}

	public InventoryChastEquipment getOwnerInventoryChastEquipment()
	{
		return this.theChast.getInventoryChastEquipment();
	}

	public BlockPos getOwnerHomePosition()
	{
		BlockPos homePosition = this.theChast.getPosition();

		switch (this.theChast.getAIMode())
		{
			case PATROL :

				ItemStack specificationItem = this.theChast.getInventoryChastEquipment().getSpecificationItem();

				if (ChastMobHelper.isNotEmptyItemStack(specificationItem) && (specificationItem.getItem().equals(ChastMobItems.SPECIFICATION_PATROL)))
				{
					BlockPos homePositionChest = ((ItemSpecificationPatrol) specificationItem.getItem()).getHomeChestPosition(specificationItem);

					if (homePositionChest != null)
					{
						homePosition = homePositionChest;
					}
				}
				break;

			case FOLLOW :

				EntityLivingBase ownerEntity = this.theChast.getOwnerEntity();

				if (this.theChast.isOwnerTame() && (ownerEntity != null))
				{
					homePosition = ownerEntity.getPosition();
				}
				break;

			default :
				break;
		}

		return homePosition;
	}

	public void forceMoveToTargetBlockPos(BlockPos blockPos, double moveSpeed)
	{
		if (!this.theChast.getNavigator().tryMoveToXYZ(blockPos.getX(), blockPos.getY(), blockPos.getZ(), moveSpeed))
		{
			int targetPosX = MathHelper.floor_double(blockPos.getX()) - 2;
			int targetPosY = MathHelper.floor_double(blockPos.getY());
			int targetPosZ = MathHelper.floor_double(blockPos.getZ()) - 2;

			if (!this.teleportTargetPosition(targetPosX, targetPosY, targetPosZ))
			{
				// none
			}
		}
	}

	public void forceMoveToTargetEntity(Entity entitiy, double moveSpeed)
	{
		if (!this.theChast.getNavigator().tryMoveToEntityLiving(entitiy, moveSpeed))
		{
			int targetPosX = MathHelper.floor_double(entitiy.posX) - 2;
			int targetPosY = MathHelper.floor_double(entitiy.getEntityBoundingBox().minY);
			int targetPosZ = MathHelper.floor_double(entitiy.posZ) - 2;

			if (!this.teleportTargetPosition(targetPosX, targetPosY, targetPosZ))
			{
				// none
			}
		}
	}

	private boolean teleportTargetPosition(int targetPosX, int targetPosY, int targetPosZ)
	{
		World world = this.theChast.getEntityWorld();

		for (int x = 0; x <= 4; ++x)
		{
			for (int z = 0; z <= 4; ++z)
			{
				if ((x < 1 || z < 1 || x > 3 || z > 3) && world.getBlockState(new BlockPos(targetPosX + x, targetPosY - 1, targetPosZ + z)).isFullyOpaque() && this.isEmptyBlock(new BlockPos(targetPosX + x, targetPosY, targetPosZ + z)) && this.isEmptyBlock(new BlockPos(targetPosX + x, targetPosY + 1, targetPosZ + z)))
				{
					this.theChast.setLocationAndAngles((double) ((float) (targetPosX + x) + 0.5F), (double) targetPosY, (double) ((float) (targetPosZ + z) + 0.5F), this.theChast.rotationYaw, this.theChast.rotationPitch);

					return true;
				}
			}
		}

		return false;
	}

	private boolean isEmptyBlock(BlockPos pos)
	{
		IBlockState state = this.theChast.getEntityWorld().getBlockState(pos);

		return state.getMaterial().equals(Material.AIR) ? true : !state.isFullCube();
	}
}
