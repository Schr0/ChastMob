package schr0.chastmob.init;

import java.util.HashSet;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIOcelotSit;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import schr0.chastmob.item.ItemSoulBottleFull;
import schr0.chastmob.util.EntityAIOcelotSitChast;

public class ChastMobEvents
{

	public void registerEvents()
	{
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onLivingUpdateEvent(LivingUpdateEvent event)
	{
		EntityLivingBase target = event.getEntityLiving();

		if ((target instanceof EntityOcelot) && (target.ticksExisted < 20))
		{
			EntityOcelot entityOcelot = (EntityOcelot) target;
			HashSet<EntityAITaskEntry> hashSetEntityAITaskEntry = new HashSet<EntityAITaskEntry>();

			for (EntityAITaskEntry taskEntry : entityOcelot.tasks.taskEntries)
			{
				if (taskEntry.action.getClass().equals(EntityAIOcelotSit.class))
				{
					hashSetEntityAITaskEntry.add(taskEntry);
				}
			}

			for (EntityAITaskEntry task : hashSetEntityAITaskEntry)
			{
				entityOcelot.tasks.removeTask(task.action);
				entityOcelot.tasks.addTask(task.priority, new EntityAIOcelotSitChast(entityOcelot, 0.8F, 8.0D));
			}
		}
	}

	@SubscribeEvent
	public void onLivingDeathEvent(LivingDeathEvent event)
	{
		EntityLivingBase target = event.getEntityLiving();
		EnumHand handItemSoulBottleFullFriendly = this.getHandSoulBottleFullFriendly(target);
		ItemStack stackHeldItem = target.getHeldItem(handItemSoulBottleFullFriendly);

		if (this.isItemSoulBottleFullFriendly(stackHeldItem))
		{
			((ItemSoulBottleFull) stackHeldItem.getItem()).resurrectionOwner(stackHeldItem, handItemSoulBottleFullFriendly, target);

			event.setCanceled(true);
		}
	}

	// TODO /* ======================================== MOD START =====================================*/

	private EnumHand getHandSoulBottleFullFriendly(EntityLivingBase entityLivingBase)
	{
		if (this.isItemSoulBottleFullFriendly(entityLivingBase.getHeldItemOffhand()))
		{
			return EnumHand.OFF_HAND;
		}

		return EnumHand.MAIN_HAND;
	}

	private boolean isItemSoulBottleFullFriendly(ItemStack stack)
	{
		if ((stack.getItem() instanceof ItemSoulBottleFull) && (stack.getItemDamage() == 0))
		{
			return true;
		}

		return false;
	}

}
