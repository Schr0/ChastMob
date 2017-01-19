package schr0.chastmob.entity.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import schr0.chastmob.ChastMob;
import schr0.chastmob.entity.EntityChast;
import schr0.chastmob.entity.render.layer.LayerChastArm;
import schr0.chastmob.entity.render.layer.LayerChastCore;
import schr0.chastmob.entity.render.layer.LayerChastHeldItem;
import schr0.chastmob.entity.render.layer.LayerChastMode;

@SideOnly(Side.CLIENT)
public class RenderChast extends RenderLiving<EntityChast>
{

	private static final ResourceLocation RES_CHAST = new ResourceLocation(ChastMob.MOD_RESOURCE_DOMAIN + "textures/entity/chast/chast.png");

	public RenderChast(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn)
	{
		super(renderManagerIn, modelBaseIn, shadowSizeIn);
		this.addLayer(new LayerChastCore(this));
		this.addLayer(new LayerChastArm(this));
		this.addLayer(new LayerChastMode(this));
		this.addLayer(new LayerChastHeldItem(this));
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityChast entity)
	{
		return RES_CHAST;
	}

}
