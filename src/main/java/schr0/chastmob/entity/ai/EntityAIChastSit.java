package schr0.chastmob.entity.ai;

import schr0.chastmob.entity.EntityChast;

public class EntityAIChastSit extends EntityAIChast
{

	private boolean isSitting;

	public EntityAIChastSit(EntityChast entityChast)
	{
		super(entityChast);
	}

	@Override
	public boolean shouldExecute()
	{
		if (this.getOwnerEntity().isInWater())
		{
			return false;
		}

		return this.isSitting();
	}

	@Override
	public void startExecuting()
	{
		super.startExecuting();

		this.getOwnerEntity().setStateSit(true);
	}

	@Override
	public void resetTask()
	{
		super.resetTask();

		this.getOwnerEntity().setStateSit(false);
		this.setSitting(false);
	}

	// TODO /* ======================================== MOD START =====================================*/

	public boolean isSitting()
	{
		return this.isSitting;
	}

	public void setSitting(boolean isSitting)
	{
		this.isSitting = isSitting;
	}

}
