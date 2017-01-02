package schr0.chastmob;

import net.minecraft.entity.ai.EntityAIBase;

public abstract class EntityAIChast extends EntityAIBase
{

	private EntityChast aiOwnerChast;

	public EntityAIChast(EntityChast entityChast)
	{
		this.aiOwnerChast = entityChast;
	}

	// TODO /* ======================================== MOD START =====================================*/

	public EntityChast getAIOwnerChast()
	{
		return this.aiOwnerChast;
	}

}
