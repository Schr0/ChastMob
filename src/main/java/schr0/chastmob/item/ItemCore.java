package schr0.chastmob.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import schr0.chastmob.entity.EntityChast;

public class ItemCore extends Item
{

	public ItemCore()
	{
		this.setMaxStackSize(1);
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{
		TextComponentTranslation info = new TextComponentTranslation("item.core.tooltip", new Object[0]);

		info.getStyle().setColor(TextFormatting.BLUE);
		info.getStyle().setItalic(true);

		tooltip.add(info.getFormattedText());
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		ItemStack stack = player.getHeldItem(hand);
		TileEntity tileEntity = worldIn.getTileEntity(pos);

		if ((worldIn.getBlockState(pos).getBlock() == Blocks.CHEST) && (tileEntity instanceof TileEntityChest))
		{
			EntityChast entityChast = new EntityChast(worldIn);
			IInventory inventoryTileChest = (IInventory) tileEntity;

			entityChast.setPositionAndRotation(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0.0F, 0.0F);

			if (stack.hasDisplayName())
			{
				entityChast.setCustomNameTag(stack.getDisplayName());
				entityChast.enablePersistence();
			}

			entityChast.onSpawnByPlayer(player);

			for (int slot = 0; slot < inventoryTileChest.getSizeInventory(); ++slot)
			{
				ItemStack stackTileChest = inventoryTileChest.getStackInSlot(slot);

				if (!stackTileChest.isEmpty())
				{
					entityChast.getInventoryMain().setInventorySlotContents(slot, stackTileChest);
				}

				inventoryTileChest.setInventorySlotContents(slot, ItemStack.EMPTY);
			}

			if (!worldIn.isRemote)
			{
				worldIn.spawnEntity(entityChast);
			}

			worldIn.destroyBlock(pos, false);

			if (!player.capabilities.isCreativeMode)
			{
				stack.shrink(1);
			}

			return EnumActionResult.SUCCESS;
		}

		return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
	}

}
