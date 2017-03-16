package schr0.chastmob.packet.particleentity;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MessageHandlerParticleEntity implements IMessageHandler<MessageParticleEntity, IMessage>
{

	@Override
	public IMessage onMessage(MessageParticleEntity message, MessageContext ctx)
	{
		World world = FMLClientHandler.instance().getClient().world;
		Entity entity = message.getEntity(world);

		if (entity != null)
		{
			switch (message.getParticleType())
			{
				case 0 :

					particleHeart(world, world.rand, entity);

					break;
			}
		}

		return (IMessage) null;
	}

	// TODO /* ======================================== MOD START =====================================*/

	private static void particleHeart(World world, Random random, Entity entity)
	{
		for (int i = 0; i < 7; ++i)
		{
			double randX = random.nextGaussian() * 0.02D;
			double randY = random.nextGaussian() * 0.02D;
			double randZ = random.nextGaussian() * 0.02D;
			world.spawnParticle(EnumParticleTypes.HEART, entity.posX + (double) (random.nextFloat() * entity.width * 2.0F) - (double) entity.width, entity.posY + 0.5D + (double) (random.nextFloat() * entity.height), entity.posZ + (double) (random.nextFloat() * entity.width * 2.0F) - (double) entity.width, randX, randY, randZ, new int[0]);
		}
	}

}
