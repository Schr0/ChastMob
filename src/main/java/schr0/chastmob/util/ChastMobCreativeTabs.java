package schr0.chastmob.util;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import schr0.chastmob.ChastMob;
import schr0.chastmob.init.ChastMobItems;

public class ChastMobCreativeTabs
{

	public static final CreativeTabs ITEM = new CreativeTabs(ChastMob.MOD_ID + "." + "item")
	{

		@Override
		public ItemStack getTabIconItem()
		{
			return new ItemStack(ChastMobItems.CORE);
		}

	};

}
