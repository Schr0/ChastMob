package schr0.chastmob.item;

import java.util.List;

import net.minecraft.block.Block;
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

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
	{
		tooltip.add(TextFormatting.ITALIC + new TextComponentTranslation("item.soul_bottle.tips", new Object[0]).getFormattedText());
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if (worldIn.getBlockState(pos).getBlock().equals(Blocks.SOUL_SAND))
		{
			worldIn.destroyBlock(pos, false);

			Block.spawnAsEntity(worldIn, pos, new ItemStack(Blocks.SAND));

			ItemStack stackSoulBottleFull = new ItemStack(ChastMobItems.SOUL_BOTTLE_FULL, 1, ItemSoulBottleFull.MAX_DAMAGE);

			if (!playerIn.inventory.addItemStackToInventory(stackSoulBottleFull))
			{
				playerIn.dropItem(stackSoulBottleFull, false);
			}

			if (!playerIn.capabilities.isCreativeMode)
			{
				--stack.stackSize;
			}

			playerIn.playSound(SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, 0.5F, 1.0F);

			return EnumActionResult.SUCCESS;
		}

		return super.onItemUse(stack, playerIn, worldIn, pos, hand, facing, hitX, hitY, hitZ);
	}

}
