package schr0.chastmob.entity.ai;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import schr0.chastmob.entity.EntityChast;

public class EntityAIChastStateSit extends EntityAIChast
{

	private static final double OCELOT_SIT_RANGE = 1.5D;
	private static final double OCELOT_MOVE_SPEED = 0.8D;
	private boolean isSitting;

	public EntityAIChastStateSit(EntityChast entityChast)
	{
		super(entityChast);

		this.isSitting = false;
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

		EntityOcelot nearestOcelot = this.getNearestEntityOcelot();

		if (nearestOcelot != null)
		{
			nearestOcelot.getLookHelper().setLookPositionWithEntity(this.getEntity(), nearestOcelot.getHorizontalFaceSpeed(), nearestOcelot.getVerticalFaceSpeed());

			if (nearestOcelot.getDistanceSq(this.getEntity()) < OCELOT_SIT_RANGE)
			{
				nearestOcelot.startRiding(this.getEntity());
			}
			else
			{
				nearestOcelot.getNavigator().tryMoveToEntityLiving(this.getEntity(), OCELOT_MOVE_SPEED);
			}
		}
	}

	// TODO /* ======================================== MOD START =====================================*/

	public void startTask()
	{
		this.isSitting = true;
		this.getEntity().setSit(true);
	}

	@Nullable
	private EntityOcelot getNearestEntityOcelot()
	{
		EntityOcelot nearestEntityOcelot = null;
		double rangeOrigin = 0;

		for (EntityOcelot aroundEntityOcelot : this.getAroundEntityOcelots())
		{
			if (this.canOcelotSit(aroundEntityOcelot))
			{
				double range = this.getEntity().getDistanceSq(aroundEntityOcelot);

				if ((range < rangeOrigin) || (rangeOrigin == 0))
				{
					rangeOrigin = range;

					nearestEntityOcelot = aroundEntityOcelot;
				}
			}
		}

		return nearestEntityOcelot;
	}

	private List<EntityOcelot> getAroundEntityOcelots()
	{
		BlockPos pos = this.getEntity().getCenterPosition();

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
