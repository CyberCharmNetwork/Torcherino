package torcherino.client.screen.widgets;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public abstract class StateButtonWidget extends ButtonWidget
{
    private static ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
    private final Screen screen;
    private String narrationMessage;

    public StateButtonWidget(Screen screen, int x, int y)
    {
        super(x, y, 20, 20, "", null);
        this.screen = screen;
        initialize();
    }

    protected abstract void initialize();

    protected abstract void nextState();

    protected abstract ItemStack getButtonIcon();

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        if (visible)
        {
            super.render(mouseX, mouseY, partialTicks);
            itemRenderer.renderGuiItem(getButtonIcon(), x + 2, y + 2);
            if (this.isHovered)
            {
                screen.renderTooltip(Lists.asList(narrationMessage, new String[]{}), x + 14, y + 18);
                GlStateManager.disableRescaleNormal();
                GuiLighting.disable();
                GlStateManager.disableLighting();
                GlStateManager.disableDepthTest();
            }
        }
    }

    @Override
    public void onPress() { nextState(); }

    @Override
    public String getNarrationMessage() { return new TranslatableText("gui.narrate.button", narrationMessage).asString(); }

    protected void setNarrationMessage(String narrationMessage) { this.narrationMessage = narrationMessage; }
}