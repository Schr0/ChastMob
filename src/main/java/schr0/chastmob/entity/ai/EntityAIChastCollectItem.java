package schr0.chastmob.entity.ai;

import java.util.List;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.math.AxisAlignedBB;
import schr0.chastmob.entity.EntityChast;
import schr0.chastmob.init.ChastMobPackets;
import schr0.chastmob.inventory.InventoryChastHelper;
import schr0.chastmob.packet.MessageParticleEntity;
import schr0.chastmob.util.ChastMobParticles;

public class EntityAIChastCollectItem extends EntityAIChast
{

	private static final double COLLECT_DISTANCE = 1.5D;
	private static final int OPEN_COUNT_LIMIT = 2;
	private EntityItem targetEntityItem;
	private int openCount;

	public EntityAIChastCollectItem(EntityChast entityChast)
	{
		super(entityChast);
		this.targetEntityItem = null;
		this.openCount = 0;
	}

	@Override
	public boolean shouldExecute()
	{
		this.targetEntityItem = null;
		this.openCount = 0;

		float rangeOrigin = 0.0F;

		for (EntityItem entityItem : this.getAroundEntityItem())
		{
			float range = (float) this.getEntity().getDistanceSq(entityItem);

			if ((range < rangeOrigin) || (rangeOrigin == 0.0F))
			{
				rangeOrigin = range;

				if (this.canCollectEntityItem(entityItem))
				{
					if (InventoryChastHelper.canStoreInventory(this.getEntity().getInventoryMain(), entityItem.getItem()))
					{
						this.targetEntityItem = entityItem;
						this.openCount = OPEN_COUNT_LIMIT;

						return true;
					}
				}
			}
		}

		return false;
	}

	@Override
	public boolean shouldContinueExecuting()
	{
		if (this.isTimeOut())
		{
			return false;
		}

		return (this.targetEntityItem != null);
	}

	@Override
	public void startExecuting()
	{
		super.startExecuting();

		ChastMobPackets.DISPATCHER.sendToAll(new MessageParticleEntity(this.getEntity(), ChastMobParticles.MUSIC));
	}

	@Override
	public void resetTask()
	{
		super.resetTask();

		this.targetEntityItem = null;
		this.openCount = 0;
	}

	@Override
	public void updateTask()
	{
		super.updateTask();

		this.getEntity().getLookHelper().setLookPositionWithEntity(this.targetEntityItem, this.getEntity().getHorizontalFaceSpeed(), this.getEntity().getVerticalFaceSpeed());

		if (this.getEntity().getDistanceSq(this.targetEntityItem) < COLLECT_DISTANCE)
		{
			for (EntityItem entityItem : this.getAroundEntityItem())
			{
				if (entityItem.equals(this.targetEntityItem) && this.canCollectEntityItem(entityItem))
				{
					this.getEntity().setCoverOpen(true);

					--this.openCount;

					if (this.openCount < 0)
					{
						TileEntityHopper.putDropInInventoryAllSlots((IInventory) null, this.getEntity().getInventoryMain(), entityItem);

						this.targetEntityItem = null;
						this.openCount = 0;

						return;
					}
				}
			}
		}
		else
		{
			this.getEntity().getNavigator().tryMoveToEntityLiving(this.targetEntityItem, this.getSpeed());
		}
	}

	// TODO /* ======================================== MOD START =====================================*/

	private List<EntityItem> getAroundEntityItem()
	{
		return this.getWorld().getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(this.getHomePosition()).grow(this.getRange(), this.getRange(), this.getRange()));
	}

	private boolean canCollectEntityItem(EntityItem entityItem)
	{
		if (this.getEntity().getEntitySenses().canSee(entityItem) && entityItem.isEntityAlive() && !entityItem.cannotPickup())
		{
			return true;
		}

		return false;
	}

}
