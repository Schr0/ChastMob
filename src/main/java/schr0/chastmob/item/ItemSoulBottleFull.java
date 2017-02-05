package schr0.chastmob.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import schr0.chastmob.ChastMobHelper;
import schr0.chastmob.init.ChastMobItems;

public class ItemSoulBottleFull extends Item
{

	public static final int MAX_DAMAGE = (5 * 60);

	public ItemSoulBottleFull()
	{
		this.setMaxStackSize(1);
		this.setMaxDamage(MAX_DAMAGE);
		this.setContainerItem(ChastMobItems.SOUL_BOTTLE);

		this.addPropertyOverride(new ResourceLocation("friendly"), new IItemPropertyGetter()
		{
			@Override
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn)
			{
				if (ChastMobHelper.isNotEmptyItemStack(stack) && (stack.getItemDamage() == 0))
				{
					return 1.0F;
				}

				return 0.0F;
			}
		});
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems)
	{
		subItems.add(new ItemStack(itemIn, 1, MAX_DAMAGE));
		subItems.add(new ItemStack(itemIn, 1));
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
	{
		super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);

		if (!(entityIn instanceof EntityPlayer) || (stack.getItemDamage() == 0))
		{
			return;
		}

		EntityPlayer entityPlayer = (EntityPlayer) entityIn;

		if (entityPlayer.ticksExisted % 20 == 0)
		{
			int healAmount = 1;

			if (itemSlot < entityPlayer.inventory.getHotbarSize())
			{
				healAmount = 2;
			}

			if (isSelected)
			{
				healAmount = 4;
			}

			if (entityPlayer.isPlayerSleeping())
			{
				healAmount = MAX_DAMAGE;
			}

			stack.setItemDamage(Math.max(0, (stack.getItemDamage() - healAmount)));
		}

		if (stack.getItemDamage() == 0)
		{
			worldIn.playEvent(2005, entityPlayer.getPosition(), 30);

			entityPlayer.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
		}
	}

}
