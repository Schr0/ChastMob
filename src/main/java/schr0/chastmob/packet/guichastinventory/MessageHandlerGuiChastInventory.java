package schr0.chastmob.packet.guichastinventory;

import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import schr0.chastmob.entity.EntityChast;

public class MessageHandlerGuiChastInventory implements IMessageHandler<MessageGuiChastInventory, IMessage>
{

	@Override
	public IMessage onMessage(MessageGuiChastInventory message, MessageContext ctx)
	{
		World world = ctx.getServerHandler().player.getEntityWorld();
		EntityChast entityChast = message.getEntityChast(world);

		if (entityChast != null)
		{
			entityChast.setOwnerFollow(!entityChast.isOwnerFollow());
		}

		return (IMessage) null;
	}

}
