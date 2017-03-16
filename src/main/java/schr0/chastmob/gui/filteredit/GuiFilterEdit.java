package schr0.chastmob.gui.filteredit;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import schr0.chastmob.ChastMob;
import schr0.chastmob.init.ChastMobPacket;
import schr0.chastmob.inventory.InventoryFilter;
import schr0.chastmob.item.ItemFilter;
import schr0.chastmob.packet.buttonedit.MessageButtonEdit;

@SideOnly(Side.CLIENT)
public class GuiFilterEdit extends GuiContainer
{

	private static final ResourceLocation RES_FILTER_EDIT = new ResourceLocation(ChastMob.MOD_RESOURCE_DOMAIN + "textures/gui/filter_edit.png");

	private InventoryFilter inventoryFilter;
	private EntityPlayer entityPlayer;
	private GuiFilterEdit.RegistryButton buttonRegistry;

	public GuiFilterEdit(IInventory inventory, ItemStack stack, EntityPlayer entityPlayer)
	{
		super(new ContainerFilterEdit(inventory, stack, entityPlayer));

		this.ySize = 178;
		this.inventoryFilter = ((ItemFilter) stack.getItem()).getInventoryFilter(stack);
		this.entityPlayer = entityPlayer;
	}

	@Override
	public void initGui()
	{
		super.initGui();

		int buttonPosX = ((this.width - this.xSize) / 2) + 74;
		int buttonPosY = ((this.height - this.ySize) / 2) + 42;
		this.buttonRegistry = (GuiFilterEdit.RegistryButton) this.addButton(new GuiFilterEdit.RegistryButton(0, buttonPosX, buttonPosY));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float renderPartialTicks, int xMouse, int yMouse)
	{
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(RES_FILTER_EDIT);

		int originPosX = (this.width - this.xSize) / 2;
		int originPosY = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(originPosX, originPosY, 0, 0, this.xSize, this.ySize);

		int resultTextureY = 8;

		if (this.inventoryFilter.getFilterType() == ItemFilter.Type.BLACK)
		{
			resultTextureY = 80;
		}

		this.drawTexturedModalRect((originPosX + 102), (originPosY + 17), 184, resultTextureY, 66, 66);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int xMouse, int yMouse)
	{
		String nameChast = this.inventoryFilter.getName();
		this.fontRendererObj.drawString(nameChast, this.xSize / 2 - this.fontRendererObj.getStringWidth(nameChast) / 2, 6, 4210752);
		this.fontRendererObj.drawString(this.entityPlayer.inventory.getDisplayName().getUnformattedText(), 8, 84, 4210752);
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		super.drawScreen(mouseX, mouseY, partialTicks);

		if (!this.inventoryFilter.isEmpty())
		{
			int originPosX = (this.width - this.xSize) / 2;
			int originPosY = (this.height - this.ySize) / 2;
			InventoryBasic inventoryFilterCopy = new InventoryBasic("", true, 9);

			for (int slot = 0; slot < this.inventoryFilter.getSizeInventory(); ++slot)
			{
				inventoryFilterCopy.setInventorySlotContents(slot, this.inventoryFilter.getStackInSlot(slot));
			}

			int column;
			int row;
			int index;

			for (column = 0; column < 3; ++column)
			{
				for (row = 0; row < 3; ++row)
				{
					int slotPosX = originPosX + (107 + row * 20);
					int slotPosY = originPosY + (22 + column * 20);
					index = (row + column * 3);
					ItemStack stackSlot = inventoryFilterCopy.getStackInSlot(index);

					GlStateManager.pushMatrix();
					RenderHelper.enableGUIStandardItemLighting();
					GlStateManager.disableLighting();
					GlStateManager.enableRescaleNormal();
					GlStateManager.enableColorMaterial();
					GlStateManager.enableLighting();
					this.itemRender.zLevel = 100.0F;
					this.itemRender.renderItemAndEffectIntoGUI(stackSlot, slotPosX, slotPosY);
					this.itemRender.renderItemOverlays(this.fontRendererObj, stackSlot, slotPosX, slotPosY);
					this.itemRender.zLevel = 0.0F;
					GlStateManager.disableLighting();
					GlStateManager.popMatrix();
					GlStateManager.enableLighting();
					GlStateManager.enableDepth();
					RenderHelper.enableStandardItemLighting();
				}
			}

			for (column = 0; column < 3; ++column)
			{
				for (row = 0; row < 3; ++row)
				{
					int slotPosX = originPosX + (107 + row * 20);
					int slotPosY = originPosY + (22 + column * 20);
					index = (row + column * 3);
					ItemStack stackSlot = inventoryFilterCopy.getStackInSlot(index);

					if (this.isPointInRegion((107 + row * 20), (22 + column * 20), 16, 16, mouseX, mouseY) && !stackSlot.isEmpty())
					{
						this.renderToolTip(stackSlot, mouseX, mouseY);
					}
				}
			}
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException
	{
		if (button == this.buttonRegistry)
		{
			ChastMobPacket.DISPATCHER.sendToServer(new MessageButtonEdit(this.entityPlayer));

			((RegistryButton) button).mouseClicked();
		}
	}

	// TODO /* ======================================== MOD START =====================================*/

	@SideOnly(Side.CLIENT)
	public static class RegistryButton extends GuiButton
	{
		private int buttonTextureY;

		public RegistryButton(int buttonId, int x, int y)
		{
			super(buttonId, x, y, 27, 19, "");

			this.buttonTextureY = 152;
		}

		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY)
		{
			if (this.visible)
			{
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

				mc.getTextureManager().bindTexture(RES_FILTER_EDIT);

				this.drawTexturedModalRect(this.xPosition, this.yPosition, 184, this.buttonTextureY, this.width, this.height);
			}
		}

		@Override
		public void mouseReleased(int mouseX, int mouseY)
		{
			this.buttonTextureY = 152;
		}

		// TODO /* ======================================== MOD START =====================================*/

		public void mouseClicked()
		{
			this.buttonTextureY = 176;
		}

	}

}
