package schr0.chastmob.item;

import java.util.List;

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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import schr0.chastmob.ChastMobHelper;
import schr0.chastmob.entity.EntityChast;
import schr0.chastmob.init.ChastMobLang;

public class ItemCoreChast extends Item
{

	public ItemCoreChast()
	{
		this.setMaxStackSize(1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
	{
		tooltip.add(TextFormatting.ITALIC + new TextComponentTranslation(ChastMobLang.ITEM_CORE_CHAST_TIPS, new Object[0]).getFormattedText());
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		TileEntity tileEntity = worldIn.getTileEntity(pos);

		if (worldIn.getBlockState(pos).getBlock().equals(Blocks.CHEST) && (tileEntity instanceof TileEntityChest))
		{
			EntityChast entityChast = new EntityChast(worldIn);
			IInventory inventoryTileChest = (IInventory) tileEntity;

			entityChast.setPositionAndRotation(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0.0F, 0.0F);

			if (stack.hasDisplayName())
			{
				entityChast.setCustomNameTag(stack.getDisplayName());
				entityChast.enablePersistence();
			}

			entityChast.onSpawnByPlayer(playerIn);

			for (int slot = 0; slot < inventoryTileChest.getSizeInventory(); ++slot)
			{
				ItemStack stackTileChest = inventoryTileChest.getStackInSlot(slot);

				if (ChastMobHelper.isNotEmptyItemStack(stackTileChest))
				{
					entityChast.getInventoryChast().setInventorySlotContents(slot, stackTileChest);
				}

				inventoryTileChest.setInventorySlotContents(slot, ChastMobHelper.getEmptyItemStack());
			}

			if (!worldIn.isRemote)
			{
				worldIn.spawnEntityInWorld(entityChast);
			}

			worldIn.destroyBlock(pos, false);

			if (!playerIn.capabilities.isCreativeMode)
			{
				--stack.stackSize;
			}

			return EnumActionResult.SUCCESS;
		}

		return super.onItemUse(stack, playerIn, worldIn, pos, hand, facing, hitX, hitY, hitZ);
	}

}
