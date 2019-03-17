package schr0.chastmob.entity.ai;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import schr0.chastmob.entity.EntityChast;

public class EntityAIChastTrade extends EntityAIChast
{

	private static final double TRADER_RANGE = 16.0D;
	private Entity trader;

	public EntityAIChastTrade(EntityChast entityChast)
	{
		super(entityChast);
	}

	@Override
	public boolean shouldExecute()
	{
		return (this.trader != null);
	}

	@Override
	public void startExecuting()
	{
		super.startExecuting();

		this.getEntity().setTrade(true);
	}

	@Override
	public void resetTask()
	{
		super.resetTask();

		this.getEntity().setTrade(false);
	}

	@Override
	public void updateTask()
	{
		super.updateTask();

		this.getEntity().getLookHelper().setLookPositionWithEntity(this.trader, this.getEntity().getHorizontalFaceSpeed(), this.getEntity().getVerticalFaceSpeed());
	}

	// TODO /* ======================================== MOD START =====================================*/

	public void setTrading(@Nullable EntityPlayer trader)
	{
		this.trader = trader;
	}

}
