package schr0.chastmob;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class EntityAIChastPanic extends EntityAIChast
{

	private static final int IDEL_TIME = (3 * 20);

	private double moveSpeed;
	private int maxDistance;
	private int timeCounter;
	private double randPosX;
	private double randPosY;
	private double randPosZ;

	public EntityAIChastPanic(EntityChast entityChast, double speed, int distance)
	{
		super(entityChast);
		this.setMutexBits(1);

		this.moveSpeed = speed;
		this.maxDistance = distance;
	}

	@Override
	public boolean shouldExecute()
	{
		if (this.isPanicking())
		{
			return true;
		}

		return false;
	}

	@Override
	public void startExecuting()
	{
		super.startExecuting();
		this.getAIOwnerEntity().setStatePanic(true);

		this.getAIOwnerEntity().setCoverOpen(true);
	}

	@Override
	public void resetTask()
	{
		super.resetTask();
		this.getAIOwnerEntity().setStatePanic(false);

		this.getAIOwnerEntity().setCoverOpen(false);
		this.setPanicking(0);
	}

	@Override
	public void updateTask()
	{
		--this.timeCounter;

		if (this.timeCounter <= IDEL_TIME)
		{
			return;
		}

		if ((this.getAIOwnerEntity().getRNG().nextInt(10) == 0) && !this.getAIOwnerWorld().isRemote)
		{
			this.onPanicDropItem(this.getAIOwnerEntity());
		}

		if (this.getAIOwnerEntity().isBurning())
		{
			BlockPos blockPos = this.getNearWaterBlockPos(this.getAIOwnerEntity(), this.maxDistance);

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
			Vec3d vec3d = RandomPositionGenerator.findRandomTarget(this.getAIOwnerEntity(), this.maxDistance, this.maxDistance);

			if (vec3d == null)
			{
				return;
			}
			else
			{
				this.randPosX = vec3d.xCoord;
				this.randPosY = vec3d.yCoord;
				this.randPosZ = vec3d.zCoord;
			}
		}

		this.getAIOwnerEntity().getNavigator().tryMoveToXYZ(this.randPosX, this.randPosY, this.randPosZ, this.moveSpeed);
	}

	// TODO /* ======================================== MOD START =====================================*/

	public boolean isPanicking()
	{
		return (0 < this.timeCounter);
	}

	public void setPanicking(int timeCounter)
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
		InventoryChast inventoryChast = entityChast.getInventoryChast();

		for (int slot = 0; slot < inventoryChast.getSizeInventory(); ++slot)
		{
			ItemStack stackInv = inventoryChast.getStackInSlot(slot);

			if (ChastMobHelper.isNotEmptyItemStack(stackInv))
			{
				if (entityChast.getRNG().nextInt(2) == 0)
				{
					continue;
				}

				ItemStack stackInvCopy = stackInv.copy();

				stackInvCopy.stackSize = 1;

				Block.spawnAsEntity(entityChast.getEntityWorld(), entityChast.getPosition(), stackInvCopy);

				--stackInv.stackSize;

				if (stackInv.stackSize <= 0)
				{
					inventoryChast.setInventorySlotContents(slot, ChastMobHelper.getEmptyItemStack());
				}
				else
				{
					inventoryChast.setInventorySlotContents(slot, stackInv);
				}

				entityChast.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 1.0F, 1.0F);

				break;
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
					Block block = entity.worldObj.getBlockState(blockPosMutable).getBlock();

					if (block.equals(Blocks.WATER) || block.equals(Blocks.FLOWING_WATER))
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
