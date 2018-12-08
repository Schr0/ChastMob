package schr0.chastmob.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import schr0.chastmob.entity.ChastMode;
import schr0.chastmob.entity.EntityChast;

public class EntityAIChastFollowOwner extends EntityAIChast
{

	private EntityLivingBase targetOwner;

	public EntityAIChastFollowOwner(EntityChast entityChast)
	{
		super(entityChast);
		this.targetOwner = null;
	}

	@Override
	public boolean shouldExecute()
	{
		if (this.getMode() == ChastMode.FOLLOW)
		{
			this.targetOwner = null;

			EntityLivingBase owner = this.getEntity().getOwner();

			if (this.canFollowOwner(owner))
			{
				if (this.getRange() < this.getEntity().getDistanceSq(owner))
				{
					this.targetOwner = owner;

					return true;
				}
			}
		}

		return false;
	}

	@Override
	public boolean shouldContinueExecuting()
	{
		if (this.isTimeOut())
		{
			return false;
		}

		return (this.targetOwner != null);
	}

	@Override
	public void resetTask()
	{
		super.resetTask();

		double minRange = (this.getRange() * this.getRange());

		if ((this.targetOwner != null) && (minRange < this.getEntity().getDistanceSq(this.targetOwner)))
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

		if (this.getEntity().getDistanceSq(this.targetOwner) < this.getRange())
		{
			this.targetOwner = null;
		}
		else
		{
			this.getEntity().getNavigator().tryMoveToEntityLiving(this.targetOwner, this.getSpeed());
		}
	}

	// TODO /* ======================================== MOD START =====================================*/

	private boolean canFollowOwner(EntityLivingBase owner)
	{
		if (owner instanceof EntityPlayer)
		{
			return !((EntityPlayer) owner).isSpectator();
		}

		return false;
	}

}
