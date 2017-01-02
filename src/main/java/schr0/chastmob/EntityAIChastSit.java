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
		if (this.getAIOwnerChast().isPanicking())
		{
			return false;
		}

		if (this.getAIOwnerChast().isInWater() || !this.getAIOwnerChast().onGround)
		{
			return false;
		}

		return this.isSitting;
	}

	@Override
	public void startExecuting()
	{
		this.getAIOwnerChast().getNavigator().clearPathEntity();
		this.getAIOwnerChast().setSitting(true);
	}

	@Override
	public void resetTask()
	{
		this.getAIOwnerChast().getNavigator().clearPathEntity();
		this.getAIOwnerChast().setSitting(false);
	}

	// TODO /* ======================================== MOD START =====================================*/

	public void setSitting(boolean isSitting)
	{
		this.isSitting = isSitting;
	}

}
