package schr0.chastmob.init;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import schr0.chastmob.ChastMob;

public class ChastMobGui
{

	public static final int ID_CHAST_INVENTORY = 0;
	public static final int ID_FILTER_EDIT = 1;

	public void init()
	{
		NetworkRegistry.INSTANCE.registerGuiHandler(ChastMob.instance, new ChastMobGuiHandler());
	}

}
