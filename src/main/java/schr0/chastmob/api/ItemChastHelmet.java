package schr0.chastmob.api;

import com.google.common.collect.Multimap;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import schr0.chastmob.entity.EntityChast;

public abstract class ItemChastHelmet extends Item
{

	private final int damageReduceAmount;
	private final int enchantability;

	public ItemChastHelmet(ItemChastHelmet.ChastHelmetMaterial material)
	{
		this.setMaxStackSize(1);
		this.setMaxDamage(material.getMaxUses());
		this.damageReduceAmount = material.getDamageReduceAmount();
		this.enchantability = material.getEnchantability();
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack)
	{
		Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);

		if (slot == EntityEquipmentSlot.HEAD)
		{
			multimap.put(SharedMonsterAttributes.ARMOR.getName(), new AttributeModifier("Helmet modifier", (double) this.damageReduceAmount, 0));
		}

		return multimap;
	}

	@Override
	public int getItemEnchantability()
	{
		return this.enchantability;
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment)
	{
		if (enchantment.type == EnumEnchantmentType.ARMOR_HEAD)
		{
			return true;
		}

		return super.canApplyAtEnchantingTable(stack, enchantment);
	}

	// TODO /* ======================================== MOD START =====================================*/

	public static enum ChastHelmetMaterial
	{

		WOOD(59, 1, 15),
		STONE(131, 2, 5),
		IRON(250, 3, 14),
		DIAMOND(1561, 4, 10),
		GOLD(32, 1, 22);

		private final int maxUses;
		private final int damageReduceAmount;
		private final int enchantability;

		private ChastHelmetMaterial(int maxUses, int damageReduceAmount, int enchantability)
		{
			this.maxUses = maxUses;
			this.damageReduceAmount = damageReduceAmount;
			this.enchantability = enchantability;
		}

		public int getMaxUses()
		{
			return this.maxUses;
		}

		public int getDamageReduceAmount()
		{
			return this.damageReduceAmount;
		}

		public int getEnchantability()
		{
			return this.enchantability;
		}

	}

	@SideOnly(Side.CLIENT)
	public abstract ResourceLocation getHelmetTexture(ItemStack stack);

	public boolean onDmageOwner(DamageSource source, float damage, ItemStack stack, EntityChast owner)
	{
		stack.damageItem(Math.max(1, (int) (damage / 4)), owner);

		return true;
	}

	public void onUpdateOwner(ItemStack stack, EntityChast owner)
	{
		// none
	}

}
