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
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import schr0.chastmob.ChastMob;
import schr0.chastmob.entity.EntityChast;
import schr0.chastmob.entity.EnumHealthState;
import schr0.chastmob.init.ChastMobLang;
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

		int buttonPosX = ((this.width - this.xSize) / 2) + 121;
		int buttonPosY = ((this.height - this.ySize) / 2) + 41;
		this.buttonChageAIState = (GuiChastInventory.ChageAIStateButton) this.func_189646_b(new GuiChastInventory.ChageAIStateButton(1, buttonPosX, buttonPosY, 44, 26));
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
		int healthTextureY = 8;
		int healthWidth = Math.min(40, (40 - ((healthMax * 2) - (health * 2))));

		if (this.theChast.getHealthState() == EnumHealthState.HURT)
		{
			healthTextureY = 24;

			if (this.theChast.getHealthState() == EnumHealthState.DYING)
			{
				healthTextureY = 40;
			}
		}

		this.drawTexturedModalRect((originPosX + 123), (originPosY + 24), 184, healthTextureY, healthWidth, 10);

		int entityPosX = (originPosX + 51);
		int entityPosY = (originPosY + 60);
		GuiInventory.drawEntityOnScreen(entityPosX, entityPosY, 25, (float) (entityPosX - xMouse), (float) ((entityPosY / 2) - yMouse), this.theChast);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int xMouse, int yMouse)
	{
		String nameChast = this.theChast.getName() + " / " + this.theChast.getAIState().getName();
		this.fontRendererObj.drawString(nameChast, this.xSize / 2 - this.fontRendererObj.getStringWidth(nameChast) / 2, 6, 4210752);
		this.fontRendererObj.drawString(this.thePlayer.inventory.getDisplayName().getUnformattedText(), 8, 128, 4210752);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		super.drawScreen(mouseX, mouseY, partialTicks);

		if (this.isPointInRegion(121, 22, 44, 14, mouseX, mouseY))
		{
			String health = this.theChast.getHealth() + " / " + this.theChast.getMaxHealth();
			this.drawHoveringText(Lists.newArrayList(health), mouseX, mouseY);
		}

		if (this.isPointInRegion(121, 41, 44, 26, mouseX, mouseY))
		{
			String button = new TextComponentTranslation(ChastMobLang.ENTITY_CHAST_GUI_BUTTON, new Object[0]).getFormattedText();
			this.drawHoveringText(Lists.newArrayList(button), mouseX, mouseY);
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException
	{
		if (button == this.buttonChageAIState)
		{
			ChastMobPacket.DISPATCHER.sendToServer(new MessageButtonAction(this.theChast));
		}
	}

	// TODO /* ======================================== MOD START =====================================*/

	@SideOnly(Side.CLIENT)
	public static class ChageAIStateButton extends GuiButton
	{
		public ChageAIStateButton(int buttonId, int x, int y, int widthIn, int heightIn)
		{
			super(buttonId, x, y, widthIn, heightIn, "");
		}

		public void drawButton(Minecraft mc, int mouseX, int mouseY)
		{
			if (this.visible)
			{
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

				mc.getTextureManager().bindTexture(RES_CHAST_STATUS);

				this.drawTexturedModalRect(this.xPosition, this.yPosition, 184, 56, this.width, this.height);
			}
		}
	}

}
