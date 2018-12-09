package schr0.chastmob.entity.ai;

import javax.annotation.Nullable;

import net.minecraft.block.BlockChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import schr0.chastmob.entity.ChastMode;
import schr0.chastmob.entity.EntityChast;
import schr0.chastmob.inventory.InventoryChastHelper;
import schr0.chastmob.inventory.InventoryChastMain;

public class EntityAIChastStoreChest extends EntityAIChast
{

	private static final double STORE_RANGE = 2.5D;
	private TileEntityChest targetTEChest;

	public EntityAIChastStoreChest(EntityChast entityChast)
	{
		super(entityChast);
		this.targetTEChest = null;
	}

	@Override
	public boolean shouldExecute()
	{
		if (this.getMode() == ChastMode.PATROL)
		{
			this.targetTEChest = null;

			if (InventoryChastHelper.canStoreInventory(this.getEntity().getInventoryMain(), ItemStack.EMPTY))
			{
				return false;
			}

			TileEntityChest homeChest = this.getEntity().getCanSeeHomeChest(true);

			if (homeChest != null)
			{
				InventoryChastMain inventoryMain = this.getEntity().getInventoryMain();
				IInventory inventory = (IInventory) homeChest;

				for (int slot = 0; slot < inventoryMain.getSizeInventory(); ++slot)
				{
					ItemStack stackSlot = inventoryMain.getStackInSlot(slot);

					if (InventoryChastHelper.canStoreInventory(inventory, stackSlot))
					{
						this.targetTEChest = homeChest;

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

		return (this.targetTEChest != null);
	}

	@Override
	public void resetTask()
	{
		super.resetTask();

		this.targetTEChest = null;
	}

	@Override
	public void updateTask()
	{
		super.updateTask();

		BlockPos targetPos = this.targetTEChest.getPos();

		this.getEntity().getLookHelper().setLookPosition(targetPos.getX(), targetPos.getY(), targetPos.getZ(), 10.0F, this.getEntity().getVerticalFaceSpeed());

		if (this.getEntity().getDistanceSqToCenter(targetPos) < STORE_RANGE)
		{
			TileEntityChest nearOpenChest = this.getNearOpenChest(this.getEntity(), this.getRange());

			if ((nearOpenChest != null) && (nearOpenChest == this.targetTEChest))
			{
				InventoryChastMain inventoryMain = this.getEntity().getInventoryMain();
				IInventory inventory = (IInventory) nearOpenChest;

				for (int slot = 0; slot < inventoryMain.getSizeInventory(); ++slot)
				{
					ItemStack stackSlot = inventoryMain.getStackInSlot(slot);

					if (!stackSlot.isEmpty())
					{
						if (!this.getEntity().isCoverOpen())
						{
							this.getEntity().setCoverOpen(true);

							return;
						}

						inventoryMain.setInventorySlotContents(slot, TileEntityHopper.putStackInInventoryAllSlots((IInventory) null, (IInventory) nearOpenChest, stackSlot, EnumFacing.UP));
					}
				}

				this.targetTEChest = null;

				return;
			}
		}
		else
		{
			this.getEntity().getNavigator().tryMoveToXYZ(targetPos.getX(), targetPos.getY(), targetPos.getZ(), this.getSpeed());
		}
	}

	// TODO /* ======================================== MOD START =====================================*/

	private boolean canStoreChest(@Nullable TileEntityChest tileEntityChest)
	{
		if (tileEntityChest != null)
		{
			return this.getEntity().canBlockBeSeen(tileEntityChest.getPos());
		}

		return false;
	}

	private TileEntityChest getNearOpenChest(EntityChast entityChast, int searchXYZ)
	{
		World world = entityChast.getEntityWorld();
		BlockPos blockPos = entityChast.getPosition();
		int posX = blockPos.getX();
		int posY = blockPos.getY();
		int posZ = blockPos.getZ();
		float rangeOrigin = (float) (searchXYZ * searchXYZ * searchXYZ * 2);

		BlockPos.MutableBlockPos posMutable = new BlockPos.MutableBlockPos();
		TileEntityChest tileEntityChest = null;

		for (int x = (posX - searchXYZ); x <= (posX + searchXYZ); ++x)
		{
			for (int y = (posY - searchXYZ); y <= (posY + searchXYZ); ++y)
			{
				for (int z = (posZ - searchXYZ); z <= (posZ + searchXYZ); ++z)
				{
					posMutable.setPos(x, y, z);

					TileEntity tileEntity = world.getTileEntity(posMutable);

					if (tileEntity instanceof TileEntityChest)
					{
						boolean isLockedChest = (((BlockChest) world.getBlockState(posMutable).getBlock()).getLockableContainer(world, posMutable) == null);

						if (isLockedChest)
						{
							continue;
						}

						float range = (float) ((x - posX) * (x - posX) + (y - posY) * (y - posY) + (z - posZ) * (z - posZ));

						if (range < rangeOrigin)
						{
							rangeOrigin = range;

							for (int slot = 0; slot < entityChast.getInventoryMain().getSizeInventory(); ++slot)
							{
								ItemStack stackSlot = entityChast.getInventoryMain().getStackInSlot(slot);

								if (InventoryChastHelper.canStoreInventory((IInventory) tileEntity, stackSlot))
								{
									tileEntityChest = (TileEntityChest) tileEntity;

									break;
								}
							}
						}
					}
				}
			}
		}

		return tileEntityChest;
	}

}
