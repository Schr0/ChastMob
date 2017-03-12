package schr0.chastmob.entity;

public enum EnumAIState
{

	FREEDOM(0),
	FOLLOW(1);

	private static final EnumAIState[] AISTATE_LOOKUP = new EnumAIState[values().length];
	private final int number;

	static
	{
		for (EnumAIState enumaistate : values())
		{
			AISTATE_LOOKUP[enumaistate.getNumber()] = enumaistate;
		}
	}

	private EnumAIState(int number)
	{
		this.number = number;
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
