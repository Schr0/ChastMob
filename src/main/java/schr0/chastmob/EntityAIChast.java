package schr0.chastmob;

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

	// TODO /* ======================================== MOD START =====================================*/

	public EntityChast getAIOwnerEntity()
	{
		return this.theChast;
	}

	public World getAIOwnerWorld()
	{
		return this.theChast.getEntityWorld();
	}

	public BlockPos getAIOwnerBlockPos()
	{
		return (new BlockPos(this.theChast));
	}

}
