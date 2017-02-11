package schr0.chastmob.item;

import java.util.List;

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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import schr0.chastmob.init.ChastMobPacket;
import schr0.chastmob.packet.MessageParticleEntity;

public class ItemSoulFragment extends Item
{

	public ItemSoulFragment()
	{
		// none
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
	{
		tooltip.add(TextFormatting.ITALIC + new TextComponentTranslation("item.soul_fragment.tips", new Object[0]).getFormattedText());
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand)
	{
		if (playerIn.shouldHeal())
		{
			healLivingBase(itemStackIn, worldIn, playerIn, playerIn);

			return new ActionResult(EnumActionResult.SUCCESS, itemStackIn);
		}

		return super.onItemRightClick(itemStackIn, worldIn, playerIn, hand);
	}

	// TODO /* ======================================== MOD START =====================================*/

	public static void healLivingBase(ItemStack stack, World world, EntityLivingBase target, EntityPlayer player)
	{
		target.heal(2.0F);

		player.getCooldownTracker().setCooldown(stack.getItem(), 10);

		if (!player.capabilities.isCreativeMode)
		{
			--stack.stackSize;
		}

		ChastMobPacket.DISPATCHER.sendToAll(new MessageParticleEntity(target, 0));

		target.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 1.0F, 1.0F);
	}

}
