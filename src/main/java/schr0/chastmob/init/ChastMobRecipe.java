package schr0.chastmob.init;

import java.util.HashMap;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class ChastMobRecipe
{

	public void init()
	{
		addRecipeItem();
		addRecipeItemArmour();
	}

	private static void addRecipeItem()
	{
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ChastMobItems.SOUL_BOTTLE), new Object[]
		{
				"XYX",
				"YXY",

				'X', new ItemStack(Items.GOLD_NUGGET),
				'Y', new ItemStack(Items.QUARTZ),
		}));

		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ChastMobItems.SOUL_FRAGMENT, 4), new Object[]
		{
				new ItemStack(ChastMobItems.SOUL_BOTTLE_FULL),
		}));

		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ChastMobItems.CORE), new Object[]
		{
				new ItemStack(Blocks.CHEST),
				new ItemStack(ChastMobItems.SOUL_BOTTLE_FULL),
		}));

		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ChastMobItems.MODE_PATROL), new Object[]
		{
				new ItemStack(Items.MAP),
				new ItemStack(ChastMobItems.SOUL_FRAGMENT),
		}));

		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ChastMobItems.MODE_PATROL), new Object[]
		{
				new ItemStack(ChastMobItems.MODE_PATROL),
		}));

		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ChastMobItems.FILTER, 1, 0), new Object[]
		{
				new ItemStack(Blocks.IRON_BARS),
				new ItemStack(ChastMobItems.SOUL_FRAGMENT),
		}));

		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ChastMobItems.FILTER, 1, 0), new Object[]
		{
				new ItemStack(ChastMobItems.FILTER, 1, 0),
		}));

		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ChastMobItems.FILTER, 1, 1), new Object[]
		{
				new ItemStack(ChastMobItems.FILTER, 1, 1),
		}));
	}

	private static void addRecipeItemArmour()
	{
		HashMap<Item, Item> armourRecipe = new HashMap<Item, Item>()
		{
			{
				put(ChastMobItems.HELMET_WOOD, Item.getItemFromBlock(Blocks.PLANKS));
				put(ChastMobItems.HELMET_STONE, Item.getItemFromBlock(Blocks.COBBLESTONE));
				put(ChastMobItems.HELMET_IRON, Items.IRON_INGOT);
				put(ChastMobItems.HELMET_DIAMOND, Items.DIAMOND);
				put(ChastMobItems.HELMET_GOLD, Items.GOLD_INGOT);
			}
		};

		for (Item armour : armourRecipe.keySet())
		{
			Item material = armourRecipe.get(armour);

			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(armour), new Object[]
			{
					"XXX",
					"XYX",
					"XXX",

					'X', new ItemStack(material),
					'Y', new ItemStack(ChastMobItems.SOUL_FRAGMENT),
			}));
		}

	}

}
