package schr0.chastmob.init;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class ChastMobRecipe
{

	public void init()
	{
		addRecipeItem();
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

		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ChastMobItems.CORE_CHAST), new Object[]
		{
				new ItemStack(Blocks.CHEST),
				new ItemStack(ChastMobItems.SOUL_BOTTLE_FULL),
		}));

		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ChastMobItems.SPECIFICATION_PATROL), new Object[]
		{
				new ItemStack(Items.PAPER),
				new ItemStack(ChastMobItems.SOUL_FRAGMENT),
		}));

		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ChastMobItems.SPECIFICATION_PATROL), new Object[]
		{
				new ItemStack(ChastMobItems.SPECIFICATION_PATROL),
		}));
	}

}
