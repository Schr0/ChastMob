package schr0.chastmob.proxy;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import schr0.chastmob.ChastMobEntitys;

@SideOnly(Side.CLIENT)
public class ProxyClient extends ProxyServer
{

	@Override
	public void preInitEventProxy(FMLPreInitializationEvent event)
	{
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

	@Override
	public Minecraft getMinecraft()
	{
		return FMLClientHandler.instance().getClient();
	}

	@Override
	public void infoModLog(String format, Object... data)
	{
		FMLLog.info(format, data);
	}

}
