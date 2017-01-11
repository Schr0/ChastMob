package schr0.chastmob;

import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.Vec3d;

public class EntityAIChastWander extends EntityAIChast
{

	private static final int CHANCE_WONDER = (5 * 20);

	private double moveSpeed;
	private int maxDistance;
	private int timeCounter;
	private double randPosX;
	private double randPosY;
	private double randPosZ;

	public EntityAIChastWander(EntityChast entityChast, double speed, int distance)
	{
		super(entityChast);
		this.setMutexBits(1);

		this.moveSpeed = speed;
		this.maxDistance = distance;
	}

	@Override
	public boolean shouldExecute()
	{
		if (this.isWandering())
		{
			Vec3d vec3d = RandomPositionGenerator.findRandomTarget(this.getAIOwnerEntity(), this.maxDistance, this.maxDistance);

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
		return !this.getAIOwnerEntity().getNavigator().noPath();
	}

	@Override
	public void startExecuting()
	{
		super.startExecuting();

		this.getAIOwnerEntity().getNavigator().tryMoveToXYZ(this.randPosX, this.randPosY, this.randPosZ, this.moveSpeed);
	}

	// TODO /* ======================================== MOD START =====================================*/

	public boolean isWandering()
	{
		return (this.getAIOwnerEntity().getRNG().nextInt(CHANCE_WONDER) == 0);
	}

}
