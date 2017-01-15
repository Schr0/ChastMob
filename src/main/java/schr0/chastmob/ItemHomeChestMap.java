package schr0.chastmob;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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

public class ItemHomeChestMap extends Item
{

	public ItemHomeChestMap()
	{
		this.setMaxStackSize(1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack)
	{
		return (this.getHomeChestBlockPos(stack) != null);
	}

	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
	{
		BlockPos blockPos = this.getHomeChestBlockPos(stack);

		if (blockPos != null)
		{
			tooltip.add("posX : " + blockPos.getX());
			tooltip.add("posY : " + blockPos.getY());
			tooltip.add("posZ : " + blockPos.getZ());
		}
		else
		{
			String none = "NONE";
			tooltip.add("posX : " + none);
			tooltip.add("posY : " + none);
			tooltip.add("posZ : " + none);
		}
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if (this.getHomeChestBlockPos(stack) == null)
		{
			if (worldIn.getTileEntity(pos) instanceof TileEntityChest)
			{
				this.setHomeChestBlockPos(stack, pos);

				playerIn.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 1.0F, 1.0F);

				if (!worldIn.isRemote)
				{
					playerIn.addChatComponentMessage(new TextComponentTranslation("item.home_chest_map.save_log", new Object[]
					{
							(TextFormatting.ITALIC + stack.getDisplayName()), pos.getX(), pos.getY(), pos.getZ()
					}));
				}

				return EnumActionResult.SUCCESS;
			}
		}

		return super.onItemUse(stack, playerIn, worldIn, pos, hand, facing, hitX, hitY, hitZ);
	}

	// TODO /* ======================================== MOD START =====================================*/

	@Nullable
	public BlockPos getHomeChestBlockPos(ItemStack stack)
	{
		NBTTagCompound nbttagStack = stack.getTagCompound();

		if (nbttagStack == null)
		{
			return (BlockPos) null;
		}

		if (nbttagStack.hasKey(ChastMobNBTTags.HOME_CHEST_MAP_POS_X) && nbttagStack.hasKey(ChastMobNBTTags.HOME_CHEST_MAP_POS_Y) && nbttagStack.hasKey(ChastMobNBTTags.HOME_CHEST_MAP_POS_Z))
		{
			int posX = nbttagStack.getInteger(ChastMobNBTTags.HOME_CHEST_MAP_POS_X);
			int posY = nbttagStack.getInteger(ChastMobNBTTags.HOME_CHEST_MAP_POS_Y);
			int posZ = nbttagStack.getInteger(ChastMobNBTTags.HOME_CHEST_MAP_POS_Z);

			return new BlockPos(posX, posY, posZ);
		}

		return (BlockPos) null;
	}

	public void setHomeChestBlockPos(ItemStack stack, BlockPos blockPos)
	{
		NBTTagCompound nbttagStack = stack.getTagCompound();

		if (nbttagStack == null)
		{
			nbttagStack = new NBTTagCompound();
		}

		if (blockPos != null)
		{
			nbttagStack.setInteger(ChastMobNBTTags.HOME_CHEST_MAP_POS_X, blockPos.getX());
			nbttagStack.setInteger(ChastMobNBTTags.HOME_CHEST_MAP_POS_Y, blockPos.getY());
			nbttagStack.setInteger(ChastMobNBTTags.HOME_CHEST_MAP_POS_Z, blockPos.getZ());
		}

		stack.setTagCompound(nbttagStack);
	}

}
