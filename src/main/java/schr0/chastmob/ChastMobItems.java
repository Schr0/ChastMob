package schr0.chastmob;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class ChastMobItems
{

	public static final Item HOME_CHEST_MAP;

	public static final String NAME_HOME_CHEST_MAP = "home_chest_map";

	public static final int META_HOME_CHEST_MAP = 0;

	static
	{
		HOME_CHEST_MAP = new ItemHomeChestMap().setUnlocalizedName(NAME_HOME_CHEST_MAP).setCreativeTab(ChastMobCreativeTabs.ITEM);
	}

	public void init()
	{
		registerItem(HOME_CHEST_MAP, NAME_HOME_CHEST_MAP, META_HOME_CHEST_MAP);
	}

	@SideOnly(Side.CLIENT)
	public void initClient()
	{
		ChastMobModelLoader.registerModel(HOME_CHEST_MAP, META_HOME_CHEST_MAP);
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
