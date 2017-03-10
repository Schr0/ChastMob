package schr0.chastmob.entity.ai;

import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.Vec3d;
import schr0.chastmob.entity.EntityChast;

public class EntityAIChastWander extends EntityAIChast
{

	private static final int CHANCE_WONDER = 100;

	private double speed;
	private int distance;
	private double randPosX;
	private double randPosY;
	private double randPosZ;

	public EntityAIChastWander(EntityChast entityChast, double speed, int distance)
	{
		super(entityChast);
		this.setMutexBits(1);

		this.speed = speed;
		this.distance = distance;
	}

	@Override
	public boolean shouldExecute()
	{
		if (this.isWandering())
		{
			Vec3d vec3d = RandomPositionGenerator.findRandomTarget(this.getOwnerEntity(), this.distance, this.distance);

			if (vec3d == null)
			{
				return false;
			}
			else
			{
				this.randPosX = vec3d.xCoord;
				this.randPosY = vec3d.yCoord;
				this.randPosZ = vec3d.zCoord;

				return true;
			}
		}

		return false;
	}

	@Override
	public boolean continueExecuting()
	{
		return !this.getOwnerEntity().getNavigator().noPath();
	}

	@Override
	public void startExecuting()
	{
		super.startExecuting();

		this.getOwnerEntity().getNavigator().tryMoveToXYZ(this.randPosX, this.randPosY, this.randPosZ, this.speed);
	}

	// TODO /* ======================================== MOD START =====================================*/

	public boolean isWandering()
	{
		return (this.getOwnerEntity().getRNG().nextInt(CHANCE_WONDER) == 0);
	}

}
