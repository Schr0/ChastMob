package schr0.chastmob;

import net.minecraft.entity.ai.EntityAIBase;

public abstract class EntityAIChast extends EntityAIBase
{

	private EntityChast entityChast;

	public EntityAIChast(EntityChast entityChast)
	{
		this.entityChast = entityChast;
	}

	// TODO /* ======================================== MOD START =====================================*/

	public EntityChast getAIChastEntity()
	{
		return this.entityChast;
	}

}
