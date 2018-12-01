package schr0.chastmob.entity.ai;

import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.Vec3d;
import schr0.chastmob.entity.EntityChast;

public class EntityAIChastWander extends EntityAIChast
{

	private static final int CHANCE_WONDER = 120;

	private double speed;
	private int distance;
	private double randPosX;
	private double randPosY;
	private double randPosZ;

	public EntityAIChastWander(EntityChast entityChast, double speed, int distance)
	{
		super(entityChast);
		this.speed = speed;
		this.distance = distance;
		this.randPosX = 0;
		this.randPosY = 0;
		this.randPosZ = 0;
	}

	@Override
	public boolean shouldExecute()
	{

		if (this.getEntity().getRNG().nextInt(CHANCE_WONDER) == 0)
		{
			Vec3d vec3d = RandomPositionGenerator.findRandomTarget(this.getEntity(), this.distance, this.distance);

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
		return !this.getEntity().getNavigator().noPath();
	}

	@Override
	public void startExecuting()
	{
		super.startExecuting();

		this.getEntity().getNavigator().tryMoveToXYZ(this.randPosX, this.randPosY, this.randPosZ, this.speed);
	}

	@Override
	public void resetTask()
	{
		super.resetTask();

		this.randPosX = 0;
		this.randPosY = 0;
		this.randPosZ = 0;
	}

}
