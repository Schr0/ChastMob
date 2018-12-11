package schr0.chastmob.entity.ai;

import java.util.List;

import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import schr0.chastmob.entity.ChastMode;
import schr0.chastmob.entity.EntityChast;

public class EntityAIChastGoHome extends EntityAIChast
{

	private TileEntityChest targetTEChest;

	public EntityAIChastGoHome(EntityChast entityChast)
	{
		super(entityChast);

		this.targetTEChest = null;
	}

	@Override
	public boolean shouldExecute()
	{
		if (this.getMode() == ChastMode.PATROL)
		{
			this.targetTEChest = null;

			TileEntityChest homeChest = this.getEntity().getCanSeeHomeChest(false);

			if (homeChest != null)
			{
				for (EntityChast ownerChast : this.getAroundEntityChast())
				{
					if (ownerChast.equals(this.getEntity()))
					{
						return false;
					}
				}

				this.targetTEChest = homeChest;

				return true;
			}
		}

		return false;
	}

	@Override
	public boolean shouldContinueExecuting()
	{
		if (this.isTimeOut())
		{
			return false;
		}

		return (this.targetTEChest != null);
	}

	@Override
	public void resetTask()
	{
		super.resetTask();

		if (this.targetTEChest != null)
		{
			this.forceMoveToTargetBlockPos(this.targetTEChest.getPos());
		}

		this.targetTEChest = null;
	}

	@Override
	public void updateTask()
	{
		super.updateTask();

		BlockPos targetPos = this.targetTEChest.getPos();
		double minRange = (this.getRange() * this.getRange());

		this.getEntity().getLookHelper().setLookPosition(targetPos.getX(), targetPos.getY(), targetPos.getZ(), 10.0F, this.getEntity().getVerticalFaceSpeed());

		if (this.getEntity().getDistanceSqToCenter(targetPos) < minRange)
		{
			this.targetTEChest = null;
		}
		else
		{
			this.getEntity().getNavigator().tryMoveToXYZ(targetPos.getX(), targetPos.getY(), targetPos.getZ(), this.getSpeed());
		}
	}

	// TODO /* ======================================== MOD START =====================================*/

	private List<EntityChast> getAroundEntityChast()
	{
		BlockPos pos = this.getEntity().getCenterPosition();

		return this.getWorld().getEntitiesWithinAABB(EntityChast.class, new AxisAlignedBB(pos).grow(this.getRange(), this.getRange(), this.getRange()));
	}

}
