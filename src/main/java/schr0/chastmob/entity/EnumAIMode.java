package schr0.chastmob.entity;

import net.minecraft.util.text.TextComponentTranslation;
import schr0.chastmob.init.ChastMobLang;

public enum EnumAIMode
{

	FREEDOM(0, new TextComponentTranslation(ChastMobLang.ENTITY_CHAST_AIMODE_FREEDOM, new Object[0]).getFormattedText()),
	FOLLOW(1, new TextComponentTranslation(ChastMobLang.ENTITY_CHAST_AIMODE_FOLLOW, new Object[0]).getFormattedText()),
	PATROL(2, new TextComponentTranslation(ChastMobLang.ENTITY_CHAST_AIMODE_PATROL, new Object[0]).getFormattedText()),
	SUPPLY(3, new TextComponentTranslation(ChastMobLang.ENTITY_CHAST_AIMODE_SUPPLY, new Object[0]).getFormattedText());

	private static final EnumAIMode[] AIMODE_LOOKUP = new EnumAIMode[values().length];
	private final int number;
	private final String label;

	static
	{
		for (EnumAIMode enumaimode : values())
		{
			AIMODE_LOOKUP[enumaimode.getNumber()] = enumaimode;
		}
	}

	private EnumAIMode(int number, String label)
	{
		this.number = number;
		this.label = label;
	}

	public int getNumber()
	{
		return this.number;
	}

	public String getLabel()
	{
		return this.label;
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
