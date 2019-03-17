package schr0.chastmob.entity.ai;

import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.Vec3d;
import schr0.chastmob.entity.ChastMode;
import schr0.chastmob.entity.EntityChast;

public class EntityAIChastWander extends EntityAIChast
{

	private static final int CHANCE = 20;
	private double randPosX;
	private double randPosY;
	private double randPosZ;

	public EntityAIChastWander(EntityChast entityChast)
	{
		super(entityChast);
	}

	@Override
	public boolean shouldExecute()
	{
		if ((this.getEntity().getMode() == ChastMode.FREEDOM) || (this.getEntity().getMode() == ChastMode.PATROL))
		{
			this.randPosX = 0;
			this.randPosY = 0;
			this.randPosZ = 0;

			if (this.getEntity().getNavigator().noPath() && (this.getRandom().nextInt(CHANCE) == 0))
			{
				Vec3d vec3d = RandomPositionGenerator.findRandomTarget(this.getEntity(), this.getSearchRange(), this.getSearchRange());

				if (vec3d == null)
				{
					return false;
				}
				else
				{
					this.randPosX = vec3d.x;
					this.randPosY = vec3d.y;
					this.randPosZ = vec3d.z;

					return true;
				}
			}
		}

		return false;
	}

	@Override
	public boolean shouldContinueExecuting()
	{
		return !this.getEntity().getNavigator().noPath();
	}

	@Override
	public void updateTask()
	{
		super.updateTask();

		this.getEntity().getNavigator().tryMoveToXYZ(this.randPosX, this.randPosY, this.randPosZ, this.getMoveSpeed());
	}

}
