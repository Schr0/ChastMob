package schr0.chastmob.entity.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import schr0.chastmob.ChastMob;
import schr0.chastmob.ChastMobHelper;
import schr0.chastmob.entity.EntityChast;
import schr0.chastmob.entity.ai.EnumAIMode;
import schr0.chastmob.entity.render.layer.LayerChastArm;
import schr0.chastmob.entity.render.layer.LayerChastCore;
import schr0.chastmob.entity.render.layer.LayerChastHeldItem;
import schr0.chastmob.init.ChastMobLang;

@SideOnly(Side.CLIENT)
public class RenderChast extends RenderLiving<EntityChast>
{

	private static final ResourceLocation RES_CHAST = new ResourceLocation(ChastMob.MOD_RESOURCE_DOMAIN + "textures/entity/chast/chast.png");
	private final RenderItem itemRenderer;

	public RenderChast(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn)
	{
		super(renderManagerIn, modelBaseIn, shadowSizeIn);
		this.addLayer(new LayerChastCore(this));
		this.addLayer(new LayerChastArm(this));
		this.addLayer(new LayerChastHeldItem(this));

		this.itemRenderer = Minecraft.getMinecraft().getRenderItem();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityChast entity)
	{
		return RES_CHAST;
	}

	@Override
	public void doRender(EntityChast entity, double x, double y, double z, float entityYaw, float partialTicks)
	{
		if (this.canRenderModeLabel(entity))
		{
			boolean isSneaking = entity.isSneaking();

			EntityRenderer.func_189692_a(this.getFontRendererFromRenderManager(), this.getModeLabel(entity), (float) x, (float) y + (entity.height + 0.25F - (isSneaking ? 0.125F : 0.0F)), (float) z, 0, this.renderManager.playerViewY, this.renderManager.playerViewX, (this.renderManager.options.thirdPersonView == 2), isSneaking);
		}

		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	// TODO /* ======================================== MOD START =====================================*/

	private boolean canRenderModeLabel(EntityChast entity)
	{
		if (this.renderManager.renderViewEntity instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer) this.renderManager.renderViewEntity;
			EnumHand handHasItemBatonChast = this.getHandHasItemBatonChast(player);
			ItemStack stackHeldItem = player.getHeldItem(handHasItemBatonChast);

			if (this.isItemItemBatonChast(stackHeldItem))
			{
				double distance = entity.getDistanceSqToEntity(this.renderManager.renderViewEntity);
				float maxDistance = entity.isSneaking() ? NAME_TAG_RANGE_SNEAK : NAME_TAG_RANGE;

				if (distance < (double) (maxDistance * maxDistance))
				{
					return Minecraft.isGuiEnabled() && (entity != this.renderManager.renderViewEntity) && (entity == this.renderManager.pointedEntity) && !entity.isBeingRidden() && !entity.isStateTrade();
				}
			}
		}

		return false;
	}

	private EnumHand getHandHasItemBatonChast(EntityPlayer player)
	{
		if (this.isItemItemBatonChast(player.getHeldItemMainhand()))
		{
			return EnumHand.MAIN_HAND;
		}

		if (this.isItemItemBatonChast(player.getHeldItemOffhand()))
		{
			return EnumHand.OFF_HAND;
		}

		return EnumHand.MAIN_HAND;
	}

	private boolean isItemItemBatonChast(ItemStack stack)
	{
		// TODO
		if (ChastMobHelper.isNotEmptyItemStack(stack) && (stack.getItem().equals(Items.APPLE)))
		{
			return true;
		}

		return false;
	}

	private String getModeLabel(EntityChast entity)
	{
		String label = TextFormatting.BOLD.DARK_RED + new TextComponentTranslation(ChastMobLang.ENTITY_CHAST_MODE_FREEDOM, new Object[0]).getFormattedText();

		if (entity.getAIMode() == EnumAIMode.FOLLOW)
		{
			label = TextFormatting.BOLD.AQUA + new TextComponentTranslation(ChastMobLang.ENTITY_CHAST_MODE_FOLLOW, new Object[0]).getFormattedText();
		}

		return label;
	}

}
