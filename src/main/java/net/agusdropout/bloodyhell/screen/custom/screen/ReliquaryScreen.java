package net.agusdropout.bloodyhell.screen.custom.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.screen.custom.menu.ReliquaryMenu;
import net.agusdropout.bloodyhell.screen.custom.menu.SanguineLapidaryMenu;
import net.agusdropout.bloodyhell.util.MouseUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ReliquaryScreen extends AbstractContainerScreen<ReliquaryMenu> {
    private static final ResourceLocation GUI_BASE = new ResourceLocation("bloodyhell", "textures/gui/reliquary_gui_base.png");
    private static final ResourceLocation GUI_TIER_1 = new ResourceLocation("bloodyhell", "textures/gui/reliquary_gui_tier_1.png");
    private static final ResourceLocation GUI_TIER_2 = new ResourceLocation("bloodyhell", "textures/gui/reliquary_gui_tier_2.png");

    public ReliquaryScreen(ReliquaryMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
        super.imageHeight = 169;
    }

    @Override
    protected void init() {
        super.init();

    }


    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int pMouseX, int pMouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        renderEnergyAreaTooltips(guiGraphics, pMouseX, pMouseY, x, y);
    }

    private void renderEnergyAreaTooltips(GuiGraphics guiGraphics, int pMouseX, int pMouseY, int x, int y) {
        if(isMouseAboveArea(pMouseX, pMouseY, x, y, 55, 15, 16, 62)) {
            renderTooltip(guiGraphics, pMouseX - x, pMouseY - y);
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        int currentTier = this.menu.getUpgradeTier();
        ResourceLocation activeTexture = switch (currentTier) {
            case 1 -> GUI_TIER_1;
            case 2 -> GUI_TIER_2;
            default -> GUI_BASE;
        };

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(activeTexture, x, y, 0, 0, imageWidth, imageHeight);

        drawCapacityBar(guiGraphics, x, y, activeTexture);


    }

    private void drawCapacityBar(GuiGraphics guiGraphics, int x , int y, ResourceLocation activeTexture) {
        int usedCapacity = this.menu.getUsedCapacity();
        int maxCapacity = this.menu.getMaxCapacity();

        if (maxCapacity > 0) {
            int maxBarWidth = 113;
            int barHeight = 2;

            int renderX = x + 22;
            int renderY = y + 22;

            int textureU = 22;
            int textureV = 170;

            int filledWidth = (int) (((float) usedCapacity / maxCapacity) * maxBarWidth);

            guiGraphics.blit(activeTexture, renderX, renderY, textureU, textureV, filledWidth, barHeight);
        }
    }



    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    private boolean isMouseAboveArea(int pMouseX, int pMouseY, int x, int y, int offsetX, int offsetY, int width, int height) {
        return MouseUtil.isMouseOver(pMouseX, pMouseY, x + offsetX, y + offsetY, width, height);
    }
}
