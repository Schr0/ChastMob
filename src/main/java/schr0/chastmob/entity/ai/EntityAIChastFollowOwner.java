package schr0.chastmob.entity.ai;

import javax.annotation.Nullable;

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
			this.targetOwner = this.getFollowOwner();

			if (this.targetOwner != null)
			{
				return true;
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

		if (this.targetOwner != null)
		{
			if (this.getFollowRange() < this.getEntity().getDistanceSq(this.targetOwner))
			{
				this.forceMoveToTargetEntity(this.targetOwner);
			}
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

	private int getFollowRange()
	{
		return (this.getRange() * this.getRange());
	}

	@Nullable
	private EntityLivingBase getFollowOwner()
	{
		EntityLivingBase owner = this.getEntity().getOwner();

		if (owner == null)
		{
			return (EntityLivingBase) null;
		}

		if (owner instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer) owner;

			if (player.isSpectator())
			{
				return (EntityLivingBase) null;
			}
		}

		if (this.getFollowRange() < this.getEntity().getDistanceSq(owner))
		{
			return owner;
		}

		return (EntityLivingBase) null;
	}

}
