package schr0.chastmob;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class EntityAIChast extends EntityAIBase
{

	private EntityChast theChast;

	public EntityAIChast(EntityChast entityChast)
	{
		this.theChast = entityChast;
	}

	@Override
	public void startExecuting()
	{
		this.theChast.getNavigator().clearPathEntity();
	}

	@Override
	public void resetTask()
	{
		this.theChast.getNavigator().clearPathEntity();
	}

	// TODO /* ======================================== MOD START =====================================*/

	public EntityChast getAIOwnerEntity()
	{
		return this.theChast;
	}

	public InventoryChast getAIOwnerInventory()
	{
		return this.theChast.getInventoryChast();
	}

	public BlockPos getAIOwnerPosition()
	{
		BlockPos blockPos = this.theChast.getPosition();

		if (this.theChast.isOwnerFollow())
		{
			EntityLivingBase entityLivingBase = this.theChast.getOwnerEntity();

			if (this.theChast.isOwnerTame() && (entityLivingBase != null))
			{
				blockPos = entityLivingBase.getPosition();
			}
		}
		else
		{
			ItemStack stackMain = this.theChast.getHeldItem(EnumHand.MAIN_HAND);

			if (ChastMobHelper.isNotEmptyItemStack(stackMain) && (stackMain.getItem() instanceof ItemHomeChestMap))
			{
				BlockPos blockPosHome = ((ItemHomeChestMap) stackMain.getItem()).getHomeChestBlockPos(stackMain);

				if (blockPosHome != null)
				{
					blockPos = blockPosHome;
				}
			}
		}

		return blockPos;
	}

	public World getAIOwnerWorld()
	{
		return this.theChast.getEntityWorld();
	}

	public boolean isEmptyBlock(BlockPos pos)
	{
		IBlockState state = this.getAIOwnerWorld().getBlockState(pos);

		return state.getMaterial().equals(Material.AIR) ? true : !state.isFullCube();
	}

}
