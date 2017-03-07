package schr0.chastmob.entity.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import schr0.chastmob.ChastMob;
import schr0.chastmob.entity.EntityChast;

@SideOnly(Side.CLIENT)
public class GuiChastInventory extends GuiContainer
{

	private static final ResourceLocation RES_CHAST_STATUS = new ResourceLocation(ChastMob.MOD_RESOURCE_DOMAIN + "textures/gui/chast_inventory.png");
	private EntityChast theChast;
	private EntityPlayer thePlayer;

	public GuiChastInventory(EntityChast entityChast, EntityPlayer entityPlayer)
	{
		super(new ContainerChastInventory(entityChast, entityPlayer));
		this.xSize = 176;
		this.ySize = 228;

		this.theChast = entityChast;
		this.thePlayer = entityPlayer;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int xMouse, int yMouse)
	{
		this.fontRendererObj.drawString(this.theChast.getName(), 8, 6, 4210752);
		this.fontRendererObj.drawString(this.thePlayer.inventory.getDisplayName().getUnformattedText(), 8, 130, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float renderPartialTicks, int xMouse, int yMouse)
	{
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		this.mc.getTextureManager().bindTexture(RES_CHAST_STATUS);

		int originPosX = (this.width - this.xSize) / 2;
		int originPosY = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(originPosX, originPosY, 0, 0, this.xSize, this.ySize);

		int health = (int) this.theChast.getHealth();
		int healthMax = (int) this.theChast.getMaxHealth();
		int healthPosY = 8;

		if (health < (healthMax / 2))
		{
			healthPosY = 24;

			if (health < (healthMax / 4))
			{
				healthPosY = 40;
			}
		}

		this.drawTexturedModalRect((originPosX + 116), (originPosY + 21), 184, healthPosY, (40 - ((healthMax * 2) - (health * 2))), 10);

		int gX = (originPosX + 64);
		int gY = (originPosY + 60);
		GuiInventory.drawEntityOnScreen(gX, gY, 25, (float) (gX - xMouse), (float) ((gY / 2) - yMouse), this.theChast);
	}

}
