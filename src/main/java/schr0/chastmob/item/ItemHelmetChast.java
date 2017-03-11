package schr0.chastmob.item;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public abstract class ItemHelmetChast extends Item
{

	public ItemHelmetChast(ItemArmor.ArmorMaterial material)
	{
		this.setMaxDamage(material.getDurability(EntityEquipmentSlot.HEAD));
		this.setMaxStackSize(1);
	}

	public abstract ResourceLocation getTexture(ItemStack stack);

}
