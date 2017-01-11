package schr0.chastmob;

public class EntityAIChastSit extends EntityAIChast
{

	private boolean isSitting;

	public EntityAIChastSit(EntityChast entityChast)
	{
		super(entityChast);
		this.setMutexBits(1);
	}

	@Override
	public boolean shouldExecute()
	{
		if (this.getAIOwnerEntity().isInWater() || !this.getAIOwnerEntity().onGround)
		{
			return false;
		}

		return this.isSitting();
	}

	@Override
	public void startExecuting()
	{
		super.startExecuting();
		this.getAIOwnerEntity().setStateSit(true);
	}

	@Override
	public void resetTask()
	{
		super.resetTask();
		this.getAIOwnerEntity().setStateSit(false);

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
