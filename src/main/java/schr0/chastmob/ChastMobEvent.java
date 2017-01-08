package schr0.chastmob;

import java.util.HashSet;

import net.minecraft.entity.ai.EntityAIOcelotSit;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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
			HashSet<EntityAITaskEntry> hashset = new HashSet<EntityAITaskEntry>();
			EntityOcelot entityOcelot = (EntityOcelot) event.getEntityLiving();

			for (EntityAITaskEntry taskEntry : entityOcelot.tasks.taskEntries)
			{
				if (taskEntry.action.getClass().equals(EntityAIOcelotSit.class))
				{
					hashset.add(taskEntry);
				}
			}

			for (EntityAITaskEntry task : hashset)
			{
				entityOcelot.tasks.removeTask(task.action);
				entityOcelot.tasks.addTask(task.priority, new EntityAIOcelotSitChast(entityOcelot, 0.8F, 8.0D));
			}
		}
	}

}
