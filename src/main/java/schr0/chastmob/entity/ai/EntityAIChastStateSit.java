package schr0.chastmob.entity.ai;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import schr0.chastmob.entity.EntityChast;

public class EntityAIChastStateSit extends EntityAIChast
{

	private boolean isSitting;

	private static final double OCELOT_SIT_RANGE = 1.5D;
	private static final double OCELOT_MOVE_SPEED = 0.8D;
	private float ocelotRangeOrigin;

	public EntityAIChastStateSit(EntityChast entityChast)
	{
		super(entityChast);
		this.isSitting = false;
		this.ocelotRangeOrigin = 0;
	}

	@Override
	public boolean shouldExecute()
	{
		if (this.getEntity().isInWater())
		{
			return false;
		}

		return this.isSitting;
	}

	@Override
	public void resetTask()
	{
		super.resetTask();

		for (Entity passenger : this.getEntity().getPassengers())
		{
			if (passenger.isEntityAlive())
			{
				passenger.dismountRidingEntity();
			}
		}

		this.isSitting = false;
		this.ocelotRangeOrigin = 0;
		this.getEntity().setSit(false);
	}

	@Override
	public void updateTask()
	{
		super.updateTask();

		for (Entity passenger : this.getEntity().getPassengers())
		{
			if (passenger instanceof EntityOcelot)
			{
				EntityOcelot ocelot = (EntityOcelot) passenger;

				if (!ocelot.isSitting())
				{
					ocelot.getAISit().setSitting(true);
				}

				if (!ocelot.onGround)
				{
					ocelot.onGround = true;
				}

				return;
			}
		}

		for (EntityOcelot ocelot : this.getAroundOcelot())
		{
			float range = (float) this.getEntity().getDistanceSq(ocelot);

			if ((range < this.ocelotRangeOrigin) || (this.ocelotRangeOrigin == 0.0F))
			{
				this.ocelotRangeOrigin = range;

				if (this.canOcelotSit(ocelot))
				{
					ocelot.getLookHelper().setLookPositionWithEntity(this.getEntity(), ocelot.getHorizontalFaceSpeed(), ocelot.getVerticalFaceSpeed());

					if (ocelot.getDistanceSq(this.getEntity()) < OCELOT_SIT_RANGE)
					{
						ocelot.startRiding(this.getEntity());
					}
					else
					{
						ocelot.getNavigator().tryMoveToEntityLiving(this.getEntity(), OCELOT_MOVE_SPEED);
					}
				}
			}
		}
	}

	// TODO /* ======================================== MOD START =====================================*/

	public void startTask()
	{
		this.isSitting = true;
		this.getEntity().setSit(true);
	}

	private List<EntityOcelot> getAroundOcelot()
	{
		BlockPos pos = this.getEntity().getPosition();

		return this.getWorld().getEntitiesWithinAABB(EntityOcelot.class, new AxisAlignedBB(pos).grow(this.getRange(), this.getRange(), this.getRange()));
	}

	private boolean canOcelotSit(EntityOcelot ocelot)
	{
		if (ocelot.isTamed() && !ocelot.isSitting())
		{
			return ocelot.getEntitySenses().canSee(this.getEntity());
		}

		return false;
	}

}
