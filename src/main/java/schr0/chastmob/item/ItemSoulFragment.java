package schr0.chastmob.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import schr0.chastmob.entity.EntityChast;
import schr0.chastmob.util.ChastMobParticles;

public class ItemSoulFragment extends Item
{

	public ItemSoulFragment()
	{
		this.setMaxStackSize(16);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{
		TextComponentTranslation info = new TextComponentTranslation("item.soul_fragment.tooltip", new Object[0]);

		info.getStyle().setColor(TextFormatting.BLUE);
		info.getStyle().setItalic(true);

		tooltip.add(info.getFormattedText());
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand)
	{
		if (target instanceof EntityChast)
		{
			if (this.shouldHeal(target))
			{
				this.heal(stack, playerIn.getEntityWorld(), target, playerIn);

				return true;
			}
		}

		return false;
	}

	// TODO /* ======================================== MOD START =====================================*/

	public boolean shouldHeal(EntityLivingBase target)
	{
		if ((0.0F < target.getHealth()) && (target.getHealth() < target.getMaxHealth()))
		{
			return true;
		}

		return false;
	}

	public void heal(ItemStack stack, World world, EntityLivingBase target, EntityLivingBase owner)
	{
		if (owner instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer) owner;

			player.getCooldownTracker().setCooldown(stack.getItem(), 10);

			if (!player.capabilities.isCreativeMode)
			{
				stack.shrink(1);
			}
		}
		else
		{
			stack.shrink(1);
		}

		target.heal(2.0F);

		ChastMobParticles.spawnParticleHeart(target);

		target.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 1.0F, 1.0F);
	}

}
