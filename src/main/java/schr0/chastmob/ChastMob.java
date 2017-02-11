package schr0.chastmob;

import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import schr0.chastmob.init.ChastMobEntitys;
import schr0.chastmob.init.ChastMobEvent;
import schr0.chastmob.init.ChastMobItems;
import schr0.chastmob.init.ChastMobRecipe;
import schr0.chastmob.proxy.ProxyServer;

@Mod(modid = ChastMob.MOD_ID, name = ChastMob.MOD_NAME, version = ChastMob.MOD_VERSION, dependencies = ChastMob.MOD_DEPENDENCIES)
public class ChastMob
{

	/**
	 * ModのID.
	 */
	public static final String MOD_ID = "schr0chastmob";

	/**
	 * Modの名前.
	 */
	public static final String MOD_NAME = "ChastMob";

	/**
	 * Modのバージョン.
	 */
	public static final String MOD_VERSION = "1.0.0";

	/**
	 * Forgeのバージョン.
	 */
	public static final String MOD_DEPENDENCIES = "required-after:Forge@[1.10.2-12.18.3.2185,)";

	/**
	 * ResourceLocationのDomain.
	 */
	public static final String MOD_RESOURCE_DOMAIN = MOD_ID + ":";

	@Mod.Instance(ChastMob.MOD_ID)
	public static ChastMob instance;

	@SidedProxy(clientSide = "schr0.chastmob.proxy.ProxyClient", serverSide = "schr0.chastmob.proxy.ProxyServer")
	public static ProxyServer proxy;

	/**
	 * Modの事前・初期設定時イベント.
	 */
	@Mod.EventHandler
	public void preInitEvent(FMLPreInitializationEvent event)
	{
		(new ChastMobItems()).init();

		(new ChastMobEntitys()).init();

		this.proxy.preInitEventProxy(event);
	}

	/**
	 * Modの事中・初期設定時イベント.
	 */
	@Mod.EventHandler
	public void initEvent(FMLInitializationEvent event)
	{
		(new ChastMobRecipe()).init();

		(new ChastMobEvent()).init();

		this.proxy.initEventProxy(event);
	}

	/**
	 * Modの事後・初期設定時イベント.
	 */
	@Mod.EventHandler
	public void postInitEvent(FMLPostInitializationEvent event)
	{
		this.proxy.postInitEventProxy(event);
	}

	// TODO /* ======================================== MOD START =====================================*/

	public void logInfo(String format, Object... data)
	{
		FMLLog.info(format, data);
	}

}
