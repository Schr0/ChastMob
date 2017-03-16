package schr0.chastmob.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import schr0.chastmob.ChastMob;
import schr0.chastmob.ChastMobHelper;
import schr0.chastmob.init.ChastMobGui;
import schr0.chastmob.init.ChastMobLang;
import schr0.chastmob.init.ChastMobNBTs;
import schr0.chastmob.inventory.InventoryFilter;

public class ItemFilter extends Item
{

	public ItemFilter()
	{
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		this.setMaxStackSize(1);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack)
	{
		return super.getUnlocalizedName() + "_" + stack.getMetadata();
	}

	@Override
	public boolean hasEffect(ItemStack stack)
	{
		return this.hasInventoryFilter(stack);
	}

	@Override
	public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems)
	{
		subItems.add(new ItemStack(itemIn, 1, 0));
		subItems.add(new ItemStack(itemIn, 1, 1));
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
	{
		if (this.getFilterType(stack) == ItemFilter.Type.WHITE)
		{
			tooltip.add(TextFormatting.ITALIC + new TextComponentTranslation(ChastMobLang.ITEM_FILTER_0_TIPS, new Object[0]).getFormattedText());
		}
		else
		{
			tooltip.add(TextFormatting.ITALIC + new TextComponentTranslation(ChastMobLang.ITEM_FILTER_1_TIPS, new Object[0]).getFormattedText());
		}

		tooltip.add("");

		if (this.hasInventoryFilter(stack))
		{
			InventoryFilter inventoryFilter = this.getInventoryFilter(stack);
			int num = 1;

			for (int slot = 0; slot < inventoryFilter.getSizeInventory(); ++slot)
			{
				ItemStack stackSlot = inventoryFilter.getStackInSlot(slot);

				if (ChastMobHelper.isNotEmptyItemStack(stackSlot))
				{
					String nameItem = stackSlot.getDisplayName();

					if (inventoryFilter.getFilterType() == ItemFilter.Type.BLACK)
					{
						nameItem = (TextFormatting.RED + nameItem);
					}

					tooltip.add(num + " : " + nameItem);

					++num;
				}
			}
		}
		else
		{
			tooltip.add("NONE");
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
	{
		if (handIn == EnumHand.OFF_HAND)
		{
			return super.onItemRightClick(worldIn, playerIn, handIn);
		}

		ItemStack stack = playerIn.getHeldItem(handIn);

		if (!worldIn.isRemote)
		{
			playerIn.openGui(ChastMob.instance, ChastMobGui.ID_FILTER_EDIT, worldIn, 0, 0, 0);
		}

		return new ActionResult(EnumActionResult.SUCCESS, stack);
	}

	// TODO /* ======================================== MOD START =====================================*/

	public static enum Type
	{
		WHITE,
		BLACK
	}

	public boolean hasInventoryFilter(ItemStack stack)
	{
		return !this.getInventoryFilter(stack).isEmpty();
	}

	public InventoryFilter getInventoryFilter(ItemStack stack)
	{
		InventoryFilter inventoryFilter = new InventoryFilter(stack);
		NBTTagCompound nbtItemStack = stack.getTagCompound();

		if ((nbtItemStack != null) && nbtItemStack.hasKey(ChastMobNBTs.ITEM_FILTER_INVENTORY))
		{
			inventoryFilter.readInventoryFromNBT(nbtItemStack.getTagList(ChastMobNBTs.ITEM_FILTER_INVENTORY, 10));

			return inventoryFilter;
		}

		return inventoryFilter;
	}

	public void setInventoryFilter(ItemStack stack, InventoryFilter inventoryFilter)
	{
		NBTTagCompound nbtItemStack = stack.getTagCompound();

		if (nbtItemStack == null)
		{
			nbtItemStack = new NBTTagCompound();
		}

		nbtItemStack.setTag(ChastMobNBTs.ITEM_FILTER_INVENTORY, inventoryFilter.writeInventoryToNBT());

		stack.setTagCompound(nbtItemStack);
	}

	public ItemFilter.Type getFilterType(ItemStack stack)
	{
		if (stack.getItemDamage() == 1)
		{
			return ItemFilter.Type.BLACK;
		}

		return ItemFilter.Type.WHITE;
	}

}
