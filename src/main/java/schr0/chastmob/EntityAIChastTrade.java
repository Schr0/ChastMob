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
		if (this.getAIOwnerChast().isPanicking())
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
		this.getAIOwnerChast().getNavigator().clearPathEntity();
		this.getAIOwnerChast().setTrading(true);
	}

	@Override
	public void resetTask()
	{
		this.getAIOwnerChast().getNavigator().clearPathEntity();
		this.getAIOwnerChast().setTrading(false);
	}

	@Override
	public void updateTask()
	{
		this.getAIOwnerChast().getLookHelper().setLookPositionWithEntity(this.tradePlayer, 10.0F, this.getAIOwnerChast().getVerticalFaceSpeed());
	}

	// TODO /* ======================================== MOD START =====================================*/

	public void setTrading(EntityPlayer player)
	{
		this.tradePlayer = player;
	}

}
