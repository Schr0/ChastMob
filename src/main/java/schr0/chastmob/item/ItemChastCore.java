package schr0.chastmob.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import schr0.chastmob.ChastMobHelper;
import schr0.chastmob.entity.EntityChast;

public class ItemChastCore extends Item
{

	public ItemChastCore()
	{
		this.setMaxStackSize(1);
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		TileEntity tileEntity = worldIn.getTileEntity(pos);

		if (tileEntity instanceof TileEntityChest)
		{
			EntityChast entityChast = new EntityChast(worldIn);
			IInventory inventoryTileChest = (IInventory) tileEntity;

			entityChast.setPosition(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
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
