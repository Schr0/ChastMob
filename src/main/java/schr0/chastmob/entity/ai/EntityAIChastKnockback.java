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
import schr0.chastmob.ChastMobHelper;
import schr0.chastmob.entity.EntityChast;
import schr0.chastmob.inventory.InventoryChastEquipment;
import schr0.chastmob.inventory.InventoryChastMain;

public class EntityAIChastKnockback extends EntityAIChast
{

	private static final int IDEL_TIME = (3 * 20);
	private int timeCounter;

	private double speed;
	private int distance;
	private double randPosX;
	private double randPosY;
	private double randPosZ;

	public EntityAIChastKnockback(EntityChast entityChast, double speed, int distance)
	{
		super(entityChast);

		this.speed = speed;
		this.distance = distance;
	}

	@Override
	public boolean shouldExecute()
	{
		if (this.isKnockbacking())
		{
			return true;
		}

		return false;
	}

	@Override
	public void startExecuting()
	{
		super.startExecuting();

		this.getOwnerEntity().setStateKnockback(true);
	}

	@Override
	public void resetTask()
	{
		super.resetTask();

		this.getOwnerEntity().setStateKnockback(false);
		this.getOwnerEntity().setCoverOpen(false);
		this.setKnockbacking(0);
	}

	@Override
	public void updateTask()
	{
		--this.timeCounter;

		if (this.timeCounter <= IDEL_TIME)
		{
			return;
		}

		if (this.getOwnerEntity().isEquipHelmet())
		{
			// none
		}
		else
		{
			this.getOwnerEntity().setCoverOpen(true);

			if ((this.getOwnerEntity().getRNG().nextInt(10) == 0) && !this.getOwnerWorld().isRemote)
			{
				this.onPanicDropItem(this.getOwnerEntity());
			}

			if (this.getOwnerEntity().isBurning())
			{
				BlockPos blockPos = this.getNearWaterBlockPos(this.getOwnerEntity(), this.distance);

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
				Vec3d vec3d = RandomPositionGenerator.findRandomTarget(this.getOwnerEntity(), this.distance, this.distance);

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

			this.getOwnerEntity().getNavigator().tryMoveToXYZ(this.randPosX, this.randPosY, this.randPosZ, this.speed);
		}
	}

	// TODO /* ======================================== MOD START =====================================*/

	public boolean isKnockbacking()
	{
		return (0 < this.timeCounter);
	}

	public void setKnockbacking(int timeCounter)
	{
		if (0 < timeCounter)
		{
			int sec = timeCounter;
			sec = Math.max(4, sec);
			sec = Math.min(8, sec);

			this.timeCounter = (sec * 20) + IDEL_TIME;
		}
		else
		{
			this.timeCounter = 0;
		}

		this.randPosX = 0;
		this.randPosY = 0;
		this.randPosZ = 0;
	}

	private void onPanicDropItem(EntityChast entityChast)
	{
		InventoryChastEquipment inventoryChastEquipment = entityChast.getInventoryChastEquipment();

		for (int slot = 0; slot < inventoryChastEquipment.getSizeInventory(); ++slot)
		{
			if ((slot == 0) || (slot == 3))
			{
				continue;
			}

			ItemStack stackSlot = inventoryChastEquipment.getStackInSlot(slot);

			if (ChastMobHelper.isNotEmptyItemStack(stackSlot))
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

		InventoryChastMain inventoryChastMain = entityChast.getInventoryChastMain();

		for (int slot = 0; slot < inventoryChastMain.getSizeInventory(); ++slot)
		{
			ItemStack stackSlot = inventoryChastMain.getStackInSlot(slot);

			if (ChastMobHelper.isNotEmptyItemStack(stackSlot))
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
