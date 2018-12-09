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
	private static final String TAG_POS_X = TAG + "pos_x";
	private static final String TAG_POS_Y = TAG + "pos_y";
	private static final String TAG_POS_Z = TAG + "pos_z";

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

					if (itemHomeMap.hasPosition(stack))
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
		BlockPos homePos = this.getPosition(stack);

		if (homePos.equals(BlockPos.ORIGIN))
		{
			String none = "NONE";
			tooltip.add("posX : " + none);
			tooltip.add("posY : " + none);
			tooltip.add("posZ : " + none);
		}
		else
		{
			tooltip.add("posX : " + homePos.getX());
			tooltip.add("posY : " + homePos.getY());
			tooltip.add("posZ : " + homePos.getZ());
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
		return this.hasPosition(stack);
	}

	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand)
	{
		ItemStack stack = player.getHeldItem(hand);

		if (!this.hasPosition(stack))
		{
			if (world.getTileEntity(pos) instanceof TileEntityChest)
			{
				if (!world.isRemote)
				{
					this.setPosition(stack, pos);

					player.sendMessage(new TextComponentTranslation("item.home_map.message", new Object[]
					{
							pos.getX(),
							pos.getY(),
							pos.getZ(),
					}));
				}

				player.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 1.0F, 1.0F);

				return EnumActionResult.SUCCESS;
			}
		}

		return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
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

	public boolean hasPosition(ItemStack stack)
	{
		if (this.getPosition(stack).equals(BlockPos.ORIGIN))
		{
			return false;
		}

		return true;
	}

}
