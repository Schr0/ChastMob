package schr0.chastmob;

public class EntityAIChastSit extends EntityAIChast
{

	private boolean aiIsSitting;

	public EntityAIChastSit(EntityChast entityChast)
	{
		super(entityChast);
		this.setMutexBits(1);
	}

	@Override
	public boolean shouldExecute()
	{
		if (this.getAIChastEntity().isInWater() || !this.getAIChastEntity().onGround)
		{
			return false;
		}

		return this.aiIsSitting;
	}

	@Override
	public void startExecuting()
	{
		this.getAIChastEntity().getNavigator().clearPathEntity();
		this.getAIChastEntity().setSitting(true);
	}

	@Override
	public void resetTask()
	{
		this.getAIChastEntity().getNavigator().clearPathEntity();
		this.getAIChastEntity().setSitting(false);
	}

	// TODO /* ======================================== MOD START =====================================*/

	public void setSitting(boolean isSitting)
	{
		this.aiIsSitting = isSitting;
	}

}
