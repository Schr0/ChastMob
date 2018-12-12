package schr0.chastmob.entity.ai;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import schr0.chastmob.entity.EntityChast;

public class EntityAIChastWatchClosest extends EntityAIChast
{

	private static final int CHANCE = (50);
	private EntityLivingBase targetClosest;

	public EntityAIChastWatchClosest(EntityChast entityChast)
	{
		super(entityChast);

		this.targetClosest = null;
	}

	@Override
	public boolean shouldExecute()
	{
		if (this.getRandom().nextInt(CHANCE) == 0)
		{
			this.targetClosest = this.getNearestEntity();

			if (this.targetClosest == null)
			{
				return false;
			}

			if (this.canWatchClosest(this.targetClosest))
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

		return true;
	}

	@Override
	public void resetTask()
	{
		super.resetTask();

		this.targetClosest = null;
	}

	@Override
	public void updateTask()
	{
		super.updateTask();

		this.getEntity().getLookHelper().setLookPositionWithEntity(this.targetClosest, this.getEntity().getHorizontalFaceSpeed(), this.getEntity().getVerticalFaceSpeed());
	}

	// TODO /* ======================================== MOD START =====================================*/

	private boolean canWatchClosest(Entity entity)
	{
		if (entity.isEntityAlive())
		{
			return this.getEntity().getEntitySenses().canSee(entity);
		}

		return false;
	}

	@Nullable
	private EntityLivingBase getNearestEntity()
	{
		BlockPos pos = this.getEntity().getPosition();

		return (EntityLivingBase) this.getWorld().findNearestEntityWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos).grow(this.getRange(), this.getRange(), this.getRange()), this.getEntity());
	}

}
