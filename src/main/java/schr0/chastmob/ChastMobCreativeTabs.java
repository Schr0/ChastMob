package schr0.chastmob;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ChastMobCreativeTabs
{

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
