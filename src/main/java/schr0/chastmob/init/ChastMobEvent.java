package schr0.chastmob.init;

import java.util.HashSet;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIOcelotSit;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import schr0.chastmob.api.EntityAIOcelotSitChast;
import schr0.chastmob.item.ItemSoulFragment;

public class ChastMobEvent
{

	public void init()
	{
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onLivingUpdateEvent(LivingUpdateEvent event)
	{
		if ((event.getEntityLiving().ticksExisted < 5) && (event.getEntityLiving() instanceof EntityOcelot))
		{
			EntityOcelot entityOcelot = (EntityOcelot) event.getEntityLiving();
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
	public void onEntityInteractEvent(PlayerInteractEvent.EntityInteract event)
	{
		EntityPlayer player = event.getEntityPlayer();
		ItemStack stackHand = event.getItemStack();
		Entity targetEntity = event.getTarget();
		World world = player.getEntityWorld();

		if ((stackHand != null) && stackHand.getItem().equals(ChastMobItems.SOUL_FRAGMENT) && (targetEntity instanceof EntityLivingBase))
		{
			EntityLivingBase targetLivingBase = (EntityLivingBase) targetEntity;

			if ((0.0F < targetLivingBase.getHealth()) && (targetLivingBase.getHealth() < targetLivingBase.getMaxHealth()))
			{
				ItemSoulFragment.healLivingBase(stackHand, world, targetLivingBase, player);

				event.setCanceled(true);
			}
		}
	}

}
