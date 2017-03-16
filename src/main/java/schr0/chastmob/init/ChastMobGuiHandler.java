package schr0.chastmob.init;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import schr0.chastmob.entity.EntityChast;
import schr0.chastmob.gui.chastinventory.ContainerChastInventory;
import schr0.chastmob.gui.chastinventory.GuiChastInventory;
import schr0.chastmob.gui.filteredit.ContainerFilterEdit;
import schr0.chastmob.gui.filteredit.GuiFilterEdit;

public class ChastMobGuiHandler implements IGuiHandler
{

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		if (ID == ChastMobGui.ID_CHAST_INVENTORY)
		{
			Entity entity = world.getEntityByID(x);

			if (entity instanceof EntityChast)
			{
				return new ContainerChastInventory((EntityChast) entity, player);
			}
		}

		if (ID == ChastMobGui.ID_FILTER_EDIT)
		{
			ItemStack stackMainhand = player.getHeldItemMainhand();

			if (stackMainhand.getItem() == ChastMobItems.FILTER)
			{
				return new ContainerFilterEdit(new InventoryBasic("", true, 9), stackMainhand, player);
			}
		}

		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		if (ID == ChastMobGui.ID_CHAST_INVENTORY)
		{
			Entity entity = world.getEntityByID(x);

			if (entity instanceof EntityChast)
			{
				return new GuiChastInventory((EntityChast) entity, player);
			}
		}

		if (ID == ChastMobGui.ID_FILTER_EDIT)
		{
			ItemStack stackMainhand = player.getHeldItemMainhand();

			if (stackMainhand.getItem() == ChastMobItems.FILTER)
			{
				return new GuiFilterEdit(new InventoryBasic("", true, 9), stackMainhand, player);
			}
		}

		return null;
	}

}
