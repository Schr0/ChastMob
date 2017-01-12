package schr0.chastmob;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class EntityAIChastFollowOwner extends EntityAIChast
{

	private static final int TIME_LIMIT = (5 * 20);

	private double moveSpeed;
	private double minDistance;
	private int timeCounter;
	private EntityLivingBase targetOwner;

	public EntityAIChastFollowOwner(EntityChast entityChast, double speed, double distance)
	{
		super(entityChast);
		this.setMutexBits(1);

		this.moveSpeed = speed;
		this.minDistance = (distance * distance);
	}

	@Override
	public boolean shouldExecute()
	{
		if (this.getAIOwnerEntity().isOwnerFollow())
		{
			EntityLivingBase owner = this.getAIOwnerEntity().getOwnerEntity();

			if (this.canFollowEntityLivingBase(owner))
			{
				if (this.getAIOwnerEntity().getDistanceSqToEntity(owner) < this.minDistance)
				{
					return false;
				}
				else
				{
					this.setFollowing(TIME_LIMIT, owner);

					return true;
				}
			}
		}

		return false;
	}

	@Override
	public boolean continueExecuting()
	{
		if (this.isFollowing())
		{
			return true;
		}

		return false;
	}

	@Override
	public void resetTask()
	{
		super.resetTask();

		this.setFollowing(0, null);
	}

	@Override
	public void updateTask()
	{
		--this.timeCounter;

		this.getAIOwnerEntity().getLookHelper().setLookPositionWithEntity(this.targetOwner, 10.0F, this.getAIOwnerEntity().getVerticalFaceSpeed());

		if (this.getAIOwnerEntity().getDistanceSqToEntity(this.targetOwner) < this.minDistance)
		{
			this.setFollowing(0, null);
		}
		else
		{
			if (!this.getAIOwnerEntity().getNavigator().tryMoveToEntityLiving(this.targetOwner, this.moveSpeed))
			{
				int ownerPosX = MathHelper.floor_double(this.targetOwner.posX) - 2;
				int ownerPosY = MathHelper.floor_double(this.targetOwner.getEntityBoundingBox().minY);
				int ownerPosZ = MathHelper.floor_double(this.targetOwner.posZ) - 2;

				for (int x = 0; x <= 4; ++x)
				{
					for (int z = 0; z <= 4; ++z)
					{
						if ((x < 1 || z < 1 || x > 3 || z > 3) && this.getAIOwnerWorld().getBlockState(new BlockPos(ownerPosX + x, ownerPosY - 1, ownerPosZ + z)).isFullyOpaque() && this.isEmptyBlock(new BlockPos(ownerPosX + x, ownerPosY, ownerPosZ + z)) && this.isEmptyBlock(new BlockPos(ownerPosX + x, ownerPosY + 1, ownerPosZ + z)))
						{
							this.getAIOwnerEntity().setLocationAndAngles((double) ((float) (ownerPosX + x) + 0.5F), (double) ownerPosY, (double) ((float) (ownerPosZ + z) + 0.5F), this.getAIOwnerEntity().rotationYaw, this.getAIOwnerEntity().rotationPitch);

							return;
						}
					}
				}
			}
		}
	}

	// TODO /* ======================================== MOD START =====================================*/

	public boolean isFollowing()
	{
		return (0 < this.timeCounter) && (this.targetOwner != null);
	}

	public void setFollowing(int timeCounter, @Nullable EntityLivingBase entityLivingBase)
	{
		this.timeCounter = timeCounter;
		this.targetOwner = entityLivingBase;
	}

	private boolean canFollowEntityLivingBase(EntityLivingBase entityLivingBase)
	{
		if ((entityLivingBase instanceof EntityPlayer) && !((EntityPlayer) entityLivingBase).isSpectator())
		{
			return true;
		}

		return false;
	}

}
