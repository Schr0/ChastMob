package schr0.chastmob.vanilla;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.ai.EntityAIOcelotSit;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import schr0.chastmob.entity.EntityChast;

public class EntityAIOcelotSitChast extends EntityAIOcelotSit
{

	private static final int TIME_LIMIT = (5 * 20);

	private EntityOcelot ownerEntityOcelot;
	private World ownerWorld;
	private BlockPos ownerPosition;
	private int timeCounter;

	private boolean isEntityAIOcelotSitChast;
	private float speed;
	private double distance;
	private EntityChast targetEntityChast;

	public EntityAIOcelotSitChast(EntityOcelot entityOcelot, float speed, double distance)
	{
		super(entityOcelot, speed);

		this.ownerEntityOcelot = entityOcelot;
		this.ownerWorld = entityOcelot.getEntityWorld();
		this.ownerPosition = entityOcelot.getPosition();
		this.speed = speed;
		this.distance = distance;
	}

	@Override
	public boolean shouldExecute()
	{
		if (super.shouldExecute())
		{
			return true;
		}
		else
		{
			this.isEntityAIOcelotSitChast = false;

			if (this.ownerEntityOcelot.isTamed())
			{
				if (this.ownerEntityOcelot.getRidingEntity() instanceof EntityChast)
				{
					this.isEntityAIOcelotSitChast = true;
				}
				else
				{
					if (!this.ownerEntityOcelot.isSitting())
					{
						for (EntityChast entityChast : this.getAroundEntityChast())
						{
							if (this.canSittingEntityChast(entityChast))
							{
								this.isEntityAIOcelotSitChast = true;

								this.setSitting(TIME_LIMIT, entityChast);

								break;
							}
						}
					}
				}
			}

			return this.isEntityAIOcelotSitChast;
		}
	}

	@Override
	public boolean shouldContinueExecuting()
	{
		if (this.isEntityAIOcelotSitChast)
		{
			return this.isSitting();
		}
		else
		{
			return super.shouldContinueExecuting();
		}
	}

	@Override
	public void startExecuting()
	{
		if (this.isEntityAIOcelotSitChast)
		{
			this.ownerEntityOcelot.getAISit().setSitting(false);
			this.ownerEntityOcelot.setSitting(false);
		}
		else
		{
			super.startExecuting();
		}
	}

	@Override
	public void resetTask()
	{
		if (this.isEntityAIOcelotSitChast)
		{
			this.ownerEntityOcelot.getAISit().setSitting(false);
			this.ownerEntityOcelot.setSitting(false);

			this.setSitting(0, null);
		}
		else
		{
			super.resetTask();
		}
	}

	@Override
	public void updateTask()
	{
		if (this.isEntityAIOcelotSitChast)
		{
			if (this.ownerEntityOcelot.getRidingEntity() instanceof EntityChast)
			{
				if (!this.ownerEntityOcelot.isSitting())
				{
					this.ownerEntityOcelot.setSitting(true);
				}
			}
			else
			{
				--this.timeCounter;

				this.ownerEntityOcelot.getLookHelper().setLookPositionWithEntity(this.targetEntityChast, this.ownerEntityOcelot.getHorizontalFaceSpeed(), this.ownerEntityOcelot.getVerticalFaceSpeed());

				if (this.ownerEntityOcelot.getDistanceSq(this.targetEntityChast) < 1.5D)
				{
					for (EntityChast entityChast : this.getAroundEntityChast())
					{
						if (entityChast.equals(this.targetEntityChast) && this.canSittingEntityChast(entityChast))
						{
							this.ownerEntityOcelot.startRiding(entityChast);

							this.setSitting(0, null);

							return;
						}
					}
				}
				else
				{
					this.ownerEntityOcelot.getNavigator().tryMoveToEntityLiving(this.targetEntityChast, this.speed);
				}
			}
		}
		else
		{
			super.updateTask();
		}
	}

	// TODO /* ======================================== MOD START =====================================*/

	public boolean isSitting()
	{
		if (this.ownerEntityOcelot.getRidingEntity() instanceof EntityChast)
		{
			return true;
		}
		else
		{
			return (0 < this.timeCounter) && (this.targetEntityChast != null);
		}
	}

	public void setSitting(int timeCounter, @Nullable EntityChast entityChast)
	{
		this.timeCounter = timeCounter;
		this.targetEntityChast = entityChast;
	}

	private List<EntityChast> getAroundEntityChast()
	{
		return this.ownerWorld.getEntitiesWithinAABB(EntityChast.class, new AxisAlignedBB(this.ownerPosition).grow(this.distance, this.distance, this.distance));
	}

	private boolean canSittingEntityChast(EntityChast entityChast)
	{
		if (this.ownerEntityOcelot.getEntitySenses().canSee(entityChast))
		{
			return (entityChast.isEntityAlive() && !entityChast.isBeingRidden() && entityChast.isStateSit());
		}

		return false;
	}

}
