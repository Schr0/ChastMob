package schr0.chastmob.init;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import schr0.chastmob.ChastMob;
import schr0.chastmob.item.ItemCoreChast;
import schr0.chastmob.item.ItemHelmetChastIron;
import schr0.chastmob.item.ItemHelmetChastWood;
import schr0.chastmob.item.ItemSoulBottle;
import schr0.chastmob.item.ItemSoulBottleFull;
import schr0.chastmob.item.ItemSoulFragment;
import schr0.chastmob.item.ItemSpecificationPatrol;

public class ChastMobItems
{

	public static final Item SOUL_BOTTLE;
	public static final Item SOUL_BOTTLE_FULL;
	public static final Item SOUL_FRAGMENT;
	public static final Item CORE_CHAST;
	public static final Item SPECIFICATION_PATROL;
	public static final Item HELMET_CHAST_WOOD;
	public static final Item HELMET_CHAST_IRON;

	public static final String NAME_SOUL_BOTTLE = "soul_bottle";
	public static final String NAME_SOUL_BOTTLE_FULL = "soul_bottle_full";
	public static final String NAME_SOUL_FRAGMENT = "soul_fragment";
	public static final String NAME_CORE_CHAST = "core_chast";
	public static final String NAME_SPECIFICATION_PATROL = "specification_patrol";
	public static final String NAME_HELMET_CHAST_WOOD = "helmet_chast_wood";
	public static final String NAME_HELMET_CHAST_IRON = "helmet_chast_iron";

	public static final int META_SOUL_BOTTLE = 0;
	public static final int META_SOUL_BOTTLE_FULL = 0;
	public static final int META_SOUL_FRAGMENT = 0;
	public static final int META_CORE_CHAST = 0;
	public static final int META_SPECIFICATION_PATROL = 0;
	public static final int META_HELMET_CHAST_WOOD = 0;
	public static final int META_HELMET_CHAST_IRON = 0;

	static
	{
		SOUL_BOTTLE = new ItemSoulBottle().setUnlocalizedName(NAME_SOUL_BOTTLE).setCreativeTab(ChastMobCreativeTabs.ITEM);
		SOUL_BOTTLE_FULL = new ItemSoulBottleFull().setUnlocalizedName(NAME_SOUL_BOTTLE_FULL).setCreativeTab(ChastMobCreativeTabs.ITEM);
		SOUL_FRAGMENT = new ItemSoulFragment().setUnlocalizedName(NAME_SOUL_FRAGMENT).setCreativeTab(ChastMobCreativeTabs.ITEM);
		CORE_CHAST = new ItemCoreChast().setUnlocalizedName(NAME_CORE_CHAST).setCreativeTab(ChastMobCreativeTabs.ITEM);
		SPECIFICATION_PATROL = new ItemSpecificationPatrol().setUnlocalizedName(NAME_SPECIFICATION_PATROL).setCreativeTab(ChastMobCreativeTabs.ITEM);
		HELMET_CHAST_WOOD = new ItemHelmetChastWood().setUnlocalizedName(NAME_HELMET_CHAST_WOOD).setCreativeTab(ChastMobCreativeTabs.ITEM);
		HELMET_CHAST_IRON = new ItemHelmetChastIron().setUnlocalizedName(NAME_HELMET_CHAST_IRON).setCreativeTab(ChastMobCreativeTabs.ITEM);
	}

	public void init()
	{
		registerItem(SOUL_BOTTLE, NAME_SOUL_BOTTLE, META_SOUL_BOTTLE);
		registerItem(SOUL_BOTTLE_FULL, NAME_SOUL_BOTTLE_FULL, META_SOUL_BOTTLE_FULL);
		registerItem(SOUL_FRAGMENT, NAME_SOUL_FRAGMENT, META_SOUL_FRAGMENT);
		registerItem(CORE_CHAST, NAME_CORE_CHAST, META_CORE_CHAST);
		registerItem(SPECIFICATION_PATROL, NAME_SPECIFICATION_PATROL, META_SPECIFICATION_PATROL);
		registerItem(HELMET_CHAST_WOOD, NAME_HELMET_CHAST_WOOD, META_HELMET_CHAST_WOOD);
		registerItem(HELMET_CHAST_IRON, NAME_HELMET_CHAST_IRON, META_HELMET_CHAST_IRON);
	}

	@SideOnly(Side.CLIENT)
	public void initClient()
	{
		ChastMobModelLoader.registerModel(SOUL_BOTTLE, META_SOUL_BOTTLE);
		ChastMobModelLoader.registerModel(SOUL_BOTTLE_FULL, META_SOUL_BOTTLE_FULL);
		ChastMobModelLoader.registerModel(SOUL_FRAGMENT, META_SOUL_FRAGMENT);
		ChastMobModelLoader.registerModel(CORE_CHAST, META_CORE_CHAST);
		ChastMobModelLoader.registerModel(SPECIFICATION_PATROL, META_SPECIFICATION_PATROL);
		ChastMobModelLoader.registerModel(HELMET_CHAST_WOOD, META_HELMET_CHAST_WOOD);
		ChastMobModelLoader.registerModel(HELMET_CHAST_IRON, META_HELMET_CHAST_IRON);
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
