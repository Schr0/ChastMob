package schr0.chastmob.init;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import schr0.chastmob.ChastMob;
import schr0.chastmob.entity.EntityChast;
import schr0.chastmob.entity.render.ModelChast;
import schr0.chastmob.entity.render.RenderChast;

public class ChastMobEntitys
{

	public static final int TRACKING_RANGE = 250;
	public static final int UPDATE_FREQUENCY = 1;
	public static final boolean SENDS_VELOCITY_UPDATES = true;

	public static final String NAME_CHAST = "chast";
	public static final int ID_CHAST = 0;
	public static final int EGG_PRIMARY_CHAST = 0xa47227;
	public static final int EGG_SECONDARY_CHAST = 0x000000;

	public void registerEntitys()
	{
		registerEntity(EntityChast.class, NAME_CHAST, ID_CHAST, ChastMob.instance, TRACKING_RANGE, UPDATE_FREQUENCY, SENDS_VELOCITY_UPDATES, EGG_PRIMARY_CHAST, EGG_SECONDARY_CHAST);
	}

	@SideOnly(Side.CLIENT)
	public void registerRenders()
	{
		RenderingRegistry.registerEntityRenderingHandler(EntityChast.class, new IRenderFactory()
		{
			@Override
			public Render createRenderFor(RenderManager renderManager)
			{
				return new RenderChast(renderManager, new ModelChast(), 0.5F);
			}
		});
	}

	// TODO /* ======================================== MOD START =====================================*/

	private static void registerEntity(Class<? extends Entity> entityClass, String entityName, int id, Object mod, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates, int eggPrimary, int eggSecondary)
	{
		EntityRegistry.registerModEntity(new ResourceLocation(ChastMob.MOD_ID, entityName), entityClass, entityName, id, mod, trackingRange, updateFrequency, sendsVelocityUpdates, eggPrimary, eggSecondary);
	}

}
