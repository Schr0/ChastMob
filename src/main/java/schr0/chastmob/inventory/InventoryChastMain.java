package schr0.chastmob.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import schr0.chastmob.entity.EntityChast;

public class InventoryChastMain extends InventoryChast
{

	public InventoryChastMain(EntityChast entityChast)
	{
		super(entityChast, (9 * 3));
	}

	@Override
	public void openInventory(EntityPlayer player)
	{
		super.markDirty();

		this.getContainerEntityChast().setTradeAI(player);

		this.getContainerEntityChast().playSound(SoundEvents.BLOCK_CHEST_OPEN, 0.5F, this.getContainerEntityChast().getRNG().nextFloat() * 0.1F + 0.9F);
	}

	@Override
	public void closeInventory(EntityPlayer player)
	{
		super.markDirty();

		this.getContainerEntityChast().setTradeAI(null);

		this.getContainerEntityChast().playSound(SoundEvents.BLOCK_CHEST_CLOSE, 0.5F, this.getContainerEntityChast().getRNG().nextFloat() * 0.1F + 0.9F);
	}

}
