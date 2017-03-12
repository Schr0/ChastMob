package schr0.chastmob.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public abstract class ItemHelmetChast extends Item
{

	public ItemHelmetChast(Item.ToolMaterial material)
	{
		this.setMaxDamage(material.getMaxUses());
		this.setMaxStackSize(1);
	}

	public abstract ResourceLocation getTexture(ItemStack stack);

}
