package schr0.chastmob.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import schr0.chastmob.init.ChastMobItems;
import schr0.chastmob.init.ChastMobLang;
import schr0.chastmob.init.ChastMobNBTs;

public class ItemSpecificationPatrol extends Item implements ISpecificationItem
{

	public ItemSpecificationPatrol()
	{
		this.setMaxStackSize(1);

		this.addPropertyOverride(new ResourceLocation("empty"), new IItemPropertyGetter()
		{
			@Override
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn)
			{
				Item item = stack.getItem();

				if (item instanceof ItemSpecificationPatrol)
				{
					if (((ItemSpecificationPatrol) item).hasHomeChest(stack) == false)
					{
						return 1.0F;
					}
				}

				return 0.0F;
			}
		});
	}

	@Override
	public boolean isItemValidForSpecificationSlot(ItemStack stack)
	{
		if (stack.getItem() == ChastMobItems.SPECIFICATION_PATROL)
		{
			if (((ItemSpecificationPatrol) stack.getItem()).hasHomeChest(stack))
			{
				return true;
			}
		}

		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack)
	{
		return this.hasHomeChest(stack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
	{
		tooltip.add(TextFormatting.ITALIC + new TextComponentTranslation(ChastMobLang.ITEM_SPECIFICATION_PATROL_TIPS, new Object[0]).getFormattedText());

		BlockPos homeChestPosition = this.getHomeChestPosition(stack);

		if (homeChestPosition != null)
		{
			tooltip.add("posX : " + homeChestPosition.getX());
			tooltip.add("posY : " + homeChestPosition.getY());
			tooltip.add("posZ : " + homeChestPosition.getZ());
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
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		ItemStack stack = player.getHeldItem(hand);

		if (!this.hasHomeChest(stack))
		{
			if (worldIn.getTileEntity(pos) instanceof TileEntityChest)
			{
				this.setHomeChestPosition(stack, pos);

				player.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 1.0F, 1.0F);

				return EnumActionResult.SUCCESS;
			}
		}

		return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
	}

	// TODO /* ======================================== MOD START =====================================*/

	public boolean hasHomeChest(ItemStack stack)
	{
		return (this.getHomeChestPosition(stack) != null);
	}

	@Nullable
	public BlockPos getHomeChestPosition(ItemStack stack)
	{
		NBTTagCompound nbtItemStack = stack.getTagCompound();

		if (nbtItemStack == null)
		{
			return (BlockPos) null;
		}

		if (nbtItemStack.hasKey(ChastMobNBTs.ITEM_HOME_CHEST_MAP_POS_X) && nbtItemStack.hasKey(ChastMobNBTs.ITEM_HOME_CHEST_MAP_POS_Y) && nbtItemStack.hasKey(ChastMobNBTs.ITEM_HOME_CHEST_MAP_POS_Z))
		{
			int posX = nbtItemStack.getInteger(ChastMobNBTs.ITEM_HOME_CHEST_MAP_POS_X);
			int posY = nbtItemStack.getInteger(ChastMobNBTs.ITEM_HOME_CHEST_MAP_POS_Y);
			int posZ = nbtItemStack.getInteger(ChastMobNBTs.ITEM_HOME_CHEST_MAP_POS_Z);

			return new BlockPos(posX, posY, posZ);
		}

		return (BlockPos) null;
	}

	public void setHomeChestPosition(ItemStack stack, BlockPos blockPos)
	{
		NBTTagCompound nbtItemStack = stack.getTagCompound();

		if (nbtItemStack == null)
		{
			nbtItemStack = new NBTTagCompound();
		}

		if (blockPos != null)
		{
			nbtItemStack.setInteger(ChastMobNBTs.ITEM_HOME_CHEST_MAP_POS_X, blockPos.getX());
			nbtItemStack.setInteger(ChastMobNBTs.ITEM_HOME_CHEST_MAP_POS_Y, blockPos.getY());
			nbtItemStack.setInteger(ChastMobNBTs.ITEM_HOME_CHEST_MAP_POS_Z, blockPos.getZ());
		}

		stack.setTagCompound(nbtItemStack);
	}

}
