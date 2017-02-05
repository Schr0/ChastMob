package schr0.chastmob.init;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import schr0.chastmob.ChastMob;

public class ChastMobCreativeTabs
{

	private static final String TAB = ChastMob.MOD_ID + "." + "creativetabs";

	public static final CreativeTabs ITEM = new CreativeTabs(ChastMob.MOD_ID + "." + "item")
	{

		@Override
		@SideOnly(Side.CLIENT)
		public Item getTabIconItem()
		{
			return Item.getItemFromBlock(Blocks.CHEST);
		}

	};

}
