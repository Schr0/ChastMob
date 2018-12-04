package schr0.chastmob.entity.ai;

import schr0.chastmob.entity.EntityChast;

public class EntityAIChastStateSit extends EntityAIChast
{

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

		this.isSitting = false;
		this.getEntity().setSit(false);
	}

	// TODO /* ======================================== MOD START =====================================*/

	public void startTask()
	{
		this.isSitting = true;
		this.getEntity().setSit(true);
	}

	public void stopTask()
	{
		this.isSitting = false;
		this.getEntity().setSit(false);
	}

}
