package schr0.chastmob.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import schr0.chastmob.entity.ChastMode;
import schr0.chastmob.entity.EntityChast;

public class EntityAIChastFollowOwner extends EntityAIChast
{

	private double speed;
	private double distance;
	private EntityLivingBase targetOwner;

	public EntityAIChastFollowOwner(EntityChast entityChast, double speed, double distance)
	{
		super(entityChast);
		this.speed = speed;
		this.distance = (distance * distance);
		this.targetOwner = null;
	}

	@Override
	public boolean shouldExecute()
	{
		this.targetOwner = null;

		if (this.getMode() != ChastMode.FOLLOW)
		{
			return false;
		}

		EntityLivingBase owner = this.getEntity().getOwner();

		if (this.canFollowEntityLivingBase(owner))
		{
			if (this.getEntity().getDistanceSq(owner) < this.distance)
			{
				return false;
			}
			else
			{
				this.targetOwner = owner;

				return true;
			}
		}

		return false;
	}

	@Override
	public boolean shouldContinueExecuting()
	{
		if (this.getMode() != ChastMode.FOLLOW)
		{
			return false;
		}

		if (this.targetOwner != null)
		{
			return this.isExecutingTime();
		}

		return false;
	}

	@Override
	public void resetTask()
	{
		super.resetTask();

		if (this.targetOwner != null)
		{
			this.forceMoveToTargetEntity(this.targetOwner);
		}

		this.targetOwner = null;
	}

	@Override
	public void updateTask()
	{
		super.updateTask();

		this.getEntity().getLookHelper().setLookPositionWithEntity(this.targetOwner, this.getEntity().getHorizontalFaceSpeed(), this.getEntity().getVerticalFaceSpeed());

		if (this.getEntity().getDistanceSq(this.targetOwner) < this.distance)
		{
			this.targetOwner = null;
		}
		else
		{
			this.getEntity().getNavigator().tryMoveToEntityLiving(this.targetOwner, this.speed);
		}
	}

	// TODO /* ======================================== MOD START =====================================*/

	private boolean canFollowEntityLivingBase(EntityLivingBase entityLivingBase)
	{
		if ((entityLivingBase instanceof EntityPlayer) && !((EntityPlayer) entityLivingBase).isSpectator())
		{
			return true;
		}

		return false;
	}

}
