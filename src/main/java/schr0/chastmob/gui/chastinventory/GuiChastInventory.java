package schr0.chastmob.gui.chastinventory;

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
import schr0.chastmob.init.ChastMobPackets;
import schr0.chastmob.packet.guichastinventory.MessageGuiChastInventory;

@SideOnly(Side.CLIENT)
public class GuiChastInventory extends GuiContainer
{

	private static final ResourceLocation RES_CHAST_INVENTORY = new ResourceLocation(ChastMob.MOD_RESOURCE_DOMAIN + "textures/gui/chast_inventory.png");

	private EntityChast entityChast;
	private EntityPlayer entityPlayer;
	private GuiChastInventory.ChangeButton buttonChange;

	public GuiChastInventory(EntityChast entityChast, EntityPlayer entityPlayer)
	{
		super(new ContainerChastInventory(entityChast, entityPlayer));

		this.ySize = 222;
		this.entityChast = entityChast;
		this.entityPlayer = entityPlayer;
	}

	@Override
	public void initGui()
	{
		super.initGui();

		int buttonPosX = ((this.width - this.xSize) / 2) + 102;
		int buttonPosY = ((this.height - this.ySize) / 2) + 41;
		this.buttonChange = (GuiChastInventory.ChangeButton) this.addButton(new GuiChastInventory.ChangeButton(0, buttonPosX, buttonPosY));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float renderPartialTicks, int xMouse, int yMouse)
	{
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(RES_CHAST_INVENTORY);

		int originPosX = (this.width - this.xSize) / 2;
		int originPosY = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(originPosX, originPosY, 0, 0, this.xSize, this.ySize);

		this.drawTexturedModalRect((originPosX + 104), (originPosY + 24), 184, this.getHealthTextureY(), this.getHealthBar(), 10);

		int entityPosX = (originPosX + 51);
		int entityPosY = (originPosY + 60);
		GuiInventory.drawEntityOnScreen(entityPosX, entityPosY, 25, (float) (entityPosX - xMouse), (float) ((entityPosY / 2) - yMouse), this.entityChast);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int xMouse, int yMouse)
	{
		String nameChast = this.entityChast.getName() + " / " + this.entityChast.getAIMode().getLabel();
		this.fontRenderer.drawString(nameChast, this.xSize / 2 - this.fontRenderer.getStringWidth(nameChast) / 2, 6, 4210752);
		this.fontRenderer.drawString(this.entityPlayer.inventory.getDisplayName().getUnformattedText(), 8, 128, 4210752);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		super.drawScreen(mouseX, mouseY, partialTicks);

		this.renderHoveredToolTip(mouseX, mouseY);

		if (this.isPointInRegion(102, 22, 64, 14, mouseX, mouseY))
		{
			this.drawHoveringText(Lists.newArrayList(this.entityChast.getHealth() + " / " + this.entityChast.getMaxHealth()), mouseX, mouseY);
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException
	{
		if (button == this.buttonChange)
		{
			ChastMobPackets.DISPATCHER.sendToServer(new MessageGuiChastInventory(this.entityChast));

			((ChangeButton) button).mouseClicked();
		}
	}

	// TODO /* ======================================== MOD START =====================================*/

	@SideOnly(Side.CLIENT)
	private static class ChangeButton extends GuiButton
	{
		private int buttonTextureY;

		public ChangeButton(int buttonId, int x, int y)
		{
			super(buttonId, x, y, 44, 26, "");

			this.buttonTextureY = 56;
		}

		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks)
		{
			if (this.visible)
			{
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

				mc.getTextureManager().bindTexture(RES_CHAST_INVENTORY);

				this.drawTexturedModalRect(this.x, this.y, 184, this.buttonTextureY, this.width, this.height);
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

	private int getHealthTextureY()
	{
		int healthTextureY;

		switch (this.entityChast.getCondition())
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

		return healthTextureY;
	}

	private int getHealthBar()
	{
		int health = (int) this.entityChast.getHealth();
		int healthMax = (int) this.entityChast.getMaxHealth();

		return Math.min(60, (60 - ((healthMax - health) * 3)));
	}

}
