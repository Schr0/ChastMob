package schr0.chastmob.entity.ai;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import schr0.chastmob.entity.EntityChast;
import schr0.chastmob.inventory.InventoryChastEquipments;
import schr0.chastmob.inventory.InventoryChastMain;

public class EntityAIChastStatePanic extends EntityAIChast
{

	private int panicTime;
	private double randPosX;
	private double randPosY;
	private double randPosZ;

	public EntityAIChastStatePanic(EntityChast entityChast)
	{
		super(entityChast);
		this.panicTime = 0;
		this.randPosX = 0;
		this.randPosY = 0;
		this.randPosZ = 0;
	}

	@Override
	public boolean shouldExecute()
	{
		if (this.isPanic())
		{
			return true;
		}

		return false;
	}

	@Override
	public void resetTask()
	{
		super.resetTask();

		this.panicTime = 0;
		this.randPosX = 0;
		this.randPosY = 0;
		this.randPosZ = 0;
		this.getEntity().setPanic(false);
	}

	@Override
	public void updateTask()
	{
		super.updateTask();

		--this.panicTime;

		if (this.isIdle())
		{
			return;
		}

		this.getEntity().setCoverOpen(true);

		if ((this.getEntity().getRNG().nextInt(10) == 0) && !this.getWorld().isRemote)
		{
			this.onPanicDropItem(this.getEntity());
		}

		if (this.getEntity().isBurning())
		{
			BlockPos blockPos = this.getNearWaterBlockPos(this.getEntity(), this.getRange());

			if (blockPos == null)
			{
				return;
			}
			else
			{
				this.randPosX = (double) blockPos.getX();
				this.randPosY = (double) blockPos.getY();
				this.randPosZ = (double) blockPos.getZ();
			}
		}
		else
		{
			Vec3d vec3d = RandomPositionGenerator.findRandomTarget(this.getEntity(), this.getRange(), this.getRange());

			if (vec3d == null)
			{
				return;
			}
			else
			{
				this.randPosX = vec3d.x;
				this.randPosY = vec3d.y;
				this.randPosZ = vec3d.z;
			}
		}

		this.getEntity().getNavigator().tryMoveToXYZ(this.randPosX, this.randPosY, this.randPosZ, this.getSpeed());
	}

	@Override
	public double getSpeed()
	{
		return (super.getSpeed() * 2);
	}

	// TODO /* ======================================== MOD START =====================================*/

	public void startTask(int sec)
	{
		if (!this.getEntity().isEquipHelmet())
		{
			sec = Math.max(4, sec);
			sec = Math.min(8, sec);

			this.panicTime = ((sec * 20) + this.getIdleTime());
			this.randPosX = 0;
			this.randPosY = 0;
			this.randPosZ = 0;
			this.getEntity().setPanic(true);
		}
	}

	public void stopTask()
	{
		this.panicTime = 0;
		this.randPosX = 0;
		this.randPosY = 0;
		this.randPosZ = 0;
		this.getEntity().setPanic(false);
	}

	private boolean isPanic()
	{
		return (0 < this.panicTime);
	}

	private int getIdleTime()
	{
		return (2 * 20);
	}

	private boolean isIdle()
	{
		return (this.panicTime < this.getIdleTime());
	}

	private void onPanicDropItem(EntityChast entityChast)
	{
		InventoryChastEquipments inventoryChastEquipment = entityChast.getInventoryEquipments();

		for (int slot = 0; slot < inventoryChastEquipment.getSizeInventory(); ++slot)
		{
			if ((slot == 0) || (slot == 3))
			{
				continue;
			}

			ItemStack stackSlot = inventoryChastEquipment.getStackInSlot(slot);

			if (!stackSlot.isEmpty())
			{
				if (entityChast.getRNG().nextInt(2) == 0)
				{
					continue;
				}

				ItemStack stackSlotCopy = stackSlot.copy();

				stackSlotCopy.setCount(1);

				Block.spawnAsEntity(entityChast.getEntityWorld(), entityChast.getPosition(), stackSlotCopy);

				stackSlot.shrink(1);

				entityChast.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 1.0F, 1.0F);

				return;
			}
		}

		InventoryChastMain inventoryChastMain = entityChast.getInventoryMain();

		for (int slot = 0; slot < inventoryChastMain.getSizeInventory(); ++slot)
		{
			ItemStack stackSlot = inventoryChastMain.getStackInSlot(slot);

			if (!stackSlot.isEmpty())
			{
				if (entityChast.getRNG().nextInt(2) == 0)
				{
					continue;
				}

				ItemStack stackSlotCopy = stackSlot.copy();

				stackSlotCopy.setCount(1);

				Block.spawnAsEntity(entityChast.getEntityWorld(), entityChast.getPosition(), stackSlotCopy);

				stackSlot.shrink(1);

				entityChast.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 1.0F, 1.0F);

				return;
			}
		}
	}

	@Nullable
	private BlockPos getNearWaterBlockPos(Entity entity, int searchXYZ)
	{
		BlockPos blockPos = new BlockPos(entity);
		int entityPosX = blockPos.getX();
		int entityPosY = blockPos.getY();
		int entityPosZ = blockPos.getZ();
		float rangeOrigin = (float) (searchXYZ * searchXYZ * searchXYZ * 2);
		BlockPos.MutableBlockPos blockPosMutable = new BlockPos.MutableBlockPos();
		BlockPos blockPosWater = null;

		for (int posX = (entityPosX - searchXYZ); posX <= (entityPosX + searchXYZ); ++posX)
		{
			for (int posY = (entityPosY - searchXYZ); posY <= (entityPosY + searchXYZ); ++posY)
			{
				for (int posZ = (entityPosZ - searchXYZ); posZ <= (entityPosZ + searchXYZ); ++posZ)
				{
					blockPosMutable.setPos(posX, posY, posZ);
					Block block = entity.getEntityWorld().getBlockState(blockPosMutable).getBlock();

					if ((block == Blocks.WATER) || (block == Blocks.FLOWING_WATER))
					{
						float range = (float) ((posX - entityPosX) * (posX - entityPosX) + (posY - entityPosY) * (posY - entityPosY) + (posZ - entityPosZ) * (posZ - entityPosZ));

						if (range < rangeOrigin)
						{
							rangeOrigin = range;
							blockPosWater = new BlockPos(blockPosMutable);
						}
					}
				}
			}
		}

		return blockPosWater;
	}

}
