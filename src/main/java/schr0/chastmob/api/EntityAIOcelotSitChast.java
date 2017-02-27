package schr0.chastmob.api;

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

	private EntityOcelot theOwnerEntity;
	private World theOwnerWorld;
	private BlockPos theOwnerBlockPos;
	private int timeCounter;

	private boolean isEntityAIOcelotSitChast;
	private float speed;
	private double distance;
	private EntityChast targetEntityChast;

	public EntityAIOcelotSitChast(EntityOcelot entityOcelot, float speed, double distance)
	{
		super(entityOcelot, speed);

		this.theOwnerEntity = entityOcelot;
		this.theOwnerWorld = entityOcelot.getEntityWorld();
		this.theOwnerBlockPos = entityOcelot.getPosition();
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

			if (this.theOwnerEntity.isTamed())
			{
				if (this.theOwnerEntity.getRidingEntity() instanceof EntityChast)
				{
					this.isEntityAIOcelotSitChast = true;
				}
				else
				{
					if (!this.theOwnerEntity.isSitting())
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
	public boolean continueExecuting()
	{
		if (this.isEntityAIOcelotSitChast)
		{
			return this.isSitting();
		}
		else
		{
			return super.continueExecuting();
		}
	}

	@Override
	public void startExecuting()
	{
		if (this.isEntityAIOcelotSitChast)
		{
			this.theOwnerEntity.getAISit().setSitting(false);
			this.theOwnerEntity.setSitting(false);
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
			this.theOwnerEntity.getAISit().setSitting(false);
			this.theOwnerEntity.setSitting(false);

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
			if (this.theOwnerEntity.getRidingEntity() instanceof EntityChast)
			{
				if (!this.theOwnerEntity.isSitting())
				{
					this.theOwnerEntity.setSitting(true);
				}
			}
			else
			{
				--this.timeCounter;

				this.theOwnerEntity.getLookHelper().setLookPositionWithEntity(this.targetEntityChast, this.theOwnerEntity.getHorizontalFaceSpeed(), this.theOwnerEntity.getVerticalFaceSpeed());

				if (this.theOwnerEntity.getDistanceSqToEntity(this.targetEntityChast) < 1.5D)
				{
					for (EntityChast entityChast : this.getAroundEntityChast())
					{
						if (entityChast.equals(this.targetEntityChast) && this.canSittingEntityChast(entityChast))
						{
							this.theOwnerEntity.startRiding(entityChast);

							this.setSitting(0, null);

							return;
						}
					}
				}
				else
				{
					this.theOwnerEntity.getNavigator().tryMoveToEntityLiving(this.targetEntityChast, this.speed);
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
		if (this.theOwnerEntity.getRidingEntity() instanceof EntityChast)
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
		return this.theOwnerWorld.getEntitiesWithinAABB(EntityChast.class, new AxisAlignedBB(this.theOwnerBlockPos).expandXyz(this.distance));
	}

	private boolean canSittingEntityChast(EntityChast entityChast)
	{
		if (this.theOwnerEntity.getEntitySenses().canSee(entityChast))
		{
			return (entityChast.isEntityAlive() && !entityChast.isBeingRidden() && entityChast.isStateSit());
		}

		return false;
	}

}
