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
		this.fontRendererObj.drawString(this.thePlayer.inventory.getDisplayName().getUnformattedText(), 8, 134, 4210752);

		String name = "FOLLOW";
		this.fontRendererObj.drawString(name, 104, 39, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float renderPartialTicks, int xMouse, int yMouse)
	{
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		this.mc.getTextureManager().bindTexture(RES_CHAST_STATUS);

		int oX = (this.width - this.xSize) / 2;
		int oY = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(oX, oY, 0, 0, this.xSize, this.ySize);

		int gX = (oX + 51);
		int gY = (oY + 60);
		GuiInventory.drawEntityOnScreen(gX, gY, 25, (float) (gX - xMouse), (float) ((gY / 2) - yMouse), this.theChast);
	}

}
