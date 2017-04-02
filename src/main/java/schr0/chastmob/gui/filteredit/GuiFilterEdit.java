package schr0.chastmob.gui.filteredit;

import java.io.IOException;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import schr0.chastmob.ChastMob;
import schr0.chastmob.ChastMobHelper;
import schr0.chastmob.init.ChastMobLang;
import schr0.chastmob.init.ChastMobPacket;
import schr0.chastmob.inventory.InventoryFilterEdit;
import schr0.chastmob.inventory.InventoryFilterResult;
import schr0.chastmob.item.ItemFilter;
import schr0.chastmob.packet.buttonedit.MessageButtonEdit;

@SideOnly(Side.CLIENT)
public class GuiFilterEdit extends GuiContainer
{

	private static final ResourceLocation RES_FILTER_EDIT = new ResourceLocation(ChastMob.MOD_RESOURCE_DOMAIN + "textures/gui/filter_edit.png");

	private InventoryFilterEdit inventoryFilterEdit;
	private InventoryFilterResult inventoryFilterResult;
	private EntityPlayer entityPlayer;
	private GuiFilterEdit.RegistryButton buttonRegistry;

	public GuiFilterEdit(ItemStack stack, InventoryFilterEdit inventoryFilterEdit, InventoryFilterResult inventoryFilterResult, EntityPlayer entityPlayer)
	{
		super(new ContainerFilterEdit(stack, inventoryFilterEdit, inventoryFilterResult, entityPlayer));

		this.ySize = 178;
		this.inventoryFilterEdit = inventoryFilterEdit;
		this.inventoryFilterResult = inventoryFilterResult;
		this.entityPlayer = entityPlayer;
	}

	@Override
	public void initGui()
	{
		super.initGui();

		int buttonPosX = ((this.width - this.xSize) / 2) + 74;
		int buttonPosY = ((this.height - this.ySize) / 2) + 42;
		this.buttonRegistry = (GuiFilterEdit.RegistryButton) this.addButton(new GuiFilterEdit.RegistryButton(this.inventoryFilterEdit, 0, buttonPosX, buttonPosY));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float renderPartialTicks, int xMouse, int yMouse)
	{
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		this.mc.getTextureManager().bindTexture(RES_FILTER_EDIT);

		int originPosX = (this.width - this.xSize) / 2;
		int originPosY = (this.height - this.ySize) / 2;

		this.drawTexturedModalRect(originPosX, originPosY, 0, 0, this.xSize, this.ySize);

		int resultTextureY;

		if (this.inventoryFilterResult.getType() == ItemFilter.Type.BLACK)
		{
			resultTextureY = 80;
		}
		else
		{
			resultTextureY = 8;
		}

		this.drawTexturedModalRect((originPosX + 102), (originPosY + 17), 184, resultTextureY, 66, 66);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int xMouse, int yMouse)
	{
		String nameChast = this.inventoryFilterResult.getName();
		this.fontRendererObj.drawString(nameChast, this.xSize / 2 - this.fontRendererObj.getStringWidth(nameChast) / 2, 6, 4210752);
		this.fontRendererObj.drawString(this.entityPlayer.inventory.getDisplayName().getUnformattedText(), 8, 84, 4210752);
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		super.drawScreen(mouseX, mouseY, partialTicks);

		int originPosX = (this.width - this.xSize) / 2;
		int originPosY = (this.height - this.ySize) / 2;

		if (!this.inventoryFilterResult.isEmpty())
		{
			int column;
			int row;
			int slot;

			GlStateManager.pushMatrix();
			RenderHelper.enableGUIStandardItemLighting();
			GlStateManager.disableLighting();
			GlStateManager.enableRescaleNormal();
			GlStateManager.enableColorMaterial();
			GlStateManager.enableLighting();

			for (column = 0; column < 3; ++column)
			{
				for (row = 0; row < 3; ++row)
				{
					int slotPosX = originPosX + (107 + row * 20);
					int slotPosY = originPosY + (22 + column * 20);
					slot = (row + column * 3);
					ItemStack stackSlot = this.inventoryFilterResult.getStackInSlot(slot);

					this.itemRender.zLevel = 100.0F;
					this.itemRender.renderItemAndEffectIntoGUI(stackSlot, slotPosX, slotPosY);
					this.itemRender.renderItemOverlays(this.fontRendererObj, stackSlot, slotPosX, slotPosY);
					this.itemRender.zLevel = 0.0F;
				}
			}

			for (column = 0; column < 3; ++column)
			{
				for (row = 0; row < 3; ++row)
				{
					int slotPosX = originPosX + (107 + row * 20);
					int slotPosY = originPosY + (22 + column * 20);
					slot = (row + column * 3);
					ItemStack stackSlot = this.inventoryFilterResult.getStackInSlot(slot);

					if (this.isPointInRegion((107 + row * 20), (22 + column * 20), 16, 16, mouseX, mouseY) && ChastMobHelper.isNotEmptyItemStack(stackSlot))
					{
						this.renderToolTip(stackSlot, mouseX, mouseY);
					}
				}
			}

			GlStateManager.disableLighting();
			GlStateManager.popMatrix();
			GlStateManager.enableLighting();
			GlStateManager.enableDepth();
			RenderHelper.enableStandardItemLighting();
		}

		if (this.isPointInRegion(74, 42, 27, 19, mouseX, mouseY))
		{
			List<String> textLines = Lists.<String> newArrayList();

			if (((ContainerFilterEdit) this.inventorySlots).getInventoryFilterEdit().isEmpty())
			{
				textLines.add(new TextComponentTranslation(ChastMobLang.ITEM_FILTER_BUTTON_CLEAR, new Object[0]).getFormattedText());
			}
			else
			{
				textLines.add(new TextComponentTranslation(ChastMobLang.ITEM_FILTER_BUTTON_REGISTRY, new Object[0]).getFormattedText());
			}

			this.drawHoveringText(textLines, mouseX, mouseY);
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException
	{
		if (button == this.buttonRegistry)
		{
			ChastMobPacket.DISPATCHER.sendToServer(new MessageButtonEdit(this.entityPlayer));
		}
	}

	// TODO /* ======================================== MOD START =====================================*/

	@SideOnly(Side.CLIENT)
	public static class RegistryButton extends GuiButton
	{

		private IInventory inventoryEdit;

		public RegistryButton(IInventory inventory, int buttonId, int x, int y)
		{
			super(buttonId, x, y, 27, 19, "");

			this.inventoryEdit = inventory;
		}

		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY)
		{
			if (this.visible)
			{
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

				mc.getTextureManager().bindTexture(RES_FILTER_EDIT);

				int buttonTextureY;

				if (this.inventoryEdit.isEmpty())
				{
					buttonTextureY = 152;
				}
				else
				{
					buttonTextureY = 176;
				}

				this.drawTexturedModalRect(this.xPosition, this.yPosition, 184, buttonTextureY, 27, 19);

			}
		}
	}

}
