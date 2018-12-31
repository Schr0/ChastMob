package schr0.chastmob.util;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import schr0.chastmob.init.ChastMobMessages;
import schr0.chastmob.packet.MessageParticleEntity;

@SideOnly(Side.CLIENT)
public class ChastMobParticles
{
	public static final int HEART = 0;
	public static final int DEFENSE = 1;
	public static final int NOTE = 2;

	public static void spawnParticleHeart(Entity target)
	{
		ChastMobMessages.DISPATCHER.sendToAll(new MessageParticleEntity(target, ChastMobParticles.HEART));
	}

	public static void spawnParticleDefense(Entity target)
	{
		ChastMobMessages.DISPATCHER.sendToAll(new MessageParticleEntity(target, ChastMobParticles.DEFENSE));
	}

	public static void spawnParticleNote(Entity target)
	{
		ChastMobMessages.DISPATCHER.sendToAll(new MessageParticleEntity(target, ChastMobParticles.NOTE));
	}

}
