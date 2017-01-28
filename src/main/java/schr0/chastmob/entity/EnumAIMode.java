package schr0.chastmob.entity;

import net.minecraft.util.IStringSerializable;

public enum EnumAIMode implements IStringSerializable
{
	FREEDOM("freedom", 0),
	FOLLOW("follow", 1);

	private static final EnumAIMode[] AIMODE_LOOKUP = new EnumAIMode[values().length];
	private final String name;
	private final int number;

	static
	{
		for (EnumAIMode enumaimode : values())
		{
			AIMODE_LOOKUP[enumaimode.getNumber()] = enumaimode;
		}
	}

	private EnumAIMode(String name, int number)
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

	public static EnumAIMode byNumber(int number)
	{
		if (number < 0 || AIMODE_LOOKUP.length <= number)
		{
			number = 0;
		}

		return AIMODE_LOOKUP[number];
	}

}
