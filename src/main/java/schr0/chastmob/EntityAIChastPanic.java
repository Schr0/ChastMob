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
	private int panicTime;
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
		--this.panicTime;

		if (this.panicTime <= IDEL_TIME)
		{
			return;
		}

		if ((this.getAIOwnerEntity().getRNG().nextInt(10) == 0) && !this.getAIWorld().isRemote)
		{
			for (int slot = 0; slot < this.getAIOwnerInventory().getSizeInventory(); ++slot)
			{
				ItemStack stackInv = this.getAIOwnerInventory().getStackInSlot(slot);

				if (ChastMobHelper.isNotEmptyItemStack(stackInv))
				{
					if (this.getAIOwnerEntity().getRNG().nextInt(2) == 0)
					{
						continue;
					}

					ItemStack stackInvCopy = stackInv.copy();

					stackInvCopy.stackSize = 1;

					Block.spawnAsEntity(this.getAIWorld(), new BlockPos(this.getAIOwnerEntity()), stackInvCopy);

					--stackInv.stackSize;

					if (stackInv.stackSize <= 0)
					{
						this.getAIOwnerInventory().setInventorySlotContents(slot, null);
					}
					else
					{
						this.getAIOwnerInventory().setInventorySlotContents(slot, stackInv);
					}

					this.getAIOwnerEntity().playSound(SoundEvents.ENTITY_ITEM_PICKUP, 1.0F, 1.0F);

					break;
				}
			}
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
		return (0 < this.panicTime);
	}

	public void setPanicking(int panicTime)
	{
		this.panicTime = panicTime;
		this.randPosX = 0;
		this.randPosY = 0;
		this.randPosZ = 0;

		if (0 < panicTime)
		{
			this.panicTime += IDEL_TIME;
		}
	}

	@Nullable
	private BlockPos getNearWaterBlockPos(Entity owner, int searchXYZ)
	{
		BlockPos blockPos = new BlockPos(owner);
		int owerPosX = blockPos.getX();
		int owerPosY = blockPos.getY();
		int owerPosZ = blockPos.getZ();
		float rangeOrigin = (float) (searchXYZ * searchXYZ * searchXYZ * 2);
		BlockPos.MutableBlockPos blockPosMutable = new BlockPos.MutableBlockPos();
		BlockPos blockPosWater = null;

		for (int posX = (owerPosX - searchXYZ); posX <= (owerPosX + searchXYZ); ++posX)
		{
			for (int posY = (owerPosY - searchXYZ); posY <= (owerPosY + searchXYZ); ++posY)
			{
				for (int posZ = (owerPosZ - searchXYZ); posZ <= (owerPosZ + searchXYZ); ++posZ)
				{
					blockPosMutable.setPos(posX, posY, posZ);
					Block block = owner.worldObj.getBlockState(blockPosMutable).getBlock();

					if (block.equals(Blocks.WATER) || block.equals(Blocks.FLOWING_WATER))
					{
						float range = (float) ((posX - owerPosX) * (posX - owerPosX) + (posY - owerPosY) * (posY - owerPosY) + (posZ - owerPosZ) * (posZ - owerPosZ));

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
