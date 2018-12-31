package schr0.chastmob.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import schr0.chastmob.init.ChastMobItems;
import schr0.chastmob.util.ChastMobParticles;

public class ItemSoulBottleFull extends Item
{

	public static final int MAX_DAMAGE = (60 * 20);
	private static String NAME_FRIENDLY = "friendly";

	public ItemSoulBottleFull()
	{
		this.setMaxStackSize(1);
		this.setMaxDamage(MAX_DAMAGE);
		this.setContainerItem(ChastMobItems.SOUL_BOTTLE);

		this.addPropertyOverride(new ResourceLocation(NAME_FRIENDLY), new IItemPropertyGetter()
		{
			@SideOnly(Side.CLIENT)
			@Override
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn)
			{
				if (stack.getItemDamage() == 0)
				{
					return 1.0F;
				}

				return 0.0F;
			}
		});
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{
		TextComponentTranslation info;

		if (stack.getItemDamage() == 0)
		{
			info = new TextComponentTranslation("item.soul_bottle_full_friendly.tooltip", new Object[0]);
		}
		else
		{
			info = new TextComponentTranslation("item.soul_bottle_full.tooltip", new Object[0]);
		}

		info.getStyle().setColor(TextFormatting.BLUE);
		info.getStyle().setItalic(true);

		tooltip.add(info.getFormattedText());
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
	{
		if (this.isInCreativeTab(tab))
		{
			items.add(new ItemStack(this, 1));
			items.add(new ItemStack(this, 1, MAX_DAMAGE));
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack)
	{
		String nameOriginal = super.getUnlocalizedName();

		if (stack.getItemDamage() == 0)
		{
			return nameOriginal + "_" + NAME_FRIENDLY;
		}

		return nameOriginal;
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
	{
		super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);

		if (!(entityIn instanceof EntityPlayer) || (stack.getItemDamage() == 0))
		{
			return;
		}

		EntityPlayer entityPlayer = (EntityPlayer) entityIn;

		if (entityPlayer.ticksExisted % 20 == 0)
		{
			int healAmount = 1;// 20min

			if (itemSlot < entityPlayer.inventory.getHotbarSize())
			{
				healAmount = 2;// 10min
			}

			if (isSelected)
			{
				healAmount = 4;// 5min
			}

			if (entityPlayer.isPlayerSleeping())
			{
				healAmount = MAX_DAMAGE;
			}

			stack.setItemDamage(Math.max(0, (stack.getItemDamage() - healAmount)));
		}

		if (stack.getItemDamage() == 0)
		{
			ChastMobParticles.spawnParticleHeart(entityIn);

			entityPlayer.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
		}
	}

	// TODO /* ======================================== MOD START =====================================*/

	public void resurrection(ItemStack stack, EnumHand hand, EntityLivingBase owner)
	{
		if (stack.getItemDamage() != 0)
		{
			return;
		}

		owner.setHealth(owner.getMaxHealth());
		owner.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 400, 1));
		owner.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 6000, 0));
		owner.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 6000, 0));
		owner.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 2400, 3));

		owner.setHeldItem(hand, new ItemStack(ChastMobItems.SOUL_BOTTLE));

		if (owner instanceof EntityPlayerMP)
		{
			((EntityPlayerMP) owner).sendMessage(new TextComponentTranslation("item.soul_bottle_full_friendly.resurrection", new Object[]{}));
		}

		ChastMobParticles.spawnParticleHeart(owner);

		owner.getEntityWorld().playSound((EntityPlayer) null, owner.getPosition(), SoundEvents.ENTITY_PLAYER_LEVELUP, owner.getSoundCategory(), 1.0F, 1.0F);
	}

}
