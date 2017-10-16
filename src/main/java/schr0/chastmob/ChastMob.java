package schr0.chastmob;

import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import schr0.chastmob.init.ChastMobEntitys;
import schr0.chastmob.init.ChastMobEvent;
import schr0.chastmob.init.ChastMobGui;
import schr0.chastmob.init.ChastMobItems;
import schr0.chastmob.init.ChastMobPacket;
import schr0.chastmob.init.ChastMobRecipe;

@Mod(modid = ChastMob.MOD_ID, name = ChastMob.MOD_NAME, version = ChastMob.MOD_VERSION)
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
	public static final String MOD_VERSION = "2.1.3";

	/**
	 * ResourceLocationのDomain.
	 */
	public static final String MOD_RESOURCE_DOMAIN = MOD_ID + ":";

	@Mod.Instance(ChastMob.MOD_ID)
	public static ChastMob instance;

	/**
	 * 初期・設定イベント.
	 */
	@Mod.EventHandler
	public void construction(FMLConstructionEvent event)
	{
		MinecraftForge.EVENT_BUS.register(this);

		if (event.getSide().isClient())
		{
			(new ChastMobEntitys()).registerRenders();
		}
	}

	/**
	 * 事前・設定イベント.
	 */
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		// none

		if (event.getSide().isClient())
		{
			// none
		}
	}

	/**
	 * 事中・設定イベント.
	 */
	@Mod.EventHandler
	public void init(FMLInitializationEvent event)
	{
		(new ChastMobPacket()).registerMessages();

		(new ChastMobGui()).registerGuis();

		(new ChastMobEvent()).registerEvents();

		if (event.getSide().isClient())
		{
			(new ChastMobPacket()).registerClientMessages();
		}
	}

	/**
	 * 事後・設定イベント.
	 */
	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		// none

		if (event.getSide().isClient())
		{
			// none
		}
	}

	// TODO /* ======================================== MOD START =====================================*/

	/**
	 * Itemの登録.
	 */
	@SubscribeEvent
	public void registerItems(RegistryEvent.Register<Item> event)
	{
		IForgeRegistry<Item> registry = event.getRegistry();

		(new ChastMobItems()).registerItems(registry);
	}

	/**
	 * Item / Blockモデルの登録.
	 */
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void registerModels(ModelRegistryEvent event)
	{
		(new ChastMobItems()).registerModels();
	}

	/**
	 * Entityの登録.
	 */
	@SubscribeEvent
	public void registerEntitys(RegistryEvent.Register<EntityEntry> event)
	{
		(new ChastMobEntitys()).registerEntitys();
	}

	/**
	 * Recipeの登録.
	 */
	@SubscribeEvent
	public void registerRecipes(RegistryEvent.Register<IRecipe> event)
	{
		IForgeRegistry<IRecipe> registry = event.getRegistry();

		(new ChastMobRecipe()).registerRecipes(registry);
	}

}
