package schr0.chastmob.entity.ai;

import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.Vec3d;
import schr0.chastmob.entity.EntityChast;

public class EntityAIChastWander extends EntityAIChast
{

	private static final int CHANCE = (6 * 20);
	private double randPosX;
	private double randPosY;
	private double randPosZ;

	public EntityAIChastWander(EntityChast entityChast)
	{
		super(entityChast);

		this.randPosX = 0;
		this.randPosY = 0;
		this.randPosZ = 0;
	}

	@Override
	public boolean shouldExecute()
	{
		if (this.getRandom().nextInt(CHANCE) == 0)
		{
			this.randPosX = 0;
			this.randPosY = 0;
			this.randPosZ = 0;

			Vec3d vec3d = RandomPositionGenerator.findRandomTarget(this.getEntity(), this.getRange(), this.getRange());

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

		return false;
	}

	@Override
	public boolean shouldContinueExecuting()
	{
		if (this.isTimeOut())
		{
			return false;
		}

		return !this.getEntity().getNavigator().noPath();
	}

	@Override
	public void resetTask()
	{
		super.resetTask();

		this.randPosX = 0;
		this.randPosY = 0;
		this.randPosZ = 0;
	}

	@Override
	public void updateTask()
	{
		super.updateTask();

		this.getEntity().getNavigator().tryMoveToXYZ(this.randPosX, this.randPosY, this.randPosZ, this.getSpeed());
	}

}
