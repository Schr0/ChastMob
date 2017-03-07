package schr0.chastmob.entity.ai;

import net.minecraft.util.IStringSerializable;
import net.minecraft.util.text.TextComponentTranslation;
import schr0.chastmob.init.ChastMobLang;

public enum EnumAIMode implements IStringSerializable
{

	FREEDOM("freedom", 0, new TextComponentTranslation(ChastMobLang.ENTITY_CHAST_AIMODE_FREEDOM, new Object[0]).getFormattedText()),
	FOLLOW("follow", 1, new TextComponentTranslation(ChastMobLang.ENTITY_CHAST_AIMODE_FOLLOW, new Object[0]).getFormattedText()),
	PATROL("patrol", 2, new TextComponentTranslation(ChastMobLang.ENTITY_CHAST_AIMODE_PATROL, new Object[0]).getFormattedText()),
	SUPPLY("supply", 3, new TextComponentTranslation(ChastMobLang.ENTITY_CHAST_AIMODE_SUPPLY, new Object[0]).getFormattedText());

	private static final EnumAIMode[] AIMODE_LOOKUP = new EnumAIMode[values().length];
	private final String name;
	private final int number;
	private final String label;

	static
	{
		for (EnumAIMode enumaimode : values())
		{
			AIMODE_LOOKUP[enumaimode.getNumber()] = enumaimode;
		}
	}

	private EnumAIMode(String name, int number, String label)
	{
		this.number = number;
		this.name = name;
		this.label = label;
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
