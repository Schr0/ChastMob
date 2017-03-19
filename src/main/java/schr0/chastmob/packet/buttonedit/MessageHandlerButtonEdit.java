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

			IInventory inventoryCraft = containerFilterEdit.getInventoryEdit();
			InventoryFilter inventoryFilter = new InventoryFilter(stackMainhand);

			for (int slot = 0; slot < inventoryCraft.getSizeInventory(); ++slot)
			{
				ItemStack stackSlot = inventoryCraft.getStackInSlot(slot);
				int meta = stackSlot.isItemStackDamageable() ? (0) : stackSlot.getItemDamage();

				stackSlot.setItemDamage(meta);

				inventoryFilter.addItem(stackSlot);
			}

			for (int countCheck = 0; countCheck < inventoryFilter.getSizeInventory(); ++countCheck)
			{
				inventoryFilter.getStackInSlot(countCheck).setCount(1);
			}

			inventoryFilter.closeInventory(entityPlayer);

			entityPlayer.closeScreen();
		}

		return (IMessage) null;
	}

}
