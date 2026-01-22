package dev.recode.astro.module.modules.client;

import dev.recode.astro.module.Category;
import dev.recode.astro.module.Module;
import dev.recode.astro.module.settings.ColorSetting;
import dev.recode.astro.screens.ClickGUIScreen1;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW; // Added for the keybind constant

@Environment(EnvType.CLIENT)
public class ClickGuiModule extends Module {

    public final ColorSetting primaryColor;
    public final ColorSetting secondaryColor;
    public final ColorSetting backgroundColor;

    private static final int MODULE_COLOR = 0xFF6969FF;

    public ClickGuiModule() {
        // Calling the parent constructor (Name, Category)
        super("ClickGUI", Category.CLIENT);
        setDescription("Shows the main cheat menu");

        // KEYBIND: This is the missing piece. 
        // GLFW_KEY_RIGHT_SHIFT is the standard for most clients.
        this.setKey(GLFW.GLFW_KEY_RIGHT_SHIFT);

        // Settings Initialization
        primaryColor = new ColorSetting("Primary", 0xFF6969FF, MODULE_COLOR);
        primaryColor.setDescription("Primary/main accent color");

        secondaryColor = new ColorSetting("Secondary", 0xFF303030, MODULE_COLOR);
        secondaryColor.setDescription("Secondary panel color");

        backgroundColor = new ColorSetting("Background", 0xFF202020, MODULE_COLOR);
        backgroundColor.setDescription("Background overlay color");

        // Registering settings so they show up inside the GUI itself
        addSetting(primaryColor);
        addSetting(secondaryColor);
        addSetting(backgroundColor);
    }

    @Override
    public void onEnable() {
        // Prevent opening if the game isn't fully loaded
        if (Minecraft.getInstance().player == null) {
            setEnabled(false);
            return;
        }

        // Initialize the ImGui Screen
        ClickGUIScreen1 guiScreen = new ClickGUIScreen1();

        // This listener ensures that when you ESC out of the menu, 
        // the module "turns off" so you can press RSHIFT to open it again.
        ScreenEvents.AFTER_INIT.register((mc, screen, w, h) -> {
            if (screen == guiScreen) {
                ScreenEvents.remove(screen).register(s -> setEnabled(false));
            }
        });

        // Run on the main thread to set the current screen
        Minecraft.getInstance().execute(() ->
                Minecraft.getInstance().setScreen(guiScreen)
        );
    }
    
    @Override
    public void onDisable() {
        // Ensure the menu closes if the module is toggled off via command
        if (Minecraft.getInstance().screen instanceof ClickGUIScreen1) {
            Minecraft.getInstance().setScreen(null);
        }
    }
}
