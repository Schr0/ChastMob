package schr0.chastmob.entity.ai;

import net.minecraft.entity.player.EntityPlayer;
import schr0.chastmob.entity.EntityChast;

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
		if (this.isTrading())
		{
			return true;
		}

		return false;
	}

	@Override
	public void startExecuting()
	{
		super.startExecuting();
		this.getAIOwnerEntity().setStateTrade(true);
	}

	@Override
	public void resetTask()
	{
		super.resetTask();
		this.getAIOwnerEntity().setStateTrade(false);

		this.setTrading(null);
	}

	@Override
	public void updateTask()
	{
		this.getAIOwnerEntity().getLookHelper().setLookPositionWithEntity(this.tradePlayer, 10.0F, this.getAIOwnerEntity().getVerticalFaceSpeed());
	}

	// TODO /* ======================================== MOD START =====================================*/

	public boolean isTrading()
	{
		return (this.tradePlayer != null);
	}

	public void setTrading(EntityPlayer tradePlayer)
	{
		this.tradePlayer = tradePlayer;
	}

}
