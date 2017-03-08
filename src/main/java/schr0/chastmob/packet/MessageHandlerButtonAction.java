package schr0.chastmob.packet;

import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import schr0.chastmob.entity.EntityChast;

public class MessageHandlerButtonAction implements IMessageHandler<MessageButtonAction, IMessage>
{

	@Override
	public IMessage onMessage(MessageButtonAction message, MessageContext ctx)
	{
		World world = ctx.getServerHandler().playerEntity.getEntityWorld();
		EntityChast entityChast = message.getEntityChast(world);

		if (entityChast != null)
		{
			entityChast.changeAIState();
		}

		return (IMessage) null;
	}

}
