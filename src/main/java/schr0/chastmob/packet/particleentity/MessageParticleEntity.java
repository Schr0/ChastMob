package schr0.chastmob.packet.particleentity;

import javax.annotation.Nullable;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MessageParticleEntity implements IMessage
{

	private int entitiyID;
	private int particleType;

	public MessageParticleEntity()
	{
		// none
	}

	public MessageParticleEntity(Entity entity, int particleType)
	{
		this.entitiyID = entity.getEntityId();
		this.particleType = particleType;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.entitiyID = buf.readInt();
		this.particleType = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(this.entitiyID);
		buf.writeInt(this.particleType);
	}

	// TODO /* ======================================== MOD START =====================================*/

	@Nullable
	public Entity getEntity(World world)
	{
		return world.getEntityByID(this.entitiyID);
	}

	public int getParticleType()
	{
		return this.particleType;
	}

}
