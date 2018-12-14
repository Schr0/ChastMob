package schr0.chastmob.entity.ai;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import schr0.chastmob.entity.ChastMode;
import schr0.chastmob.entity.EntityChast;

public class EntityAIChastGoHome extends EntityAIChast
{

	private TileEntityChest targetHomeChest;

	public EntityAIChastGoHome(EntityChast entityChast)
	{
		super(entityChast);

		this.targetHomeChest = null;
	}

	@Override
	public boolean shouldExecute()
	{
		if (this.getMode() == ChastMode.PATROL)
		{
			this.targetHomeChest = this.getHomeChest();

			if (this.targetHomeChest != null)
			{
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

		return (this.targetHomeChest != null);
	}

	@Override
	public void resetTask()
	{
		super.resetTask();

		if (this.targetHomeChest != null)
		{
			this.forceMoveToTargetBlockPos(this.targetHomeChest.getPos());
		}

		this.targetHomeChest = null;
	}

	@Override
	public void updateTask()
	{
		super.updateTask();

		BlockPos targetPos = this.targetHomeChest.getPos();

		this.getEntity().getLookHelper().setLookPosition(targetPos.getX(), targetPos.getY(), targetPos.getZ(), 10.0F, this.getEntity().getVerticalFaceSpeed());

		if (this.getEntity().getDistanceSqToCenter(targetPos) < this.getGoHomeRange())
		{
			this.targetHomeChest = null;
		}
		else
		{
			this.getEntity().getNavigator().tryMoveToXYZ(targetPos.getX(), targetPos.getY(), targetPos.getZ(), this.getSpeed());
		}
	}

	// TODO /* ======================================== MOD START =====================================*/

	private int getGoHomeRange()
	{
		return (this.getRange() * this.getRange());
	}

	@Nullable
	private TileEntityChest getHomeChest()
	{
		for (EntityChast ownerChast : this.getAroundEntityChasts())
		{
			if (ownerChast.equals(this.getEntity()))
			{
				return (TileEntityChest) null;
			}
		}

		return this.getEntity().getCanSeeHomeChest(false);
	}

	private List<EntityChast> getAroundEntityChasts()
	{
		BlockPos pos = this.getEntity().getCenterPosition();

		return this.getWorld().getEntitiesWithinAABB(EntityChast.class, new AxisAlignedBB(pos).grow(this.getRange(), this.getRange(), this.getRange()));
	}

}
