package schr0.chastmob.entity.ai;

import net.minecraft.entity.Entity;
import schr0.chastmob.entity.EntityChast;

public class EntityAIChastStateTrade extends EntityAIChast
{

	private static final double TRADER_RANGE = 16.0D;
	private Entity trader;

	public EntityAIChastStateTrade(EntityChast entityChast)
	{
		super(entityChast);
		this.trader = null;
	}

	@Override
	public boolean shouldExecute()
	{
		if (this.trader != null)
		{
			return true;
		}

		return false;
	}

	@Override
	public void resetTask()
	{
		super.resetTask();

		this.trader = null;
		this.getEntity().setTrade(false);
	}

	@Override
	public void updateTask()
	{
		super.updateTask();

		this.getEntity().getLookHelper().setLookPositionWithEntity(this.trader, this.getEntity().getHorizontalFaceSpeed(), this.getEntity().getVerticalFaceSpeed());
	}

	// TODO /* ======================================== MOD START =====================================*/

	public void startTask(Entity trader)
	{
		if (trader != null)
		{
			this.trader = trader;
			this.getEntity().setTrade(true);
		}
	}

}
