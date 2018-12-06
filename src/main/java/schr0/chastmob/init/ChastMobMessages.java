package schr0.chastmob.init;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import schr0.chastmob.ChastMob;
import schr0.chastmob.packet.MessageGuiChastInventory;
import schr0.chastmob.packet.MessageHandlerGuiChastInventory;
import schr0.chastmob.packet.MessageHandlerParticleEntity;
import schr0.chastmob.packet.MessageParticleEntity;

public class ChastMobMessages
{

	public static final String CHANNEL_NAME = ChastMob.MOD_ID;
	public static final SimpleNetworkWrapper DISPATCHER = NetworkRegistry.INSTANCE.newSimpleChannel(CHANNEL_NAME);

	public static final int ID_PARTICLE_ENTITY = 0;
	public static final int ID_GUI_BUTTON_CHANGE = 1;
	public static final int ID_GUI_BUTTON_EDIT = 2;

	public void register()
	{
		DISPATCHER.registerMessage(MessageHandlerGuiChastInventory.class, MessageGuiChastInventory.class, ID_GUI_BUTTON_CHANGE, Side.SERVER);
	}

	@SideOnly(Side.CLIENT)
	public void registerClient()
	{
		DISPATCHER.registerMessage(MessageHandlerParticleEntity.class, MessageParticleEntity.class, ID_PARTICLE_ENTITY, Side.CLIENT);
	}

}
