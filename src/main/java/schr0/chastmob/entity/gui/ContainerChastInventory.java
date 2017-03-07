package schr0.chastmob.entity.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import schr0.chastmob.entity.EntityChast;

public class ContainerChastInventory extends Container
{
	private EntityChast theChast;
	private EntityPlayer thePlayer;

	public ContainerChastInventory(EntityChast entityChast, EntityPlayer entityPlayer)
	{
		this.theChast = entityChast;
		this.thePlayer = entityPlayer;

		this.theChast.getInventoryChast().openInventory(this.thePlayer);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn)
	{
		return this.theChast.getInventoryChast().isUseableByPlayer(playerIn);
	}

	@Override
	public void onContainerClosed(EntityPlayer playerIn)
	{
		this.theChast.getInventoryChast().closeInventory(playerIn);

		super.onContainerClosed(playerIn);
	}

}
