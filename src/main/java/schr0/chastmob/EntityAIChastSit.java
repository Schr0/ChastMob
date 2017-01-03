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
		if (this.getAIOwnerEntity().isPanic())
		{
			return false;
		}

		if (this.getAIOwnerEntity().isInWater() || !this.getAIOwnerEntity().onGround)
		{
			return false;
		}

		return this.isSitting;
	}

	@Override
	public void startExecuting()
	{
		this.getAIOwnerEntity().getNavigator().clearPathEntity();
		this.getAIOwnerEntity().setSitting(true);
	}

	@Override
	public void resetTask()
	{
		this.getAIOwnerEntity().getNavigator().clearPathEntity();
		this.getAIOwnerEntity().setSitting(false);

		this.setSitting(false);
	}

	// TODO /* ======================================== MOD START =====================================*/

	public void setSitting(boolean isSitting)
	{
		this.isSitting = isSitting;
	}

}
