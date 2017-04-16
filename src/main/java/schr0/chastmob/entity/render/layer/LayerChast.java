package schr0.chastmob.entity.render.layer;

import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import schr0.chastmob.entity.EntityChast;
import schr0.chastmob.entity.render.ModelChast;
import schr0.chastmob.entity.render.RenderChast;

@SideOnly(Side.CLIENT)
public abstract class LayerChast implements LayerRenderer<EntityChast>
{

	private final RenderChast renderChast;

	public LayerChast(RenderChast chastRendererRendererIn)
	{
		this.renderChast = chastRendererRendererIn;
	}

	// TODO /* ======================================== MOD START =====================================*/

	public RenderChast getRender()
	{
		return this.renderChast;
	}

	public ModelChast getModel()
	{
		return (ModelChast) renderChast.getMainModel();
	}

}
