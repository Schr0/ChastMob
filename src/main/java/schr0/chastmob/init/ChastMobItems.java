package schr0.chastmob.init;

import java.util.ArrayList;

import com.google.common.collect.Lists;

import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;
import schr0.chastmob.ChastMob;
import schr0.chastmob.item.ItemChastHelmetDiamond;
import schr0.chastmob.item.ItemChastHelmetGold;
import schr0.chastmob.item.ItemChastHelmetIron;
import schr0.chastmob.item.ItemChastHelmetStone;
import schr0.chastmob.item.ItemChastHelmetWood;
import schr0.chastmob.item.ItemCore;
import schr0.chastmob.item.ItemSoulBottle;
import schr0.chastmob.item.ItemSoulBottleFull;
import schr0.chastmob.item.ItemSoulFragment;
import schr0.chastmob.util.ChastMobCreativeTabs;

public class ChastMobItems
{

	public static final Item SOUL_BOTTLE;
	public static final Item SOUL_BOTTLE_FULL;
	public static final Item SOUL_FRAGMENT;
	public static final Item CORE;
	public static final Item CHAST_HELMET_WOOD;
	public static final Item CHAST_HELMET_STONE;
	public static final Item CHAST_HELMET_IRON;
	public static final Item CHAST_HELMET_DIAMOND;
	public static final Item CHAST_HELMET_GOLD;

	public static final String NAME_SOUL_BOTTLE = "soul_bottle";
	public static final String NAME_SOUL_BOTTLE_FULL = "soul_bottle_full";
	public static final String NAME_SOUL_FRAGMENT = "soul_fragment";
	public static final String NAME_CORE = "core";
	public static final String NAME_CHAST_HELMET_WOOD = "chast_helmet_wood";
	public static final String NAME_CHAST_HELMET_STONE = "chast_helmet_stone";
	public static final String NAME_CHAST_HELMET_IRON = "chast_helmet_iron";
	public static final String NAME_CHAST_HELMET_DIAMOND = "chast_helmet_diamond";
	public static final String NAME_CHAST_HELMET_GOLD = "chast_helmet_gold";

	static
	{
		SOUL_BOTTLE = new ItemSoulBottle().setUnlocalizedName(NAME_SOUL_BOTTLE).setCreativeTab(ChastMobCreativeTabs.ITEM);
		SOUL_BOTTLE_FULL = new ItemSoulBottleFull().setUnlocalizedName(NAME_SOUL_BOTTLE_FULL).setCreativeTab(ChastMobCreativeTabs.ITEM);
		SOUL_FRAGMENT = new ItemSoulFragment().setUnlocalizedName(NAME_SOUL_FRAGMENT).setCreativeTab(ChastMobCreativeTabs.ITEM);
		CORE = new ItemCore().setUnlocalizedName(NAME_CORE).setCreativeTab(ChastMobCreativeTabs.ITEM);
		CHAST_HELMET_WOOD = new ItemChastHelmetWood().setUnlocalizedName(NAME_CHAST_HELMET_WOOD).setCreativeTab(ChastMobCreativeTabs.ITEM);
		CHAST_HELMET_STONE = new ItemChastHelmetStone().setUnlocalizedName(NAME_CHAST_HELMET_STONE).setCreativeTab(ChastMobCreativeTabs.ITEM);
		CHAST_HELMET_IRON = new ItemChastHelmetIron().setUnlocalizedName(NAME_CHAST_HELMET_IRON).setCreativeTab(ChastMobCreativeTabs.ITEM);
		CHAST_HELMET_DIAMOND = new ItemChastHelmetDiamond().setUnlocalizedName(NAME_CHAST_HELMET_DIAMOND).setCreativeTab(ChastMobCreativeTabs.ITEM);
		CHAST_HELMET_GOLD = new ItemChastHelmetGold().setUnlocalizedName(NAME_CHAST_HELMET_GOLD).setCreativeTab(ChastMobCreativeTabs.ITEM);
	}

	public void registerItems(IForgeRegistry<Item> registry)
	{
		registerItem(registry, SOUL_BOTTLE, NAME_SOUL_BOTTLE);
		registerItem(registry, SOUL_BOTTLE_FULL, NAME_SOUL_BOTTLE_FULL);
		registerItem(registry, SOUL_FRAGMENT, NAME_SOUL_FRAGMENT);
		registerItem(registry, CORE, NAME_CORE);
		registerItem(registry, CHAST_HELMET_WOOD, NAME_CHAST_HELMET_WOOD);
		registerItem(registry, CHAST_HELMET_STONE, NAME_CHAST_HELMET_STONE);
		registerItem(registry, CHAST_HELMET_IRON, NAME_CHAST_HELMET_IRON);
		registerItem(registry, CHAST_HELMET_DIAMOND, NAME_CHAST_HELMET_DIAMOND);
		registerItem(registry, CHAST_HELMET_GOLD, NAME_CHAST_HELMET_GOLD);
	}

	@SideOnly(Side.CLIENT)
	public void registerModels()
	{
		registerModel(SOUL_BOTTLE);
		registerModel(SOUL_BOTTLE_FULL);
		registerModel(SOUL_FRAGMENT);
		registerModel(CORE);
		registerModel(CHAST_HELMET_WOOD);
		registerModel(CHAST_HELMET_STONE);
		registerModel(CHAST_HELMET_IRON);
		registerModel(CHAST_HELMET_GOLD);
		registerModel(CHAST_HELMET_DIAMOND);
	}

	// TODO /* ======================================== MOD START =====================================*/

	private static void registerItem(IForgeRegistry<Item> registry, Item item, int meta, String name)
	{
		item.setRegistryName(ChastMob.MOD_ID, name);

		registry.register(item);

		String domain = ChastMob.MOD_ID + ".";

		if (meta <= 0)
		{
			OreDictionary.registerOre(domain + name, item);
		}
		else
		{
			for (int i = 0; i < meta; i++)
			{
				OreDictionary.registerOre(domain + name + "_" + i, new ItemStack(item, 1, i));
			}
		}
	}

	private static void registerItem(IForgeRegistry<Item> registry, Item item, String name)
	{
		registerItem(registry, item, 0, name);
	}

	@SideOnly(Side.CLIENT)
	public static void registerModel(Item item, int meta)
	{
		if (meta == 0)
		{
			ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
			ModelBakery.registerItemVariants(item, item.getRegistryName());
		}
		else
		{
			ArrayList<ResourceLocation> models = Lists.newArrayList();

			for (int i = 0; i <= meta; i++)
			{
				ModelLoader.setCustomModelResourceLocation(item, i, new ModelResourceLocation(item.getRegistryName() + "_" + i, "inventory"));

				models.add(new ResourceLocation(item.getRegistryName() + "_" + i));
			}

			ModelBakery.registerItemVariants(item, models.toArray(new ResourceLocation[models.size()]));
		}
	}

	@SideOnly(Side.CLIENT)
	public static void registerModel(Item item)
	{
		registerModel(item, 0);
	}

}
