package schr0.chastmob.entity;

import net.minecraft.util.text.TextComponentTranslation;
import schr0.chastmob.init.ChastMobLang;

public enum EnumAIMode
{

	FREEDOM(new TextComponentTranslation(ChastMobLang.ENTITY_CHAST_AIMODE_FREEDOM, new Object[0]).getFormattedText()),
	FOLLOW(new TextComponentTranslation(ChastMobLang.ENTITY_CHAST_AIMODE_FOLLOW, new Object[0]).getFormattedText()),
	PATROL(new TextComponentTranslation(ChastMobLang.ENTITY_CHAST_AIMODE_PATROL, new Object[0]).getFormattedText()),
	SUPPLY(new TextComponentTranslation(ChastMobLang.ENTITY_CHAST_AIMODE_SUPPLY, new Object[0]).getFormattedText());

	private final String label;

	private EnumAIMode(String label)
	{
		this.label = label;
	}

	public String getLabel()
	{
		return this.label;
	}

}
