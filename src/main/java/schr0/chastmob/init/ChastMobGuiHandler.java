package schr0.chastmob.init;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import schr0.chastmob.entity.EntityChast;
import schr0.chastmob.entity.gui.ContainerChastInventory;
import schr0.chastmob.entity.gui.GuiChastInventory;

public class ChastMobGuiHandler implements IGuiHandler
{

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		Entity entity = world.getEntityByID(x);

		if (entity instanceof EntityChast)
		{
			if (ID == ChastMobGui.ID_CHAST_INVENTORY)
			{
				return new ContainerChastInventory((EntityChast) entity, player);
			}
		}

		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		Entity entity = world.getEntityByID(x);

		if (entity instanceof EntityChast)
		{
			if (ID == ChastMobGui.ID_CHAST_INVENTORY)
			{
				return new GuiChastInventory((EntityChast) entity, player);
			}
		}

		return null;
	}

}
