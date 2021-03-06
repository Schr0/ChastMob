package schr0.chastmob.entity;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;

public enum ChastMode
{

	FREEDOM(new TextComponentTranslation("entity.chast.mode.freedom", new Object[0]).getFormattedText(), new ItemStack(Items.FEATHER)),
	FOLLOW(new TextComponentTranslation("entity.chast.mode.follow", new Object[0]).getFormattedText(), new ItemStack(Items.SKULL, 1, 3)),
	PATROL(new TextComponentTranslation("entity.chast.mode.patrol", new Object[0]).getFormattedText(), new ItemStack(Items.LEATHER_BOOTS));

	private final String label;
	private final ItemStack stack;

	private ChastMode(String label, ItemStack stack)
	{
		this.label = label;
		this.stack = stack;
	}

	public String getLabel()
	{
		return this.label;
	}

	public ItemStack getIconItem()
	{
		return this.stack;
	}

}
