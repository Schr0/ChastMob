package schr0.chastmob.entity.gui;

import java.io.IOException;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import schr0.chastmob.ChastMob;
import schr0.chastmob.entity.EntityChast;
import schr0.chastmob.init.ChastMobPacket;
import schr0.chastmob.packet.MessageButtonAction;

@SideOnly(Side.CLIENT)
public class GuiChastInventory extends GuiContainer
{

	private static final ResourceLocation RES_CHAST_STATUS = new ResourceLocation(ChastMob.MOD_RESOURCE_DOMAIN + "textures/gui/chast_inventory.png");
	private GuiChastInventory.ChageAIStateButton buttonChageAIState;
	private EntityChast theChast;
	private EntityPlayer thePlayer;

	public GuiChastInventory(EntityChast entityChast, EntityPlayer entityPlayer)
	{
		super(new ContainerChastInventory(entityChast, entityPlayer));
		this.xSize = 176;
		this.ySize = 222;

		this.theChast = entityChast;
		this.thePlayer = entityPlayer;
	}

	@Override
	public void initGui()
	{
		super.initGui();

		int buttonPosX = ((this.width - this.xSize) / 2) + 102;
		int buttonPosY = ((this.height - this.ySize) / 2) + 41;
		this.buttonChageAIState = (GuiChastInventory.ChageAIStateButton) this.func_189646_b(new GuiChastInventory.ChageAIStateButton(0, buttonPosX, buttonPosY, 44, 26));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float renderPartialTicks, int xMouse, int yMouse)
	{
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(RES_CHAST_STATUS);

		int originPosX = (this.width - this.xSize) / 2;
		int originPosY = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(originPosX, originPosY, 0, 0, this.xSize, this.ySize);

		int healthTextureY;

		switch (this.theChast.getHealthState())
		{
			case HURT :

				healthTextureY = 24;

				break;

			case DYING :

				healthTextureY = 40;

				break;

			default :

				healthTextureY = 8;
		}

		this.drawTexturedModalRect((originPosX + 104), (originPosY + 24), 184, healthTextureY, this.getHealthBar(), 10);

		int entityPosX = (originPosX + 51);
		int entityPosY = (originPosY + 60);
		GuiInventory.drawEntityOnScreen(entityPosX, entityPosY, 25, (float) (entityPosX - xMouse), (float) ((entityPosY / 2) - yMouse), this.theChast);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int xMouse, int yMouse)
	{
		String nameChast = this.theChast.getName() + " / " + this.theChast.getAIMode().getLabel();
		this.fontRendererObj.drawString(nameChast, this.xSize / 2 - this.fontRendererObj.getStringWidth(nameChast) / 2, 6, 4210752);
		this.fontRendererObj.drawString(this.thePlayer.inventory.getDisplayName().getUnformattedText(), 8, 128, 4210752);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		super.drawScreen(mouseX, mouseY, partialTicks);

		if (this.isPointInRegion(102, 22, 64, 14, mouseX, mouseY))
		{
			this.drawHoveringText(Lists.newArrayList(this.theChast.getHealth() + " / " + this.theChast.getMaxHealth()), mouseX, mouseY);
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException
	{
		if (button.equals(this.buttonChageAIState))
		{
			ChastMobPacket.DISPATCHER.sendToServer(new MessageButtonAction(this.theChast));

			((ChageAIStateButton) button).mouseClicked();
		}
	}

	// TODO /* ======================================== MOD START =====================================*/

	private int getHealthBar()
	{
		int health = (int) this.theChast.getHealth();
		int healthMax = (int) this.theChast.getMaxHealth();

		return Math.min(60, (60 - ((healthMax - health) * 3)));
	}

	@SideOnly(Side.CLIENT)
	public static class ChageAIStateButton extends GuiButton
	{
		private int buttonTextureY;

		public ChageAIStateButton(int buttonId, int x, int y, int widthIn, int heightIn)
		{
			super(buttonId, x, y, widthIn, heightIn, "");

			this.buttonTextureY = 56;
		}

		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY)
		{
			if (this.visible)
			{
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

				mc.getTextureManager().bindTexture(RES_CHAST_STATUS);

				this.drawTexturedModalRect(this.xPosition, this.yPosition, 184, this.buttonTextureY, this.width, this.height);
			}
		}

		@Override
		public void mouseReleased(int mouseX, int mouseY)
		{
			this.buttonTextureY = 56;
		}

		// TODO /* ======================================== MOD START =====================================*/

		public void mouseClicked()
		{
			this.buttonTextureY = 88;
		}

	}

}
