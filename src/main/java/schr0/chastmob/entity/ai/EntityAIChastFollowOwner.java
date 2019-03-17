package schr0.chastmob.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import schr0.chastmob.entity.ChastMode;
import schr0.chastmob.entity.EntityChast;

public class EntityAIChastFollowOwner extends EntityAIChast
{

	public EntityAIChastFollowOwner(EntityChast entityChast)
	{
		super(entityChast);
	}

	@Override
	public boolean shouldExecute()
	{
		if (this.getEntity().getMode() == ChastMode.FOLLOW)
		{
			EntityLivingBase owner = this.getEntity().getOwner();

			if (owner == null)
			{
				return false;
			}

			if (owner instanceof EntityPlayer)
			{
				EntityPlayer player = (EntityPlayer) owner;

				if (player.isSpectator())
				{
					return false;
				}
			}

			if (this.getFollowRange() < this.getEntity().getDistanceSq(owner))
			{
				return true;
			}
		}

		return false;
	}

	@Override
	public void updateTask()
	{
		super.updateTask();

		EntityLivingBase owner = this.getEntity().getOwner();

		if (owner == null)
		{
			return;
		}

		this.getEntity().getLookHelper().setLookPositionWithEntity(owner, this.getEntity().getHorizontalFaceSpeed(), this.getEntity().getVerticalFaceSpeed());

		if (this.getSearchRange() < this.getEntity().getDistanceSq(owner))
		{
			if (this.isTimeOut())
			{
				if (this.getEntity().canEntityBeSeen(owner))
				{
					this.forceMoveToTargetEntity(owner);
				}
			}

			this.getEntity().getNavigator().tryMoveToEntityLiving(owner, this.getMoveSpeed());
		}
	}

	// TODO /* ======================================== MOD START =====================================*/

	private int getFollowRange()
	{
		return (this.getSearchRange() * this.getSearchRange());
	}

}
