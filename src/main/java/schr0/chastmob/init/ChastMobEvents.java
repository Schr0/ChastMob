package schr0.chastmob.init;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import schr0.chastmob.item.ItemSoulBottleFull;

public class ChastMobEvents
{

	public void register()
	{
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onLivingDeathEvent(LivingDeathEvent event)
	{
		EntityLivingBase target = event.getEntityLiving();
		EnumHand handItemSBFF = this.getHandItemSBFF(target);

		if (handItemSBFF != null)
		{
			ItemStack stackHeldItem = target.getHeldItem(handItemSBFF);
			ItemSoulBottleFull itemSBFF = (ItemSoulBottleFull) stackHeldItem.getItem();

			itemSBFF.resurrection(stackHeldItem, handItemSBFF, target);

			event.setCanceled(true);
		}
	}

	// TODO /* ======================================== MOD START =====================================*/

	@Nullable
	private EnumHand getHandItemSBFF(EntityLivingBase entityLivingBase)
	{
		if (this.isItemSBFF(entityLivingBase.getHeldItemMainhand()))
		{
			return EnumHand.MAIN_HAND;
		}

		if (this.isItemSBFF(entityLivingBase.getHeldItemOffhand()))
		{
			return EnumHand.OFF_HAND;
		}

		return (EnumHand) null;
	}

	private boolean isItemSBFF(ItemStack stack)
	{
		if ((stack.getItem() instanceof ItemSoulBottleFull) && (stack.getItemDamage() == 0))
		{
			return true;
		}

		return false;
	}

}
