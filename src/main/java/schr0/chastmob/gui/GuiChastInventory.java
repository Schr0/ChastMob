package schr0.chastmob.gui;

import java.io.IOException;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import schr0.chastmob.ChastMob;
import schr0.chastmob.entity.EntityChast;
import schr0.chastmob.init.ChastMobItems;
import schr0.chastmob.init.ChastMobMessages;
import schr0.chastmob.item.ItemCore;
import schr0.chastmob.packet.MessageGuiChastInventory;

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

		// AIボタン
		int buttonId = 0;
		int buttonPosX = ((this.width - this.xSize) / 2) + 121;
		int buttonPosY = ((this.height - this.ySize) / 2) + 41;
		GuiChastInventory.ChangeButton changeButton = new GuiChastInventory.ChangeButton(buttonId, buttonPosX, buttonPosY);

		this.buttonChange = (GuiChastInventory.ChangeButton) this.addButton(changeButton);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float renderPartialTicks, int xMouse, int yMouse)
	{
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(RES_CHAST_INVENTORY);
		int originPosX = (this.width - this.xSize) / 2;
		int originPosY = (this.height - this.ySize) / 2;

		// 全画面
		int drawX = originPosX;
		int drawY = originPosY;
		int textureX = 0;
		int textureY = 0;
		int width = this.xSize;
		int height = this.ySize;
		this.drawTexturedModalRect(drawX, drawY, textureX, textureY, width, height);

		// ホームチェストアイコン
		drawX = (originPosX + 93);
		drawY = (originPosY + 22);
		textureX = 184;
		textureY = this.getHomeChestTextureY();
		width = 16;
		height = 16;
		this.drawTexturedModalRect(drawX, drawY, textureX, textureY, width, height);

		// フィルターアイコン
		drawX = (originPosX + 117);
		drawY = (originPosY + 22);
		textureX = 184;
		textureY = this.getFilterTextureY();
		width = 16;
		height = 16;
		this.drawTexturedModalRect(drawX, drawY, textureX, textureY, width, height);

		// モブ表示
		int entityPosX = (originPosX + 51);
		int entityPosY = (originPosY + 60);
		int scale = 25;
		float mouseX = (float) (entityPosX - xMouse);
		float mouseY = (float) ((entityPosY / 2) - yMouse);
		EntityLivingBase screenEntity = this.entityChast;
		GuiInventory.drawEntityOnScreen(entityPosX, entityPosY, scale, mouseX, mouseY, screenEntity);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int xMouse, int yMouse)
	{
		// モブの名前
		String nameChast = this.entityChast.getName();
		this.fontRenderer.drawString(nameChast, this.xSize / 2 - this.fontRenderer.getStringWidth(nameChast) / 2, 6, 4210752);

		// インベントリの名前
		String nameInventory = this.entityPlayer.inventory.getDisplayName().getUnformattedText();
		this.fontRenderer.drawString(nameInventory, 8, 128, 4210752);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		super.drawScreen(mouseX, mouseY, partialTicks);

		// HPアイコン
		ItemStack renderItem = new ItemStack(ChastMobItems.CORE);
		((ItemCore) renderItem.getItem()).setCondition(renderItem, this.entityChast.getCondition());

		int xPosition = ((this.width - this.xSize) / 2) + 141;
		int yPosition = ((this.height - this.ySize) / 2) + 22;
		this.itemRender.renderItemAndEffectIntoGUI(renderItem, xPosition, yPosition);

		// モードアイコン
		renderItem = entityChast.getMode().getIconItem();
		xPosition = ((this.width - this.xSize) / 2) + 98;
		yPosition = ((this.height - this.ySize) / 2) + 46;
		this.itemRender.renderItemAndEffectIntoGUI(renderItem, xPosition, yPosition);

		// モードツールチップ
		int rectX = 98;
		int rectY = 46;
		int width = 16;
		int height = 16;
		if (this.isPointInRegion(rectX, rectY, width, height, mouseX, mouseY))
		{
			List<String> texts = Lists.newArrayList();
			TextComponentTranslation title = new TextComponentTranslation("entity.chast.gui.mode", new Object[0]);

			title.getStyle().setColor(TextFormatting.AQUA);

			texts.add(title.getFormattedText());
			texts.add(this.entityChast.getMode().getLabel());

			this.drawHoveringText(texts, mouseX, mouseY);
		}

		// ホームチェストツールチップ
		rectX = 93;
		rectY = 22;
		width = 16;
		height = 16;
		if (this.isPointInRegion(rectX, rectY, width, height, mouseX, mouseY))
		{
			List<String> texts = Lists.newArrayList();
			TextComponentTranslation title = new TextComponentTranslation("entity.chast.gui.home", new Object[0]);

			title.getStyle().setColor(TextFormatting.AQUA);

			texts.add(title.getFormattedText());

			if (this.hasHomeChest())
			{
				BlockPos pos = this.entityChast.getHomeChestPosition();

				texts.add("posX : " + pos.getX());
				texts.add("posY : " + pos.getY());
				texts.add("posZ : " + pos.getZ());
			}
			else
			{
				texts.add("NONE");
			}

			this.drawHoveringText(texts, mouseX, mouseY);
		}

		// フィルターツールチップ
		rectX = 117;
		rectY = 22;
		width = 16;
		height = 16;
		if (this.isPointInRegion(rectX, rectY, width, height, mouseX, mouseY))
		{
			List<String> texts = Lists.newArrayList();
			TextComponentTranslation title = new TextComponentTranslation("entity.chast.gui.filter", new Object[0]);
			TextComponentTranslation textWhite = new TextComponentTranslation("entity.chast.gui.filter.white", new Object[0]);
			TextComponentTranslation textBlack = new TextComponentTranslation("entity.chast.gui.filter.black", new Object[0]);

			title.getStyle().setColor(TextFormatting.AQUA);

			texts.add(title.getFormattedText());

			if (this.hasFilter())
			{
				texts.add(textWhite.getFormattedText() + " : " + this.getFilterWhite());
				texts.add(textBlack.getFormattedText() + " : " + this.getFilterBlack());
			}
			else
			{
				texts.add("NONE");
			}

			this.drawHoveringText(texts, mouseX, mouseY);
		}

		// HPツールチップ
		rectX = 141;
		rectY = 22;
		width = 16;
		height = 16;
		if (this.isPointInRegion(rectX, rectY, width, height, mouseX, mouseY))
		{
			List<String> texts = Lists.newArrayList();
			TextComponentTranslation title = new TextComponentTranslation("entity.chast.gui.health", new Object[0]);
			title.getStyle().setColor(TextFormatting.AQUA);

			texts.add(title.getFormattedText());

			texts.add(this.entityChast.getHealth() + "/" + this.entityChast.getMaxHealth());

			this.drawHoveringText(texts, mouseX, mouseY);
		}

		// ツールチップ表示
		this.renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException
	{
		if (button == this.buttonChange)
		{
			ChastMobMessages.DISPATCHER.sendToServer(new MessageGuiChastInventory(this.entityChast));

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
			super(buttonId, x, y, 35, 26, "");

			this.buttonTextureY = 0;
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
			this.buttonTextureY = 0;
		}

		public void mouseClicked()
		{
			this.buttonTextureY = 32;
		}

	}

	private boolean hasHomeChest()
	{
		return (this.entityChast.getHomeChestPosition() != BlockPos.ORIGIN);
	}

	private int getHomeChestTextureY()
	{
		if (this.hasHomeChest())
		{
			return 88;
		}

		return 64;
	}

	private boolean hasFilter()
	{
		return (!this.entityChast.getHeldItemMainhand().isEmpty() || !this.entityChast.getHeldItemOffhand().isEmpty());
	}

	private int getFilterTextureY()
	{
		if (this.hasFilter())
		{
			return 136;
		}

		return 112;
	}

	private String getFilterWhite()
	{
		ItemStack stack = this.entityChast.getHeldItemMainhand();

		if (stack.isEmpty())
		{
			return "NONE";
		}

		return stack.getDisplayName();
	}

	private String getFilterBlack()
	{
		ItemStack stack = this.entityChast.getHeldItemOffhand();

		if (stack.isEmpty())
		{
			return "NONE";
		}

		return stack.getDisplayName();
	}

}
