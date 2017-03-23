package schr0.chastmob.packet.buttonchange;

import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import schr0.chastmob.entity.EntityChast;

public class MessageHandlerButtonChange implements IMessageHandler<MessageButtonChange, IMessage>
{

	@Override
	public IMessage onMessage(MessageButtonChange message, MessageContext ctx)
	{
		World world = ctx.getServerHandler().playerEntity.getEntityWorld();
		EntityChast entityChast = message.getEntityChast(world);

		if (entityChast != null)
		{
			entityChast.setOwnerFollow(!entityChast.isOwnerFollow());
		}

		return (IMessage) null;
	}

}
