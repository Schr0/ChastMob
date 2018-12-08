package schr0.chastmob.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
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
import schr0.chastmob.ChastMob;

public class ItemHomeMap extends Item
{

	private static final String TAG = ChastMob.MOD_ID + "." + "item_home_map" + ".";
	private static final String TAG_POS_X = TAG + "home_pos_x";
	private static final String TAG_POS_Y = TAG + "home_pos_y";
	private static final String TAG_POS_Z = TAG + "home_pos_z";

	public ItemHomeMap()
	{
		this.setMaxStackSize(1);

		this.addPropertyOverride(new ResourceLocation("fill"), new IItemPropertyGetter()
		{

			@SideOnly(Side.CLIENT)
			@Override
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn)
			{
				Item item = stack.getItem();

				if (item instanceof ItemHomeMap)
				{
					ItemHomeMap itemHomeMap = (ItemHomeMap) item;

					if (itemHomeMap.hasHomeChest(stack))
					{
						return 1.0F;
					}
				}

				return 0.0F;
			}

		});
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{
		BlockPos homePosition = this.getPosition(stack);

		if (homePosition.equals(BlockPos.ORIGIN))
		{
			tooltip.add("posX : " + homePosition.getX());
			tooltip.add("posY : " + homePosition.getY());
			tooltip.add("posZ : " + homePosition.getZ());
		}
		else
		{
			String none = "NONE";
			tooltip.add("posX : " + none);
			tooltip.add("posY : " + none);
			tooltip.add("posZ : " + none);
		}

		TextComponentTranslation info = new TextComponentTranslation("item.home_map.tooltip", new Object[0]);

		info.getStyle().setColor(TextFormatting.BLUE);
		info.getStyle().setItalic(true);

		tooltip.add(info.getFormattedText());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean hasEffect(ItemStack stack)
	{
		return this.hasHomeChest(stack);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		ItemStack stack = player.getHeldItem(hand);

		if (!this.hasHomeChest(stack))
		{
			if (worldIn.getTileEntity(pos) instanceof TileEntityChest)
			{
				this.setPosition(stack, pos);

				player.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 1.0F, 1.0F);

				return EnumActionResult.SUCCESS;
			}
		}

		return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
	}

	// TODO /* ======================================== MOD START =====================================*/

	public BlockPos getPosition(ItemStack stack)
	{
		BlockPos posOrigin = BlockPos.ORIGIN;
		NBTTagCompound nbt = stack.getTagCompound();

		if (nbt == null)
		{
			return posOrigin;
		}

		if (nbt.hasKey(TAG_POS_X) && nbt.hasKey(TAG_POS_Y) && nbt.hasKey(TAG_POS_X))
		{
			int posX = nbt.getInteger(TAG_POS_X);
			int posY = nbt.getInteger(TAG_POS_Y);
			int posZ = nbt.getInteger(TAG_POS_Z);

			return new BlockPos(posX, posY, posZ);
		}

		return posOrigin;
	}

	public void setPosition(ItemStack stack, BlockPos blockPos)
	{
		NBTTagCompound nbt = stack.getTagCompound();

		if (nbt == null)
		{
			nbt = new NBTTagCompound();
		}

		nbt.setInteger(TAG_POS_X, blockPos.getX());
		nbt.setInteger(TAG_POS_Y, blockPos.getY());
		nbt.setInteger(TAG_POS_Z, blockPos.getZ());

		stack.setTagCompound(nbt);
	}

	public boolean hasHomeChest(ItemStack stack)
	{
		if (this.getPosition(stack).equals(BlockPos.ORIGIN))
		{
			return false;
		}

		return true;
	}

}
