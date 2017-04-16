package schr0.chastmob.entity.ai;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import schr0.chastmob.entity.EntityChast;

public class EntityAIChastTrade extends EntityAIChast
{

	private EntityPlayer tradePlayer;

	public EntityAIChastTrade(EntityChast entityChast)
	{
		super(entityChast);
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

		this.getOwnerEntity().setStateTrade(true);
	}

	@Override
	public void resetTask()
	{
		super.resetTask();

		this.getOwnerEntity().setStateTrade(false);
		this.setTrading(null);
	}

	@Override
	public void updateTask()
	{
		this.getOwnerEntity().getLookHelper().setLookPositionWithEntity(this.tradePlayer, this.getOwnerEntity().getHorizontalFaceSpeed(), this.getOwnerEntity().getVerticalFaceSpeed());
	}

	// TODO /* ======================================== MOD START =====================================*/

	public boolean isTrading()
	{
		return (this.tradePlayer != null);
	}

	public void setTrading(@Nullable EntityPlayer tradePlayer)
	{
		this.tradePlayer = tradePlayer;
	}

}
