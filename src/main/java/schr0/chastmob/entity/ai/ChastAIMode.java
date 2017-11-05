package schr0.chastmob.entity.ai;

import net.minecraft.util.text.TextComponentTranslation;
import schr0.chastmob.util.ChastMobLangs;

public enum ChastAIMode
{

	FREEDOM(new TextComponentTranslation(ChastMobLangs.ENTITY_CHAST_AIMODE_FREEDOM, new Object[0]).getFormattedText()),
	FOLLOW(new TextComponentTranslation(ChastMobLangs.ENTITY_CHAST_AIMODE_FOLLOW, new Object[0]).getFormattedText()),
	PATROL(new TextComponentTranslation(ChastMobLangs.ENTITY_CHAST_AIMODE_PATROL, new Object[0]).getFormattedText()),
	SUPPLY(new TextComponentTranslation(ChastMobLangs.ENTITY_CHAST_AIMODE_SUPPLY, new Object[0]).getFormattedText());

	private final String label;

	private ChastAIMode(String label)
	{
		this.label = label;
	}

	public String getLabel()
	{
		return this.label;
	}

}
