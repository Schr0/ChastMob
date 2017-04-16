package schr0.chastmob.init;

import java.util.HashSet;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIOcelotSit;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import schr0.chastmob.item.ItemFilter;
import schr0.chastmob.item.ItemModePatrol;
import schr0.chastmob.item.ItemSoulBottleFull;
import schr0.chastmob.item.ItemSoulFragment;
import schr0.chastmob.vanilla.EntityAIOcelotSitChast;

public class ChastMobEvent
{

	public void init()
	{
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onLivingUpdateEvent(LivingUpdateEvent event)
	{
		EntityLivingBase entityLivingBase = event.getEntityLiving();

		if ((entityLivingBase instanceof EntityOcelot) && (entityLivingBase.ticksExisted < 20))
		{
			EntityOcelot entityOcelot = (EntityOcelot) entityLivingBase;
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
		EntityPlayer entityPlayer = event.getEntityPlayer();
		Entity entityTarget = event.getTarget();
		ItemStack itemStack = event.getItemStack();
		World world = entityPlayer.getEntityWorld();

		if ((itemStack.getItem() instanceof ItemSoulFragment) && (entityTarget instanceof EntityLivingBase))
		{
			EntityLivingBase targetLivingBase = (EntityLivingBase) entityTarget;

			if ((0.0F < targetLivingBase.getHealth()) && (targetLivingBase.getHealth() < targetLivingBase.getMaxHealth()))
			{
				((ItemSoulFragment) (itemStack.getItem())).healLivingBase(itemStack, world, targetLivingBase, entityPlayer);

				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public void onLivingDeathEvent(LivingDeathEvent event)
	{
		EntityLivingBase entityLivingBase = event.getEntityLiving();
		EnumHand handItemSoulBottleFullFriendly = this.getHandSoulBottleFullFriendly(entityLivingBase);
		ItemStack stackHeldItem = entityLivingBase.getHeldItem(handItemSoulBottleFullFriendly);

		if (this.isItemSoulBottleFullFriendly(stackHeldItem))
		{
			((ItemSoulBottleFull) stackHeldItem.getItem()).resurrectionOwner(stackHeldItem, handItemSoulBottleFullFriendly, entityLivingBase);

			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onAnvilUpdateEvent(AnvilUpdateEvent event)
	{
		ItemStack leftStack = event.getLeft();
		ItemStack rightStack = event.getRight();

		if (this.canAnvilLeftCopyItems(leftStack, rightStack))
		{
			event.setCost(5);
			event.setMaterialCost(1);
			event.setOutput(leftStack.copy());
		}
	}

	@SubscribeEvent
	public void onAnvilRepairEvent(AnvilRepairEvent event)
	{
		ItemStack leftStack = event.getItemInput();
		ItemStack rightStack = event.getIngredientInput();
		EntityPlayer player = event.getEntityPlayer();

		if (this.canAnvilLeftCopyItems(leftStack, rightStack))
		{
			event.setBreakChance(0.0F);

			player.addExperienceLevel(5);

			if (!player.inventory.addItemStackToInventory(leftStack))
			{
				player.dropItem(leftStack, false);
			}
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

	private boolean canAnvilLeftCopyItems(ItemStack left, ItemStack right)
	{
		boolean isRightItemSoulFragment = (right.getItem() == ChastMobItems.SOUL_FRAGMENT);

		if ((left.getItem() == ChastMobItems.MODE_PATROL) && ((ItemModePatrol) left.getItem()).hasHomeChest(left))
		{
			return isRightItemSoulFragment;
		}

		if ((left.getItem() == ChastMobItems.FILTER) && ((ItemFilter) left.getItem()).hasInventoryFilterResult(left))
		{
			return isRightItemSoulFragment;
		}

		return false;
	}

}
