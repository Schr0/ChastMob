package schr0.chastmob.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import schr0.chastmob.ChastMobHelper;
import schr0.chastmob.init.ChastMobNBTs;

public class ItemFilter extends Item
{

	public static enum Type
	{
		WHITE,
		BLACK
	}

	public static class InventoryFilter extends InventoryBasic
	{

		private ItemStack stackFilter;

		public InventoryFilter(ItemStack stack)
		{
			super(stack.getDisplayName(), true, 9);

			this.stackFilter = stack;
		}

		@Override
		public int getInventoryStackLimit()
		{
			return 1;
		}

		@Override
		public boolean isUsableByPlayer(EntityPlayer player)
		{
			if (ItemStack.areItemStackTagsEqual(player.getHeldItemMainhand(), this.stackFilter))
			{
				return true;
			}

			return false;
		}

		@Override
		public void closeInventory(EntityPlayer player)
		{
			Item item = this.stackFilter.getItem();

			if (item instanceof ItemFilter)
			{
				((ItemFilter) item).setInventoryFilter(this.stackFilter, this);
			}
		}

		// TODO /* ======================================== MOD START =====================================*/

		public void readInventoryFromNBT(NBTTagList nbtList)
		{
			this.clear();

			for (int slot = 0; slot < nbtList.tagCount(); ++slot)
			{
				NBTTagCompound nbt = nbtList.getCompoundTagAt(slot);
				slot = nbt.getByte("Slot") & 255;

				if ((0 <= slot) && (slot < this.getSizeInventory()))
				{
					this.setInventorySlotContents(slot, new ItemStack(nbt));
				}
			}
		}

		public NBTTagList writeInventoryToNBT()
		{
			NBTTagList nbtList = new NBTTagList();

			for (int slot = 0; slot < this.getSizeInventory(); ++slot)
			{
				ItemStack stackSlot = this.getStackInSlot(slot);

				if (ChastMobHelper.isNotEmptyItemStack(stackSlot))
				{
					NBTTagCompound nbt = new NBTTagCompound();
					nbt.setByte("Slot", (byte) slot);
					stackSlot.writeToNBT(nbt);
					nbtList.appendTag(nbt);
				}
			}

			return nbtList;
		}

		public ItemFilter.Type getFilterType()
		{
			Item item = this.stackFilter.getItem();

			if (item instanceof ItemFilter)
			{
				return ((ItemFilter) item).getFilterType(stackFilter);
			}

			return ItemFilter.Type.WHITE;
		}

	}

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
		if (this.hasInventoryFilter(stack))
		{
			ItemFilter.InventoryFilter inventoryFilter = this.getInventoryFilter(stack);

			for (int slot = 0; slot < inventoryFilter.getSizeInventory(); ++slot)
			{
				ItemStack stackSlot = inventoryFilter.getStackInSlot(slot);

				String nameItem;

				if (ChastMobHelper.isNotEmptyItemStack(stackSlot))
				{
					if (inventoryFilter.getFilterType() == ItemFilter.Type.BLACK)
					{
						nameItem = TextFormatting.RED + stackSlot.getDisplayName();
					}
					else
					{
						nameItem = stackSlot.getDisplayName();
					}
				}
				else
				{
					if (inventoryFilter.getFilterType() == ItemFilter.Type.WHITE)
					{
						nameItem = TextFormatting.RED + "NONE";
					}
					else
					{
						nameItem = "NONE";
					}
				}

				tooltip.add((slot + 1) + " : " + nameItem);
			}
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
			ItemFilter.InventoryFilter inventoryFilter = new ItemFilter.InventoryFilter(stack);

			if (this.hasInventoryFilter(stack))
			{
				inventoryFilter = this.getInventoryFilter(stack);
			}

			playerIn.displayGUIChest(inventoryFilter);
		}

		return new ActionResult(EnumActionResult.SUCCESS, stack);
	}

	// TODO /* ======================================== MOD START =====================================*/

	public boolean hasInventoryFilter(ItemStack stack)
	{
		ItemFilter.InventoryFilter inventoryFilter = this.getInventoryFilter(stack);

		if (inventoryFilter != null)
		{
			return !inventoryFilter.isEmpty();
		}

		return false;
	}

	@Nullable
	public ItemFilter.InventoryFilter getInventoryFilter(ItemStack stack)
	{
		NBTTagCompound nbtItemStack = stack.getTagCompound();

		if ((nbtItemStack != null) && nbtItemStack.hasKey(ChastMobNBTs.ITEM_FILTER_INVENTORY))
		{
			ItemFilter.InventoryFilter inventoryFilter = new ItemFilter.InventoryFilter(stack);

			inventoryFilter.readInventoryFromNBT(nbtItemStack.getTagList(ChastMobNBTs.ITEM_FILTER_INVENTORY, 10));

			return inventoryFilter;
		}

		return (ItemFilter.InventoryFilter) null;
	}

	public void setInventoryFilter(ItemStack stack, ItemFilter.InventoryFilter inventoryFilter)
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
