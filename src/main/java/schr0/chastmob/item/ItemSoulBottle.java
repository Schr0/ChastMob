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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import schr0.chastmob.init.ChastMobItems;

public class ItemSoulBottle extends Item
{

	public ItemSoulBottle()
	{
		this.setMaxStackSize(16);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{
		TextComponentTranslation info = new TextComponentTranslation("item.soul_bottle.tooltip", new Object[0]);

		info.getStyle().setColor(TextFormatting.BLUE);
		info.getStyle().setItalic(true);

		tooltip.add(info.getFormattedText());
	}

	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand)
	{
		ItemStack stack = player.getHeldItem(hand);

		if (world.getBlockState(pos).getBlock() == Blocks.SOUL_SAND)
		{
			world.destroyBlock(pos, false);

			Block.spawnAsEntity(world, pos, new ItemStack(Blocks.SAND));

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

		return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
	}

}
