package schr0.chastmob.proxy;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import schr0.chastmob.ChastMobEntitys;
import schr0.chastmob.ChastMobItems;

@SideOnly(Side.CLIENT)
public class ProxyClient extends ProxyServer
{

	@Override
	public void preInitEventProxy(FMLPreInitializationEvent event)
	{
		(new ChastMobItems()).initClient();

		(new ChastMobEntitys()).initClient();
	}

	@Override
	public void initEventProxy(FMLInitializationEvent event)
	{
		// none
	}

	@Override
	public void postInitEventProxy(FMLPostInitializationEvent event)
	{
		// none
	}

}
