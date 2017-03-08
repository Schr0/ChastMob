package schr0.chastmob.packet;

import javax.annotation.Nullable;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import schr0.chastmob.entity.EntityChast;

public class MessageButtonAction implements IMessage
{

	private int entitiyID;

	public MessageButtonAction()
	{
		// none
	}

	public MessageButtonAction(Entity entity)
	{
		this.entitiyID = entity.getEntityId();
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.entitiyID = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(this.entitiyID);
	}

	// TODO /* ======================================== MOD START =====================================*/

	@Nullable
	public EntityChast getEntityChast(World world)
	{
		Entity entity = world.getEntityByID(this.entitiyID);

		if (entity instanceof EntityChast)
		{
			return (EntityChast) entity;
		}

		return (EntityChast) null;
	}

}
