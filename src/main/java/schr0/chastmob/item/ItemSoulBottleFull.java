package schr0.chastmob.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import schr0.chastmob.init.ChastMobItems;
import schr0.chastmob.init.ChastMobLang;
import schr0.chastmob.init.ChastMobPacket;
import schr0.chastmob.packet.MessageParticleEntity;

public class ItemSoulBottleFull extends Item
{

	private static String NAME_FRIENDLY = "friendly";
	public static final int MAX_DAMAGE = (20 * 60);

	public ItemSoulBottleFull()
	{
		this.setMaxStackSize(1);
		this.setMaxDamage(MAX_DAMAGE);
		this.setContainerItem(ChastMobItems.SOUL_BOTTLE);

		this.addPropertyOverride(new ResourceLocation(NAME_FRIENDLY), new IItemPropertyGetter()
		{
			@Override
			@SideOnly(Side.CLIENT)
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

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems)
	{
		subItems.add(new ItemStack(itemIn, 1, MAX_DAMAGE));
		subItems.add(new ItemStack(itemIn, 1));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
	{
		if (stack.getItemDamage() == 0)
		{
			tooltip.add(TextFormatting.ITALIC + new TextComponentTranslation(ChastMobLang.ITEM_SOUL_BOTTLE_FULL_FRIENDLY_TIPS, new Object[0]).getFormattedText());
		}
		else
		{
			tooltip.add(TextFormatting.ITALIC + new TextComponentTranslation(ChastMobLang.ITEM_SOUL_BOTTLE_FULL_TIPS, new Object[0]).getFormattedText());
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
			ChastMobPacket.DISPATCHER.sendToAll(new MessageParticleEntity(entityIn, 0));

			entityPlayer.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
		}
	}

	// TODO /* ======================================== MOD START =====================================*/

	public void resurrectionOwner(ItemStack stack, EnumHand hand, EntityLivingBase owner)
	{
		if (stack.getItemDamage() != 0)
		{
			return;
		}

		owner.setHealth(owner.getMaxHealth());

		owner.setHeldItem(hand, new ItemStack(ChastMobItems.SOUL_BOTTLE));

		ChastMobPacket.DISPATCHER.sendToAll(new MessageParticleEntity(owner, 0));

		owner.getEntityWorld().playSound((EntityPlayer) null, owner.getPosition(), SoundEvents.ENTITY_PLAYER_LEVELUP, owner.getSoundCategory(), 1.0F, 1.0F);
	}

}
