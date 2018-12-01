package schr0.chastmob.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import schr0.chastmob.init.ChastMobPackets;
import schr0.chastmob.packet.MessageParticleEntity;

public class ItemSoulFragment extends Item
{

	public ItemSoulFragment()
	{
		// none
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{
		TextComponentTranslation info = new TextComponentTranslation("item.soul_fragment.tooltip", new Object[0]);

		info.getStyle().setColor(TextFormatting.BLUE);
		info.getStyle().setItalic(true);

		tooltip.add(info.getFormattedText());
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
	{
		ItemStack stack = playerIn.getHeldItem(handIn);

		if (playerIn.shouldHeal())
		{
			this.healLivingBase(stack, worldIn, playerIn, playerIn);

			return new ActionResult(EnumActionResult.SUCCESS, stack);
		}

		return super.onItemRightClick(worldIn, playerIn, handIn);
	}

	// TODO /* ======================================== MOD START =====================================*/

	public void healLivingBase(ItemStack stack, World world, EntityLivingBase target, EntityPlayer player)
	{
		target.heal(2.0F);

		player.getCooldownTracker().setCooldown(stack.getItem(), 10);

		if (!player.capabilities.isCreativeMode)
		{
			stack.shrink(1);
		}

		ChastMobPackets.DISPATCHER.sendToAll(new MessageParticleEntity(target, 0));

		target.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 1.0F, 1.0F);
	}

}
