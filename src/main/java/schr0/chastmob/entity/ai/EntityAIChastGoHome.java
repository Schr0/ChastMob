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

	public EntityAIChastGoHome(EntityChast entityChast)
	{
		super(entityChast);
	}

	@Override
	public boolean shouldExecute()
	{
		if (this.getEntity().getMode() == ChastMode.PATROL)
		{
			for (EntityChast ownerChast : this.getAroundEntityChasts())
			{
				if (ownerChast.equals(this.getEntity()))
				{
					return false;
				}
			}

			if (this.getEntity().getCanSeeHomeChest(false) != null)
			{
				return true;
			}
		}

		return false;
	}

	@Override
	public void updateTask()
	{
		super.updateTask();

		TileEntityChest homeChest = this.getEntity().getCanSeeHomeChest(false);

		if (homeChest == null)
		{
			return;
		}

		BlockPos targetPos = homeChest.getPos();

		this.getEntity().getLookHelper().setLookPosition(targetPos.getX(), targetPos.getY(), targetPos.getZ(), 10.0F, this.getEntity().getVerticalFaceSpeed());

		if (this.getGoHomeRange() < this.getEntity().getDistanceSqToCenter(targetPos))
		{
			if (this.isTimeOut())
			{
				if (this.getEntity().canBlockBeSeen(targetPos))
				{
					this.forceMoveToTargetBlockPos(targetPos);
				}
			}

			this.getEntity().getNavigator().tryMoveToXYZ(targetPos.getX(), targetPos.getY(), targetPos.getZ(), this.getMoveSpeed());
		}
	}

	// TODO /* ======================================== MOD START =====================================*/

	private int getGoHomeRange()
	{
		return (this.getSearchRange() * this.getSearchRange());
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

		return this.getWorld().getEntitiesWithinAABB(EntityChast.class, new AxisAlignedBB(pos).grow(this.getSearchRange(), this.getSearchRange(), this.getSearchRange()));
	}

}
