package schr0.chastmob.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import schr0.chastmob.init.ChastMobItems;
import schr0.chastmob.util.ChastMobLangs;

public class ItemSoulBottle extends Item
{

	public ItemSoulBottle()
	{
		this.setMaxStackSize(16);
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{
		tooltip.add(TextFormatting.ITALIC + new TextComponentTranslation(ChastMobLangs.ITEM_SOUL_BOTTLE_TIPS, new Object[0]).getFormattedText());
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		ItemStack stack = player.getHeldItem(hand);

		if (worldIn.getBlockState(pos).getBlock() == Blocks.SOUL_SAND)
		{
			worldIn.destroyBlock(pos, false);

			Block.spawnAsEntity(worldIn, pos, new ItemStack(Blocks.SAND));

			ItemStack stackSoulBottleFull = new ItemStack(ChastMobItems.SOUL_BOTTLE_FULL, 1, ItemSoulBottleFull.MAX_DAMAGE);

			if (!player.inventory.addItemStackToInventory(stackSoulBottleFull))
			{
				player.dropItem(stackSoulBottleFull, false);
			}

			if (!player.capabilities.isCreativeMode)
			{
				stack.shrink(1);
			}

			player.playSound(SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, 0.5F, 1.0F);

			return EnumActionResult.SUCCESS;
		}

		return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
	}

}
