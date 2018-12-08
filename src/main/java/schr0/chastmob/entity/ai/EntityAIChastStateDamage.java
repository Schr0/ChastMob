package schr0.chastmob.entity.ai;

import java.util.Random;

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

public class EntityAIChastStateDamage extends EntityAIChast
{

	private double randPosX;
	private double randPosY;
	private double randPosZ;
	private int damageTime;

	public EntityAIChastStateDamage(EntityChast entityChast)
	{
		super(entityChast);
		this.randPosX = 0;
		this.randPosY = 0;
		this.randPosZ = 0;
		this.damageTime = 0;
	}

	@Override
	public boolean shouldExecute()
	{
		if (0 < this.damageTime)
		{
			return true;
		}

		return false;
	}

	@Override
	public void resetTask()
	{
		super.resetTask();

		this.randPosX = 0;
		this.randPosY = 0;
		this.randPosZ = 0;
		this.damageTime = 0;
		this.getEntity().setDamage(false);
	}

	@Override
	public void updateTask()
	{
		super.updateTask();

		--this.damageTime;

		if (this.isIdle() || this.getEntity().isEquipHelmet())
		{
			return;
		}

		this.getEntity().setCoverOpen(true);

		if (this.getEntity().getRNG().nextFloat() < 0.5F)
		{
			this.onPanicDropItem(this.getEntity());
		}

		if (this.getEntity().isBurning())
		{
			BlockPos blockPos = this.getNearWaterPosition(this.getEntity(), this.getRange());

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
		sec = Math.max(4, sec);
		sec = Math.min(8, sec);

		this.damageTime = ((sec * 20) + this.getIdleTime());
		this.randPosX = 0;
		this.randPosY = 0;
		this.randPosZ = 0;
		this.getEntity().setDamage(true);
	}

	private int getIdleTime()
	{
		return (2 * 20);
	}

	private boolean isIdle()
	{
		return (this.damageTime < this.getIdleTime());
	}

	private void onPanicDropItem(EntityChast entityChast)
	{
		InventoryChastEquipments inventoryChastEquipment = entityChast.getInventoryEquipments();
		Random random = entityChast.getRNG();

		for (int slot = 0; slot < inventoryChastEquipment.getSizeInventory(); ++slot)
		{
			if ((slot == 0) || (random.nextFloat() < 0.5F))
			{
				continue;
			}

			ItemStack stackSlot = inventoryChastEquipment.getStackInSlot(slot);

			if (!stackSlot.isEmpty())
			{
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
			if (random.nextFloat() < 0.5F)
			{
				continue;
			}

			ItemStack stackSlot = inventoryChastMain.getStackInSlot(slot);

			if (!stackSlot.isEmpty())
			{
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
	private BlockPos getNearWaterPosition(Entity entity, int searchXYZ)
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
