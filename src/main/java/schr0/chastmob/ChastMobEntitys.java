package schr0.chastmob;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ChastMobEntitys
{

	public static final int TRACKING_RANGE = 250;
	public static final int UPDATE_FREQUENCY = 1;
	public static final boolean SENDS_VELOCITY_UPDATES = true;

	public static final String NAME_CHAST = "chast";

	public static final int ID_CHAST = 0;

	public static final int EGG_PRIMARY_CHAST = 0x8f691d;

	public static final int EGG_SECONDARY_CHAST = 0x695229;

	public void init()
	{
		EntityRegistry.registerModEntity(EntityChast.class, NAME_CHAST, ID_CHAST, ChastMob.instance, TRACKING_RANGE, UPDATE_FREQUENCY, SENDS_VELOCITY_UPDATES, EGG_PRIMARY_CHAST, EGG_SECONDARY_CHAST);
	}

	@SideOnly(Side.CLIENT)
	public void initClient()
	{
		RenderingRegistry.registerEntityRenderingHandler(EntityChast.class, new IRenderFactory()
		{
			@Override
			public Render createRenderFor(RenderManager manager)
			{
				return new RenderChast(manager, new ModelChast(), 0.5F);
			}
		});
	}

}
