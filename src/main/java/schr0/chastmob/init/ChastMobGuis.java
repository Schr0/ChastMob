package schr0.chastmob.init;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import schr0.chastmob.ChastMob;
import schr0.chastmob.entity.EntityChast;
import schr0.chastmob.gui.ContainerChastInventory;
import schr0.chastmob.gui.GuiChastInventory;

public class ChastMobGuis implements IGuiHandler
{

	public static final int ID_CHAST_INVENTORY = 0;

	public void registerGuis()
	{
		NetworkRegistry.INSTANCE.registerGuiHandler(ChastMob.instance, this);
	}

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
	{
		if (id == ChastMobGuis.ID_CHAST_INVENTORY)
		{
			Entity entity = world.getEntityByID(x);

			if (entity instanceof EntityChast)
			{
				return new ContainerChastInventory((EntityChast) entity, player);
			}
		}

		return null;
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
	{
		if (id == ChastMobGuis.ID_CHAST_INVENTORY)
		{
			Entity entity = world.getEntityByID(x);

			if (entity instanceof EntityChast)
			{
				return new GuiChastInventory((EntityChast) entity, player);
			}
		}

		return null;
	}

}
