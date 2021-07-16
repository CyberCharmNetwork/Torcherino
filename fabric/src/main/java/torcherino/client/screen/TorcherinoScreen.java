package torcherino.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.lwjgl.glfw.GLFW;
import torcherino.Torcherino;
import torcherino.api.Tier;
import torcherino.api.TorcherinoAPI;
import torcherino.client.screen.widgets.FixedSliderWidget;
import torcherino.client.screen.widgets.StateButtonWidget;

@Environment(EnvType.CLIENT)
public class TorcherinoScreen extends Screen {
    private static final ResourceLocation SCREEN_TEXTURE = new ResourceLocation(Torcherino.MOD_ID, "textures/screens/torcherino.png");
    private static final int screenWidth = 245;
    private static final int screenHeight = 123;

    private final BlockPos blockPos;
    private final Tier tier;
    private final Component cached_title;
    private int xRange, zRange, yRange, speed, redstoneMode, left, top;

    public TorcherinoScreen(Component title, int xRange, int zRange, int yRange, int speed, int redstoneMode, BlockPos pos, ResourceLocation tierID) {
        super(title);
        this.tier = TorcherinoAPI.INSTANCE.getTier(tierID);
        this.blockPos = pos;
        this.xRange = xRange;
        this.zRange = zRange;
        this.yRange = yRange;
        this.speed = speed == 0 ? 1 : speed;
        this.redstoneMode = redstoneMode;
        this.cached_title = title;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        left = (width - screenWidth) / 2;
        top = (height - screenHeight) / 2;
        this.addButton(new FixedSliderWidget(left + 8, top + 20, 205, (double) (speed - 1) / (tier.getMaxSpeed() - 1), tier.getMaxSpeed()) {
            @Override
            protected void updateMessage() {
                this.setMessage(new TranslatableComponent("gui.torcherino.speed", 100 * TorcherinoScreen.this.speed));
            }

            @Override
            protected void applyValue() {
                TorcherinoScreen.this.speed = 1 + (int) Math.round(value * (TorcherinoScreen.this.tier.getMaxSpeed() - 1));
                value = (double) (speed - 1) / (tier.getMaxSpeed() - 1);
            }
        });
        this.addButton(new FixedSliderWidget(left + 8, top + 45, 205, (double) xRange / tier.getXZRange(), tier.getXZRange()) {
            @Override
            protected void updateMessage() {
                this.setMessage(new TranslatableComponent("gui.torcherino.x_range", TorcherinoScreen.this.xRange * 2 + 1));
            }

            @Override
            protected void applyValue() {
                TorcherinoScreen.this.xRange = (int) Math.round(value * TorcherinoScreen.this.tier.getXZRange());
                value = (double) xRange / tier.getXZRange();
            }
        });
        this.addButton(new FixedSliderWidget(left + 8, top + 70, 205, (double) zRange / tier.getXZRange(), tier.getXZRange()) {
            @Override
            protected void updateMessage() {
                this.setMessage(new TranslatableComponent("gui.torcherino.z_range", TorcherinoScreen.this.zRange * 2 + 1));
            }

            @Override
            protected void applyValue() {
                TorcherinoScreen.this.zRange = (int) Math.round(value * TorcherinoScreen.this.tier.getXZRange());
                value = (double) zRange / tier.getXZRange();
            }
        });
        this.addButton(new FixedSliderWidget(left + 8, top + 95, 205, (double) yRange / tier.getYRange(), tier.getYRange()) {
            @Override
            protected void updateMessage() {
                this.setMessage(new TranslatableComponent("gui.torcherino.y_range", TorcherinoScreen.this.yRange * 2 + 1));
            }

            @Override
            protected void applyValue() {
                TorcherinoScreen.this.yRange = (int) Math.round(value * TorcherinoScreen.this.tier.getYRange());
                value = (double) yRange / tier.getYRange();
            }
        });
        this.addButton(new StateButtonWidget(this, left + 217, top + 20) {
            ItemStack buttonIcon;

            @Override
            protected void initialize() {
                this.setButtonMessage();
                this.setButtonIcon();
            }

            private void setButtonMessage() {
                String translationKey;
                // todo: should this be replaced with an if statement?
                switch (TorcherinoScreen.this.redstoneMode) {
                    case 0:
                        translationKey = "gui.torcherino.mode.normal";
                        break;
                    case 1:
                        translationKey = "gui.torcherino.mode.inverted";
                        break;
                    case 2:
                        translationKey = "gui.torcherino.mode.ignore";
                        break;
                    case 3:
                        translationKey = "gui.torcherino.mode.off";
                        break;
                    default:
                        translationKey = "gui.torcherino.mode.error";
                        break;
                }
                this.setNarrationMessage(new TranslatableComponent("gui.torcherino.mode", new TranslatableComponent(translationKey)));
            }

            private void setButtonIcon() {
                // todo: should this be replaced with an if statement?
                switch (TorcherinoScreen.this.redstoneMode) {
                    case 0:
                        this.buttonIcon = new ItemStack(Items.REDSTONE);
                        break;
                    case 1:
                        this.buttonIcon = new ItemStack(Items.REDSTONE_TORCH);
                        break;
                    case 2:
                        this.buttonIcon = new ItemStack(Items.GUNPOWDER);
                        break;
                    case 3:
                        this.buttonIcon = new ItemStack(Items.REDSTONE_LAMP);
                        break;
                    default:
                        this.buttonIcon = new ItemStack(Items.FURNACE);
                        break;
                }
            }

            @Override
            protected void nextState() {
                TorcherinoScreen.this.redstoneMode = (TorcherinoScreen.this.redstoneMode + 1) % 4;
                this.initialize();
            }

            @Override
            protected ItemStack getButtonIcon() {
                return buttonIcon;
            }
        });
    }

    @Override
    public void render(PoseStack matrixStack, int x, int y, float partialTicks) {
        this.fillGradient(matrixStack, 0, 0, this.width, this.height, -1072689136, -804253680);
        minecraft.getTextureManager().bind(SCREEN_TEXTURE);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.blit(matrixStack, left, top, 0, 0, screenWidth, screenHeight);
        font.draw(matrixStack, cached_title.getVisualOrderText(), (width - font.width(cached_title)) / 2.0f, top + 6, 4210752);
        super.render(matrixStack, x, y, partialTicks);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE || minecraft.options.keyInventory.matches(keyCode, 0)) {
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose() {
        FriendlyByteBuf packetBuffer = new FriendlyByteBuf(Unpooled.buffer());
        packetBuffer.writeBlockPos(blockPos);
        packetBuffer.writeInt(xRange);
        packetBuffer.writeInt(zRange);
        packetBuffer.writeInt(yRange);
        packetBuffer.writeInt(speed);
        packetBuffer.writeInt(redstoneMode);
        // todo: replace with networking v1
        ClientSidePacketRegistry.INSTANCE.sendToServer(new ResourceLocation(Torcherino.MOD_ID, "utv"), packetBuffer);
        super.onClose();
    }
}
