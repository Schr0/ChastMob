package schr0.chastmob;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class EntityAIChast extends EntityAIBase
{

	private EntityChast theChast;

	public EntityAIChast(EntityChast entityChast)
	{
		this.theChast = entityChast;
	}

	@Override
	public void startExecuting()
	{
		this.theChast.getNavigator().clearPathEntity();
	}

	@Override
	public void resetTask()
	{
		this.theChast.getNavigator().clearPathEntity();
	}

	// TODO /* ======================================== MOD START =====================================*/

	public EntityChast getAIOwnerEntity()
	{
		return this.theChast;
	}

	public InventoryChast getAIOwnerInventory()
	{
		return this.theChast.getInventoryChast();
	}

	public World getAIWorld()
	{
		return this.theChast.getEntityWorld();
	}

	public BlockPos getAIBlockPos()
	{
		BlockPos blockPos = this.theChast.getPosition();

		if (this.theChast.isModeFollow())
		{
			EntityLivingBase entityLivingBase = this.theChast.getOwnerEntity();

			if (this.theChast.isOwnerTame() && (entityLivingBase != null))
			{
				blockPos = entityLivingBase.getPosition();
			}
		}
		else
		{
			// none
		}

		return blockPos;
	}

	public boolean isRunningBaseAI()
	{
		return this.theChast.isStatePanic() || this.theChast.isStateSit() || this.theChast.isStateTrade();
	}

	public boolean canStartFollowAI()
	{
		return this.theChast.isModeFollow();
	}

	public boolean canStartFreedomAI()
	{
		return !this.theChast.isModeFollow();
	}

}
