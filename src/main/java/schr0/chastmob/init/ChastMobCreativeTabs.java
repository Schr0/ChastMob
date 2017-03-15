package schr0.chastmob.init;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import schr0.chastmob.ChastMob;

public class ChastMobCreativeTabs
{

	public static final CreativeTabs ITEM = new CreativeTabs(ChastMob.MOD_ID + "." + "item")
	{

		@Override
		public ItemStack getTabIconItem()
		{
			return new ItemStack(ChastMobItems.SOUL_BOTTLE_FULL);
		}

	};

}
