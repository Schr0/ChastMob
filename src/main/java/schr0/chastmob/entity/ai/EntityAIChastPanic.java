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

public class EntityAIChastPanic extends EntityAIChast
{

	private static final int IDLE_TIME = (2 * 20);
	private double randPosX;
	private double randPosY;
	private double randPosZ;

	public EntityAIChastPanic(EntityChast entityChast)
	{
		super(entityChast);
	}

	@Override
	public boolean shouldExecute()
	{
		return this.getEntity().isPanic();
	}

	@Override
	public void updateTask()
	{
		super.updateTask();

		this.getEntity().shrinkPanicTime(1);

		this.getEntity().setCoverOpen(true);

		if (this.isIdleTime())
		{
			return;
		}

		if (this.getEntity().getRNG().nextFloat() < 0.50F)
		{
			this.onPanicDropItems(this.getEntity());
		}

		if (this.getEntity().isBurning())
		{
			BlockPos blockPos = this.getNearWaterPosition(this.getEntity(), this.getSearchRange());

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
			Vec3d vec3d = RandomPositionGenerator.findRandomTarget(this.getEntity(), this.getSearchRange(), this.getSearchRange());

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

		this.getEntity().getNavigator().tryMoveToXYZ(this.randPosX, this.randPosY, this.randPosZ, this.getMoveSpeed());
	}

	@Override
	public double getMoveSpeed()
	{
		return (super.getMoveSpeed() * 2);
	}

	// TODO /* ======================================== MOD START =====================================*/

	private boolean isIdleTime()
	{
		return (this.getEntity().getPanicTime() < IDLE_TIME);
	}

	private void onPanicDropItems(EntityChast entityChast)
	{
		InventoryChastEquipments inventoryChastEquipment = entityChast.getInventoryEquipments();
		Random random = this.getRandom();

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
		BlockPos blockPosWater = null;
		BlockPos blockPos = new BlockPos(entity);
		int entityPosX = blockPos.getX();
		int entityPosY = blockPos.getY();
		int entityPosZ = blockPos.getZ();
		float rangeOrigin = 0.0F;
		BlockPos.MutableBlockPos blockPosMutable = new BlockPos.MutableBlockPos();

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

						if ((range < rangeOrigin) || (rangeOrigin == 0.0F))
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
