package schr0.chastmob.packet.guifilteredit;

import javax.annotation.Nullable;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageGuiFilterEdit implements IMessage
{

	private int entitiyID;

	public MessageGuiFilterEdit()
	{
		// none
	}

	public MessageGuiFilterEdit(Entity entity)
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
	public EntityPlayer getEntityPlayer(World world)
	{
		Entity entity = world.getEntityByID(this.entitiyID);

		if (entity instanceof EntityPlayer)
		{
			return (EntityPlayer) entity;
		}

		return (EntityPlayer) null;
	}

}
