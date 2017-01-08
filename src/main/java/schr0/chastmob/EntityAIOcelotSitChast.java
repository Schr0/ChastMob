package schr0.chastmob;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.ai.EntityAIOcelotSit;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityAIOcelotSitChast extends EntityAIOcelotSit
{

	private static final int SIT_TIME_LIMIT = (5 * 20);

	private EntityOcelot theOwnerEntity;
	private World theOwnerWorld;
	private BlockPos theOwnerBlockPos;

	private float moveSpeed;
	private double maxDistance;
	private EntityChast targetEntityChast;
	private int sitTime;
	private boolean isEntityAIOcelotSitChast;

	public EntityAIOcelotSitChast(EntityOcelot owner, float moveSpeed, double maxDistance)
	{
		super(owner, moveSpeed);

		this.theOwnerEntity = owner;
		this.theOwnerWorld = owner.worldObj;
		this.theOwnerBlockPos = new BlockPos(owner);
		this.moveSpeed = moveSpeed;
		this.maxDistance = maxDistance;
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

			if (this.theOwnerEntity.isTamed() && !this.theOwnerEntity.isSitting())
			{
				List<EntityChast> listEntityChast = this.theOwnerWorld.getEntitiesWithinAABB(EntityChast.class, new AxisAlignedBB(this.theOwnerBlockPos).expandXyz(this.maxDistance));

				for (EntityChast entityChast : listEntityChast)
				{
					if (this.canSitEntityChast(entityChast))
					{
						this.isEntityAIOcelotSitChast = true;

						this.setTargetChast(entityChast, SIT_TIME_LIMIT);

						break;
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
			return this.hasTargetChast();
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
			this.theOwnerEntity.getAISit().setSitting(true);
			this.theOwnerEntity.setSitting(true);

			this.setTargetChast(null, 0);
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
			--this.sitTime;

			this.theOwnerEntity.getLookHelper().setLookPositionWithEntity(this.targetEntityChast, 10.0F, this.theOwnerEntity.getVerticalFaceSpeed());

			if (this.theOwnerEntity.getDistanceSqToEntity(this.targetEntityChast) < 1.5D)
			{
				List<EntityChast> listEntityChast = this.theOwnerWorld.getEntitiesWithinAABB(EntityChast.class, new AxisAlignedBB(this.theOwnerBlockPos).expandXyz(maxDistance));

				for (EntityChast entityChast : listEntityChast)
				{
					if (entityChast.equals(this.targetEntityChast) && this.canSitEntityChast(entityChast))
					{
						this.theOwnerEntity.startRiding(entityChast);

						this.setTargetChast(null, 0);

						return;
					}
				}
			}
			else
			{
				this.theOwnerEntity.getNavigator().tryMoveToEntityLiving(this.targetEntityChast, this.moveSpeed);
			}
		}
		else
		{
			super.updateTask();
		}
	}

	// TODO /* ======================================== MOD START =====================================*/

	private boolean hasTargetChast()
	{
		return ((this.targetEntityChast != null) && (0 < this.sitTime));
	}

	private void setTargetChast(@Nullable EntityChast entityChast, int sitTime)
	{
		this.targetEntityChast = entityChast;
		this.sitTime = sitTime;
	}

	private boolean canSitEntityChast(EntityChast entityChast)
	{
		if (this.theOwnerEntity.getEntitySenses().canSee(entityChast))
		{
			return (entityChast.isEntityAlive() && !entityChast.isBeingRidden() && entityChast.isSit());
		}

		return false;
	}

}
