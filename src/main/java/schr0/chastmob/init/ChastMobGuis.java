package schr0.chastmob.init;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import schr0.chastmob.ChastMob;
import schr0.chastmob.entity.EntityChast;
import schr0.chastmob.gui.chastinventory.ContainerChastInventory;
import schr0.chastmob.gui.chastinventory.GuiChastInventory;
import schr0.chastmob.gui.filteredit.ContainerFilterEdit;
import schr0.chastmob.gui.filteredit.GuiFilterEdit;
import schr0.chastmob.item.ItemFilter;

public class ChastMobGuis implements IGuiHandler
{

	public static final int ID_CHAST_INVENTORY = 0;
	public static final int ID_FILTER_EDIT = 1;

	public void registerGuis()
	{
		NetworkRegistry.INSTANCE.registerGuiHandler(ChastMob.instance, this);
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		if (ID == ChastMobGuis.ID_CHAST_INVENTORY)
		{
			Entity entity = world.getEntityByID(x);

			if (entity instanceof EntityChast)
			{
				return new ContainerChastInventory((EntityChast) entity, player);
			}
		}

		if (ID == ChastMobGuis.ID_FILTER_EDIT)
		{
			ItemStack stackMainhand = player.getHeldItemMainhand();

			if (stackMainhand.getItem() == ChastMobItems.FILTER)
			{
				ItemFilter itemFilter = (ItemFilter) stackMainhand.getItem();

				return new ContainerFilterEdit(stackMainhand, itemFilter.getInventoryFilterEdit(stackMainhand), itemFilter.getInventoryFilterResult(stackMainhand), player);
			}
		}

		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		if (ID == ChastMobGuis.ID_CHAST_INVENTORY)
		{
			Entity entity = world.getEntityByID(x);

			if (entity instanceof EntityChast)
			{
				return new GuiChastInventory((EntityChast) entity, player);
			}
		}

		if (ID == ChastMobGuis.ID_FILTER_EDIT)
		{
			ItemStack stackMainhand = player.getHeldItemMainhand();

			if (stackMainhand.getItem() == ChastMobItems.FILTER)
			{
				ItemFilter itemFilter = (ItemFilter) stackMainhand.getItem();

				return new GuiFilterEdit(stackMainhand, itemFilter.getInventoryFilterEdit(stackMainhand), itemFilter.getInventoryFilterResult(stackMainhand), player);
			}
		}

		return null;
	}

}
