package schr0.chastmob.entity.ai;

public enum ChastAIState
{

	FREEDOM(0),
	FOLLOW(1);

	private static final ChastAIState[] AISTATE_LOOKUP = new ChastAIState[values().length];
	private final int number;

	static
	{
		for (ChastAIState enumaistate : values())
		{
			AISTATE_LOOKUP[enumaistate.getNumber()] = enumaistate;
		}
	}

	private ChastAIState(int number)
	{
		this.number = number;
	}

	public int getNumber()
	{
		return this.number;
	}

	public static ChastAIState byNumber(int number)
	{
		if (number < 0 || AISTATE_LOOKUP.length <= number)
		{
			number = 0;
		}

		return AISTATE_LOOKUP[number];
	}

}
