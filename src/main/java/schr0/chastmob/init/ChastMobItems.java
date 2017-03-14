package schr0.chastmob.init;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import schr0.chastmob.ChastMob;
import schr0.chastmob.item.ItemCore;
import schr0.chastmob.item.ItemHelmetDiamond;
import schr0.chastmob.item.ItemHelmetGold;
import schr0.chastmob.item.ItemHelmetIron;
import schr0.chastmob.item.ItemHelmetStone;
import schr0.chastmob.item.ItemHelmetWood;
import schr0.chastmob.item.ItemModePatrol;
import schr0.chastmob.item.ItemSoulBottle;
import schr0.chastmob.item.ItemSoulBottleFull;
import schr0.chastmob.item.ItemSoulFragment;

public class ChastMobItems
{

	public static final Item SOUL_BOTTLE;
	public static final Item SOUL_BOTTLE_FULL;
	public static final Item SOUL_FRAGMENT;
	public static final Item CORE;
	public static final Item MODE_PATROL;
	public static final Item HELMET_WOOD;
	public static final Item HELMET_STONE;
	public static final Item HELMET_IRON;
	public static final Item HELMET_DIAMOND;
	public static final Item HELMET_GOLD;

	public static final String NAME_SOUL_BOTTLE = "soul_bottle";
	public static final String NAME_SOUL_BOTTLE_FULL = "soul_bottle_full";
	public static final String NAME_SOUL_FRAGMENT = "soul_fragment";
	public static final String NAME_CORE = "chast_core";
	public static final String NAME_MODE_PATROL = "mode_patrol";
	public static final String NAME_HELMET_WOOD = "chast_helmet_wood";
	public static final String NAME_HELMET_STONE = "chast_helmet_stone";
	public static final String NAME_HELMET_IRON = "chast_helmet_iron";
	public static final String NAME_HELMET_DIAMOND = "chast_helmet_diamond";
	public static final String NAME_HELMET_GOLD = "chast_helmet_gold";

	public static final int META_DEFAULT = 0;

	static
	{
		SOUL_BOTTLE = new ItemSoulBottle().setUnlocalizedName(NAME_SOUL_BOTTLE).setCreativeTab(ChastMobCreativeTabs.ITEM);
		SOUL_BOTTLE_FULL = new ItemSoulBottleFull().setUnlocalizedName(NAME_SOUL_BOTTLE_FULL).setCreativeTab(ChastMobCreativeTabs.ITEM);
		SOUL_FRAGMENT = new ItemSoulFragment().setUnlocalizedName(NAME_SOUL_FRAGMENT).setCreativeTab(ChastMobCreativeTabs.ITEM);
		CORE = new ItemCore().setUnlocalizedName(NAME_CORE).setCreativeTab(ChastMobCreativeTabs.ITEM);
		MODE_PATROL = new ItemModePatrol().setUnlocalizedName(NAME_MODE_PATROL).setCreativeTab(ChastMobCreativeTabs.ITEM);
		HELMET_WOOD = new ItemHelmetWood().setUnlocalizedName(NAME_HELMET_WOOD).setCreativeTab(ChastMobCreativeTabs.ITEM);
		HELMET_STONE = new ItemHelmetStone().setUnlocalizedName(NAME_HELMET_STONE).setCreativeTab(ChastMobCreativeTabs.ITEM);
		HELMET_IRON = new ItemHelmetIron().setUnlocalizedName(NAME_HELMET_IRON).setCreativeTab(ChastMobCreativeTabs.ITEM);
		HELMET_DIAMOND = new ItemHelmetDiamond().setUnlocalizedName(NAME_HELMET_DIAMOND).setCreativeTab(ChastMobCreativeTabs.ITEM);
		HELMET_GOLD = new ItemHelmetGold().setUnlocalizedName(NAME_HELMET_GOLD).setCreativeTab(ChastMobCreativeTabs.ITEM);
	}

	public void init()
	{
		registerItem(SOUL_BOTTLE, NAME_SOUL_BOTTLE, META_DEFAULT);
		registerItem(SOUL_BOTTLE_FULL, NAME_SOUL_BOTTLE_FULL, META_DEFAULT);
		registerItem(SOUL_FRAGMENT, NAME_SOUL_FRAGMENT, META_DEFAULT);
		registerItem(CORE, NAME_CORE, META_DEFAULT);
		registerItem(MODE_PATROL, NAME_MODE_PATROL, META_DEFAULT);
		registerItem(HELMET_WOOD, NAME_HELMET_WOOD, META_DEFAULT);
		registerItem(HELMET_STONE, NAME_HELMET_STONE, META_DEFAULT);
		registerItem(HELMET_IRON, NAME_HELMET_IRON, META_DEFAULT);
		registerItem(HELMET_DIAMOND, NAME_HELMET_DIAMOND, META_DEFAULT);
		registerItem(HELMET_GOLD, NAME_HELMET_GOLD, META_DEFAULT);
	}

	@SideOnly(Side.CLIENT)
	public void initClient()
	{
		ChastMobModelLoader.registerModel(SOUL_BOTTLE, META_DEFAULT);
		ChastMobModelLoader.registerModel(SOUL_BOTTLE_FULL, META_DEFAULT);
		ChastMobModelLoader.registerModel(SOUL_FRAGMENT, META_DEFAULT);
		ChastMobModelLoader.registerModel(CORE, META_DEFAULT);
		ChastMobModelLoader.registerModel(MODE_PATROL, META_DEFAULT);
		ChastMobModelLoader.registerModel(HELMET_WOOD, META_DEFAULT);
		ChastMobModelLoader.registerModel(HELMET_STONE, META_DEFAULT);
		ChastMobModelLoader.registerModel(HELMET_IRON, META_DEFAULT);
		ChastMobModelLoader.registerModel(HELMET_GOLD, META_DEFAULT);
		ChastMobModelLoader.registerModel(HELMET_DIAMOND, META_DEFAULT);
	}

	// TODO /* ======================================== MOD START =====================================*/

	private static void registerItem(Item item, String name, int meta)
	{
		GameRegistry.register(item, new ResourceLocation(ChastMob.MOD_ID, name));

		if (meta == 0)
		{
			OreDictionary.registerOre(name, item);
		}
		else
		{
			for (int i = 0; i <= meta; i++)
			{
				OreDictionary.registerOre(name + "_" + i, new ItemStack(item, 1, i));
			}
		}
	}

	private static void registerItem(Item item, String name, int meta, String[] oreNames)
	{
		registerItem(item, name, meta);

		for (String ore : oreNames)
		{
			OreDictionary.registerOre(ore, item);
		}
	}

}
