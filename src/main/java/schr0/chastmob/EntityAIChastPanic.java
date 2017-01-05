package schr0.chastmob;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityAIChastPanic extends EntityAIChast
{

	private static final double MOVE_SPEED = 2.5D;
	private static final int IDEL_TIME = (3 * 20);

	private int panicTime;
	private double randPosX;
	private double randPosY;
	private double randPosZ;

	public EntityAIChastPanic(EntityChast entityChast)
	{
		super(entityChast);
		this.setMutexBits(1);
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
		this.getAIOwnerEntity().getNavigator().clearPathEntity();
		this.getAIOwnerEntity().setPanic(true);

		this.getAIOwnerEntity().setOpen(true);
	}

	@Override
	public void resetTask()
	{
		this.getAIOwnerEntity().getNavigator().clearPathEntity();
		this.getAIOwnerEntity().setPanic(false);

		this.getAIOwnerEntity().setOpen(false);
		this.setPanicking(0);
		this.setRandPos(0.0D, 0.0D, 0.0D);
	}

	@Override
	public void updateTask()
	{
		--this.panicTime;

		if (this.panicTime <= IDEL_TIME)
		{
			return;
		}

		if ((this.getAIOwnerEntity().getRNG().nextInt(20) == 0) && !this.getAIOwnerWorld().isRemote)
		{
			for (int slot = 0; slot < this.getAIOwnerInventory().getSizeInventory(); ++slot)
			{
				ItemStack stackInv = this.getAIOwnerInventory().getStackInSlot(slot);

				if (ChastMobVanillaHelper.isNotEmptyItemStack(stackInv))
				{
					ItemStack stackInvCopy = stackInv.copy();

					stackInvCopy.stackSize = 1;

					Block.spawnAsEntity(this.getAIOwnerWorld(), this.getAIOwnerBlockPos(), stackInvCopy);

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
			BlockPos blockPos = getNearBlockWaterBlockPos(this.getAIOwnerEntity());

			if (blockPos == null)
			{
				return;
			}
			else
			{
				this.setRandPos((double) blockPos.getX(), (double) blockPos.getY(), (double) blockPos.getZ());
			}
		}
		else
		{
			Vec3d vec3d = RandomPositionGenerator.findRandomTarget(this.getAIOwnerEntity(), 5, 4);

			if (vec3d == null)
			{
				return;
			}
			else
			{
				this.setRandPos(vec3d.xCoord, vec3d.yCoord, vec3d.zCoord);
			}
		}

		this.getAIOwnerEntity().getNavigator().tryMoveToXYZ(this.randPosX, this.randPosY, this.randPosZ, MOVE_SPEED);
	}

	// TODO /* ======================================== MOD START =====================================*/

	public boolean isPanicking()
	{
		return (0 < this.panicTime);
	}

	public void setPanicking(int panicTime)
	{
		this.panicTime = panicTime;

		if (0 < panicTime)
		{
			this.panicTime += IDEL_TIME;
		}
	}

	private void setRandPos(double x, double y, double z)
	{
		this.randPosX = x;
		this.randPosY = y;
		this.randPosZ = z;
	}

	private static BlockPos getNearBlockWaterBlockPos(Entity owner)
	{
		World world = owner.worldObj;
		int searchXZ = 5;
		int searchY = 4;
		BlockPos blockPos = new BlockPos(owner);
		BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
		int pX = blockPos.getX();
		int pY = blockPos.getY();
		int pZ = blockPos.getZ();
		float range = (float) (searchXZ * searchXZ * searchY * 2);
		BlockPos blockPosWater = null;

		for (int x = pX - searchXZ; x <= pX + searchXZ; ++x)
		{
			for (int y = pY - searchY; y <= pY + searchY; ++y)
			{
				for (int z = pZ - searchXZ; z <= pZ + searchXZ; ++z)
				{
					blockpos$mutableblockpos.setPos(x, y, z);
					IBlockState iblockstate = world.getBlockState(blockpos$mutableblockpos);
					Block block = iblockstate.getBlock();

					if (block == Blocks.WATER || block == Blocks.FLOWING_WATER)
					{
						float range1 = (float) ((x - pX) * (x - pX) + (y - pY) * (y - pY) + (z - pZ) * (z - pZ));

						if (range1 < range)
						{
							range = range1;
							blockPosWater = new BlockPos(blockpos$mutableblockpos);
						}
					}
				}
			}
		}

		return blockPosWater;
	}

}
