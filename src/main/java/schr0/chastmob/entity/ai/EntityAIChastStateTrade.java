package schr0.chastmob.entity.ai;

import net.minecraft.entity.Entity;
import schr0.chastmob.entity.EntityChast;

public class EntityAIChastStateTrade extends EntityAIChast
{

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
		this.trader = null;
	}

	@Override
	public void updateTask()
	{
		this.getEntity().getLookHelper().setLookPositionWithEntity(this.trader, this.getEntity().getHorizontalFaceSpeed(), this.getEntity().getVerticalFaceSpeed());
	}

	// TODO /* ======================================== MOD START =====================================*/

	public void startTask(Entity trader)
	{
		if (trader != null)
		{
			this.trader = trader;
		}
	}

	public void stopTask()
	{
		this.trader = null;
	}

}
