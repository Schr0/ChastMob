package schr0.chastmob.packet.buttonedit;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import schr0.chastmob.gui.filteredit.ContainerFilterEdit;
import schr0.chastmob.inventory.InventoryFilter;
import schr0.chastmob.item.ItemFilter;

public class MessageHandlerButtonEdit implements IMessageHandler<MessageButtonEdit, IMessage>
{

	@Override
	public IMessage onMessage(MessageButtonEdit message, MessageContext ctx)
	{
		World world = ctx.getServerHandler().playerEntity.getEntityWorld();
		EntityPlayer entityPlayer = message.getEntityPlayer(world);

		if (entityPlayer != null)
		{
			ContainerFilterEdit containerFilterEdit = (ContainerFilterEdit) entityPlayer.openContainer;
			ItemStack stackMainhand = entityPlayer.getHeldItemMainhand();

			IInventory inventoryCraft = containerFilterEdit.getInventoryCraft();
			InventoryFilter inventoryFilter = new InventoryFilter(stackMainhand);

			for (int slot = 0; slot < inventoryCraft.getSizeInventory(); ++slot)
			{
				inventoryFilter.setInventorySlotContents(slot, inventoryCraft.getStackInSlot(slot));
			}

			((ItemFilter) stackMainhand.getItem()).setInventoryFilter(stackMainhand, inventoryFilter);;

			entityPlayer.closeScreen();
		}

		return (IMessage) null;
	}

}
