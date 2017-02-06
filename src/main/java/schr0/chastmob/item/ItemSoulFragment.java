package schr0.chastmob.item;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class ItemSoulFragment extends Item
{

	public ItemSoulFragment()
	{
		// none
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

	public static void healLivingBase(ItemStack stack, World world, EntityLivingBase target, @Nullable EntityPlayer player)
	{
		target.heal(2.0F);

		for (int i = 0; i < 4; ++i)
		{
			double randX = world.rand.nextGaussian() * 0.02D;
			double randY = world.rand.nextGaussian() * 0.02D;
			double randZ = world.rand.nextGaussian() * 0.02D;
			world.spawnParticle(EnumParticleTypes.HEART, target.posX + (double) (world.rand.nextFloat() * target.width * 2.0F) - (double) target.width, target.posY + 0.5D + (double) (world.rand.nextFloat() * target.height), target.posZ + (double) (world.rand.nextFloat() * target.width * 2.0F) - (double) target.width, randX, randY, randZ, new int[0]);
		}

		target.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 1.0F, 1.0F);

		if (player != null)
		{
			player.getCooldownTracker().setCooldown(stack.getItem(), 20);

			if (!player.capabilities.isCreativeMode)
			{
				--stack.stackSize;
			}
		}
	}

}
