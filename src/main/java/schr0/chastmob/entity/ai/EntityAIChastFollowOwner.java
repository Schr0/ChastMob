package schr0.chastmob.entity.ai;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import schr0.chastmob.entity.EntityChast;

public class EntityAIChastFollowOwner extends EntityAIChast
{

	private static final int TIME_LIMIT = (5 * 20);
	private int timeCounter;

	private double speed;
	private double distance;
	private EntityLivingBase targetOwner;

	public EntityAIChastFollowOwner(EntityChast entityChast, double speed, double distance)
	{
		super(entityChast);
		this.setMutexBits(1);

		this.speed = speed;
		this.distance = (distance * distance);
	}

	@Override
	public boolean shouldExecute()
	{
		if (this.getAIOwnerEntity().getAIMode() == EnumAIMode.FOLLOW)
		{
			EntityLivingBase entityOwner = this.getAIOwnerEntity().getOwnerEntity();

			if (this.canFollowEntityLivingBase(entityOwner))
			{
				if (this.getAIOwnerEntity().getDistanceSqToEntity(entityOwner) < this.distance)
				{
					return false;
				}
				else
				{
					this.setFollowing(TIME_LIMIT, entityOwner);

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

		this.getAIOwnerEntity().getLookHelper().setLookPositionWithEntity(this.targetOwner, this.getAIOwnerEntity().getHorizontalFaceSpeed(), this.getAIOwnerEntity().getVerticalFaceSpeed());

		if (this.getAIOwnerEntity().getDistanceSqToEntity(this.targetOwner) < this.distance)
		{
			this.setFollowing(0, null);
		}
		else
		{
			if (this.timeCounter < 20)
			{
				this.forceMoveToTargetEntity(this.targetOwner, this.speed);
			}
			else
			{
				this.getAIOwnerEntity().getNavigator().tryMoveToEntityLiving(this.targetOwner, this.speed);
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
