package schr0.chastmob.util;

import java.util.List;

import net.minecraft.entity.ai.EntityAIOcelotSit;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import schr0.chastmob.entity.EntityChast;

public class EntityAIOcelotSitChast extends EntityAIOcelotSit
{

	private static final int TIME_LIMIT = (5 * 20);
	private int timeCounter;

	private EntityOcelot owner;
	private BlockPos ownerPosition;
	private World world;
	private float speed;
	private double distance;
	private boolean isEnableAI;
	private EntityChast target;

	public EntityAIOcelotSitChast(EntityOcelot entityOcelot, float speed, double distance)
	{
		super(entityOcelot, speed);

		this.owner = entityOcelot;
		this.ownerPosition = entityOcelot.getPosition();
		this.world = entityOcelot.getEntityWorld();
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
			this.isEnableAI = false;

			if (this.owner.isTamed())
			{
				if (this.owner.getRidingEntity() instanceof EntityChast)
				{
					this.isEnableAI = true;
				}
				else
				{
					if (!this.owner.isSitting())
					{
						for (EntityChast entityChast : this.getAroundEntityChast())
						{
							if (this.canSittingEntityChast(entityChast))
							{
								this.startTask(entityChast);

								this.isEnableAI = true;
							}
						}
					}
				}
			}

			return this.isEnableAI;
		}
	}

	@Override
	public boolean shouldContinueExecuting()
	{
		if (this.isEnableAI)
		{
			if (this.owner.isTamed())
			{
				if (this.owner.getRidingEntity() instanceof EntityChast)
				{
					return true;
				}
				else
				{
					return ((0 < this.timeCounter) && (this.target != null));
				}
			}

			return false;
		}
		else
		{
			return super.shouldContinueExecuting();
		}
	}

	@Override
	public void startExecuting()
	{
		if (this.isEnableAI)
		{
			this.owner.getAISit().setSitting(false);
			this.owner.setSitting(false);
		}
		else
		{
			super.startExecuting();
		}
	}

	@Override
	public void resetTask()
	{
		if (this.isEnableAI)
		{
			this.owner.getAISit().setSitting(false);
			this.owner.setSitting(false);

			this.stopTask();
		}
		else
		{
			super.resetTask();
		}
	}

	@Override
	public void updateTask()
	{
		if (this.isEnableAI)
		{
			if (this.owner.getRidingEntity() instanceof EntityChast)
			{
				if (!this.owner.isSitting())
				{
					this.owner.setSitting(true);
				}
			}
			else
			{
				--this.timeCounter;

				this.owner.getLookHelper().setLookPositionWithEntity(this.target, this.owner.getHorizontalFaceSpeed(), this.owner.getVerticalFaceSpeed());

				if (this.owner.getDistanceSq(this.target) < 1.5D)
				{
					for (EntityChast entityChast : this.getAroundEntityChast())
					{
						if (entityChast.equals(this.target) && this.canSittingEntityChast(entityChast))
						{
							this.owner.startRiding(entityChast);

							this.stopTask();

							return;
						}
					}
				}
				else
				{
					this.owner.getNavigator().tryMoveToEntityLiving(this.target, this.speed);
				}
			}
		}
		else
		{
			super.updateTask();
		}
	}

	// TODO /* ======================================== MOD START =====================================*/

	public void startTask(EntityChast entityChast)
	{
		this.timeCounter = TIME_LIMIT;
		this.target = entityChast;
	}

	public void stopTask()
	{
		this.timeCounter = 0;
		this.target = null;
	}

	private List<EntityChast> getAroundEntityChast()
	{
		return this.world.getEntitiesWithinAABB(EntityChast.class, new AxisAlignedBB(this.ownerPosition).grow(this.distance, this.distance, this.distance));
	}

	private boolean canSittingEntityChast(EntityChast entityChast)
	{
		if (this.owner.getEntitySenses().canSee(entityChast))
		{
			return (entityChast.isEntityAlive() && !entityChast.isBeingRidden() && entityChast.isSit());
		}

		return false;
	}

}
