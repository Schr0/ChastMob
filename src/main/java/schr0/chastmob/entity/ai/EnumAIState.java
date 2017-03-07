package schr0.chastmob.entity.ai;

import net.minecraft.util.IStringSerializable;

public enum EnumAIState implements IStringSerializable
{

	FREEDOM("freedom", 0),
	FOLLOW("follow", 1);

	private static final EnumAIState[] AISTATE_LOOKUP = new EnumAIState[values().length];
	private final String name;
	private final int number;

	static
	{
		for (EnumAIState enumaistate : values())
		{
			AISTATE_LOOKUP[enumaistate.getNumber()] = enumaistate;
		}
	}

	private EnumAIState(String name, int number)
	{
		this.number = number;
		this.name = name;
	}

	@Override
	public String getName()
	{
		return this.name;
	}

	public int getNumber()
	{
		return this.number;
	}

	public static EnumAIState byNumber(int number)
	{
		if (number < 0 || AISTATE_LOOKUP.length <= number)
		{
			number = 0;
		}

		return AISTATE_LOOKUP[number];
	}

}
