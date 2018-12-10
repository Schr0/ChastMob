package schr0.chastmob.entity.ai;

import schr0.chastmob.entity.EntityChast;

public class EntityAIChastYawn extends EntityAIChast
{

	private static final float CHANCE = 0.005F;

	public EntityAIChastYawn(EntityChast entityChast)
	{
		super(entityChast);
	}

	@Override
	public boolean shouldExecute()
	{
		if (this.getWorld().rand.nextFloat() < CHANCE)
		{
			return true;
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
	public void startExecuting()
	{
		super.startExecuting();

		this.getEntity().setCoverOpen(true);
	}

}
