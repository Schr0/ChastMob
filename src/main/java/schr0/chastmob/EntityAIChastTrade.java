package schr0.chastmob;

import net.minecraft.entity.player.EntityPlayer;

public class EntityAIChastTrade extends EntityAIChast
{

	private EntityPlayer tradePlayer;

	public EntityAIChastTrade(EntityChast entityChast)
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

		if (this.tradePlayer != null)
		{
			return true;
		}

		return false;
	}

	@Override
	public void startExecuting()
	{
		this.getAIOwnerEntity().getNavigator().clearPathEntity();
		this.getAIOwnerEntity().setTrade(true);
	}

	@Override
	public void resetTask()
	{
		this.getAIOwnerEntity().getNavigator().clearPathEntity();
		this.getAIOwnerEntity().setTrade(false);

		this.setTrading(null);
	}

	@Override
	public void updateTask()
	{
		this.getAIOwnerEntity().getLookHelper().setLookPositionWithEntity(this.tradePlayer, 10.0F, this.getAIOwnerEntity().getVerticalFaceSpeed());
	}

	// TODO /* ======================================== MOD START =====================================*/

	public void setTrading(EntityPlayer player)
	{
		this.tradePlayer = player;
	}

}
