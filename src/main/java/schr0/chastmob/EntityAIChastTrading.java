package schr0.chastmob;

import net.minecraft.entity.player.EntityPlayer;

public class EntityAIChastTrading extends EntityAIChast
{

	private EntityPlayer aiTradePlayer;

	public EntityAIChastTrading(EntityChast entityChast)
	{
		super(entityChast);
		this.setMutexBits(1);
	}

	@Override
	public boolean shouldExecute()
	{
		if (this.getAIChastEntity().getTradePlayer() != null)
		{
			return true;
		}

		return false;
	}

	@Override
	public void startExecuting()
	{
		this.getAIChastEntity().getNavigator().clearPathEntity();
		this.setTradePlayer(this.getAIChastEntity().getTradePlayer());
	}

	@Override
	public void resetTask()
	{
		this.getAIChastEntity().getNavigator().clearPathEntity();
		this.setTradePlayer(null);
	}

	@Override
	public void updateTask()
	{
		this.getAIChastEntity().getLookHelper().setLookPositionWithEntity(this.aiTradePlayer, 10.0F, this.getAIChastEntity().getVerticalFaceSpeed());
	}

	// TODO /* ======================================== MOD START =====================================*/

	private void setTradePlayer(EntityPlayer player)
	{
		this.aiTradePlayer = player;
	}

}
