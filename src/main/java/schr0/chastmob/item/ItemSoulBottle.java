package schr0.chastmob.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import schr0.chastmob.init.ChastMobItems;

public class ItemSoulBottle extends Item
{

	public ItemSoulBottle()
	{
		this.setMaxStackSize(16);
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if (worldIn.getBlockState(pos).getBlock().equals(Blocks.SOUL_SAND))
		{
			worldIn.destroyBlock(pos, false);

			ItemStack stackSoulBottleFull = new ItemStack(ChastMobItems.SOUL_BOTTLE_FULL, 1, ItemSoulBottleFull.MAX_DAMAGE);

			if (!playerIn.inventory.addItemStackToInventory(stackSoulBottleFull))
			{
				playerIn.dropItem(stackSoulBottleFull, false);
			}

			if (!playerIn.capabilities.isCreativeMode)
			{
				--stack.stackSize;
			}

			return EnumActionResult.SUCCESS;
		}

		return super.onItemUse(stack, playerIn, worldIn, pos, hand, facing, hitX, hitY, hitZ);
	}

}
