package schr0.chastmob.init;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.minecraftforge.registries.IForgeRegistry;
import schr0.chastmob.ChastMob;

public class ChastMobRecipe
{

	public static final String KEY_RES = ChastMob.MOD_ID;
	public static final ResourceLocation RES_SOUL_BOTTLE = new ResourceLocation(KEY_RES, "soul_bottle");
	public static final ResourceLocation RES_SOUL_FRAGMENT = new ResourceLocation(KEY_RES, "soul_fragment");
	public static final ResourceLocation RES_CORE = new ResourceLocation(KEY_RES, "core");
	public static final ResourceLocation RES_MODE_PATROL = new ResourceLocation(KEY_RES, "mode_patrol");
	public static final ResourceLocation RES_MODE_PATROL_RESET = new ResourceLocation(KEY_RES, "mode_patrol_reset");
	public static final ResourceLocation RES_FILTER = new ResourceLocation(KEY_RES, "filter");
	public static final ResourceLocation RES_FILTER_WHITE = new ResourceLocation(KEY_RES, "filter_white");
	public static final ResourceLocation RES_FILTER_BLACK = new ResourceLocation(KEY_RES, "filter_black");
	public static final ResourceLocation RES_HELMET_WOOD = new ResourceLocation(KEY_RES, "helmet_wood");
	public static final ResourceLocation RES_HELMET_STONE = new ResourceLocation(KEY_RES, "helmet_stone");
	public static final ResourceLocation RES_HELMET_IRON = new ResourceLocation(KEY_RES, "helmet_iron");
	public static final ResourceLocation RES_HELMET_DIAMOND = new ResourceLocation(KEY_RES, "helmet_diamond");
	public static final ResourceLocation RES_HELMET_GOLD = new ResourceLocation(KEY_RES, "helmet_gold");

	public void registerRecipes(IForgeRegistry<IRecipe> registry)
	{
		registry.register(getRecipeSoulBottle());
		registry.register(getRecipeSoulFragment());
		registry.register(getRecipeCore());
		registry.register(getRecipeModePatrol());
		registry.register(getRecipeModePatrolReset());
		registry.register(getRecipeFilter());
		registry.register(getRecipeFilterWhite());
		registry.register(getRecipeFilterBlack());
		registry.register(getRecipeHelmetWood());
		registry.register(getRecipeHelmetStone());
		registry.register(getRecipeHelmetIron());
		registry.register(getRecipeHelmetDiamond());
		registry.register(getRecipeHelmetGold());
	}

	// TODO /* ======================================== MOD START =====================================*/

	private static IRecipe getRecipeSoulBottle()
	{
		return new ShapedOreRecipe(RES_SOUL_BOTTLE, new ItemStack(ChastMobItems.SOUL_BOTTLE), new Object[]
		{
				"XYX",
				"YXY",

				'X', new ItemStack(Items.GOLD_NUGGET),
				'Y', new ItemStack(Items.QUARTZ),
		}).setRegistryName(RES_SOUL_BOTTLE);
	}

	private static IRecipe getRecipeSoulFragment()
	{
		return new ShapelessOreRecipe(RES_SOUL_FRAGMENT, new ItemStack(ChastMobItems.SOUL_FRAGMENT, 16), new Object[]
		{
				new ItemStack(ChastMobItems.SOUL_BOTTLE_FULL),
		}).setRegistryName(RES_SOUL_FRAGMENT);
	}

	private static IRecipe getRecipeCore()
	{
		return new ShapelessOreRecipe(RES_CORE, new ItemStack(ChastMobItems.CORE), new Object[]
		{
				new ItemStack(Blocks.CHEST),
				new ItemStack(ChastMobItems.SOUL_BOTTLE_FULL),
		}).setRegistryName(RES_CORE);
	}

	private static IRecipe getRecipeModePatrol()
	{
		return new ShapelessOreRecipe(RES_MODE_PATROL, new ItemStack(ChastMobItems.MODE_PATROL), new Object[]
		{
				new ItemStack(Items.MAP),
				new ItemStack(ChastMobItems.SOUL_FRAGMENT),
		}).setRegistryName(RES_MODE_PATROL);
	}

	private static IRecipe getRecipeModePatrolReset()
	{
		return new ShapelessOreRecipe(RES_MODE_PATROL_RESET, new ItemStack(ChastMobItems.MODE_PATROL), new Object[]
		{
				new ItemStack(ChastMobItems.MODE_PATROL),
		}).setRegistryName(RES_MODE_PATROL_RESET);
	}

	private static IRecipe getRecipeFilter()
	{
		return new ShapelessOreRecipe(RES_FILTER, new ItemStack(ChastMobItems.FILTER, 1), new Object[]
		{
				new ItemStack(Items.PAPER),
				new ItemStack(ChastMobItems.SOUL_FRAGMENT),
		}).setRegistryName(RES_FILTER);
	}

	private static IRecipe getRecipeFilterWhite()
	{
		return new ShapelessOreRecipe(RES_FILTER_WHITE, new ItemStack(ChastMobItems.FILTER, 1), new Object[]
		{
				new ItemStack(ChastMobItems.FILTER, 1),
		}).setRegistryName(RES_FILTER_WHITE);
	}

	private static IRecipe getRecipeFilterBlack()
	{
		return new ShapelessOreRecipe(RES_FILTER_BLACK, new ItemStack(ChastMobItems.FILTER, 1, 1), new Object[]
		{
				new ItemStack(ChastMobItems.FILTER, 1, 1),
		}).setRegistryName(RES_FILTER_BLACK);
	}

	private static IRecipe getRecipeHelmetWood()
	{
		return new ShapedOreRecipe(RES_HELMET_WOOD, new ItemStack(ChastMobItems.HELMET_WOOD), new Object[]
		{
				"XXX",
				"XYX",
				"XXX",

				'X', new ItemStack(Item.getItemFromBlock(Blocks.PLANKS)),
				'Y', new ItemStack(ChastMobItems.SOUL_FRAGMENT),
		}).setRegistryName(RES_HELMET_WOOD);
	}

	private static IRecipe getRecipeHelmetStone()
	{
		return new ShapedOreRecipe(RES_HELMET_STONE, new ItemStack(ChastMobItems.HELMET_STONE), new Object[]
		{
				"XXX",
				"XYX",
				"XXX",

				'X', new ItemStack(Item.getItemFromBlock(Blocks.COBBLESTONE)),
				'Y', new ItemStack(ChastMobItems.SOUL_FRAGMENT),
		}).setRegistryName(RES_HELMET_STONE);
	}

	private static IRecipe getRecipeHelmetIron()
	{
		return new ShapedOreRecipe(RES_HELMET_IRON, new ItemStack(ChastMobItems.HELMET_IRON), new Object[]
		{
				"XXX",
				"XYX",
				"XXX",

				'X', new ItemStack(Items.IRON_INGOT),
				'Y', new ItemStack(ChastMobItems.SOUL_FRAGMENT),
		}).setRegistryName(RES_HELMET_IRON);
	}

	private static IRecipe getRecipeHelmetDiamond()
	{
		return new ShapedOreRecipe(RES_HELMET_DIAMOND, new ItemStack(ChastMobItems.HELMET_DIAMOND), new Object[]
		{
				"XXX",
				"XYX",
				"XXX",

				'X', new ItemStack(Items.DIAMOND),
				'Y', new ItemStack(ChastMobItems.SOUL_FRAGMENT),
		}).setRegistryName(RES_HELMET_DIAMOND);
	}

	private static IRecipe getRecipeHelmetGold()
	{
		return new ShapedOreRecipe(RES_HELMET_GOLD, new ItemStack(ChastMobItems.HELMET_GOLD), new Object[]
		{
				"XXX",
				"XYX",
				"XXX",

				'X', new ItemStack(Items.GOLD_INGOT),
				'Y', new ItemStack(ChastMobItems.SOUL_FRAGMENT),
		}).setRegistryName(RES_HELMET_GOLD);
	}

}
