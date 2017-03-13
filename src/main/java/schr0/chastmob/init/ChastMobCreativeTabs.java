package schr0.chastmob.init;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import schr0.chastmob.ChastMob;

public class ChastMobCreativeTabs
{

	public static final CreativeTabs ITEM = new CreativeTabs(ChastMob.MOD_ID + "." + "item")
	{

		@Override
		@SideOnly(Side.CLIENT)
		public ItemStack getTabIconItem()
		{
			return new ItemStack(ChastMobItems.SOUL_BOTTLE_FULL);
		}

	};

}
