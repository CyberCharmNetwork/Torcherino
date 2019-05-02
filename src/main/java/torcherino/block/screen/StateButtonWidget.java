package torcherino.block.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.Item;

public abstract class StateButtonWidget extends ButtonWidget
{
	private int state;
	private final int MAX_STATES;
	private final Screen screen;
	private final String narrationMessage;
	private static ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();

	public StateButtonWidget(Screen screen, int x, int y, int state, int maxStates, String narrationMessage)
	{
		super(x, y, 20, 20, "", null);
		this.screen = screen;
		this.state = state;
		this.MAX_STATES = maxStates;
		this.narrationMessage= narrationMessage;
	}

	protected abstract Item getStateItem(int state);
	protected abstract String getStateName(int state);
	protected abstract void onStateChange(int state);

	@Override public void render(int mouseX, int mouseY, float unused)
	{
		super.render(mouseX, mouseY, unused);
		itemRenderer.renderGuiItem(getStateItem(state).getDefaultStack(), x + 2, y + 2);
		if (this.isHovered) screen.renderTooltip(getStateName(state), x + 14, y + 18);
		GlStateManager.disableRescaleNormal();
		GuiLighting.disable();
		GlStateManager.disableLighting();
		GlStateManager.disableDepthTest();
	}

	@Override protected String getNarrationMessage(){ return I18n.translate(this.narrationMessage, getStateName(state)); }
	@Override public void onPress(){ onStateChange(state =  (state+1) % MAX_STATES); }
}