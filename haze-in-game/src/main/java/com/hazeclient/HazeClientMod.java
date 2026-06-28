package com.hazeclient;

import com.hazeclient.gui.ClickGUI;
import com.hazeclient.modules.Module;
import com.hazeclient.modules.ModuleRegistry;
import com.hazeclient.modules.impl.ToggleSprintModule;
import com.hazeclient.modules.impl.ToggleSneakModule;
import com.hazeclient.modules.impl.ZoomModule;
import com.hazeclient.modules.impl.FreelookModule;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HazeClientMod implements ClientModInitializer {
    public static final String MOD_ID = "hazeclient";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static KeyBinding clickGuiKeyBind;
    private static KeyBinding hudEditorKeyBind;
    private static KeyBinding zoomKeyBind;
    private static KeyBinding freelookKeyBind;

    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing Haze Client Core...");
        
        com.hazeclient.ipc.HazeIPCClient.initialize();
        ModuleRegistry.initialize();

        // ─── Key Bindings ───────────────────────────────────────────

        clickGuiKeyBind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.hazeclient.clickgui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "category.hazeclient.ui"
        ));

        hudEditorKeyBind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.hazeclient.hudeditor",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_CONTROL,
                "category.hazeclient.ui"
        ));

        zoomKeyBind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.hazeclient.zoom",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_C,
                "category.hazeclient.features"
        ));

        freelookKeyBind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.hazeclient.freelook",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_LEFT_ALT,
                "category.hazeclient.features"
        ));

        // ─── Tick Event Handler ─────────────────────────────────────

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // GUI toggles
            while (clickGuiKeyBind.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new ClickGUI());
                } else if (client.currentScreen instanceof ClickGUI) {
                    client.setScreen(null);
                }
            }

            while (hudEditorKeyBind.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new com.hazeclient.gui.HUDEditor());
                } else if (client.currentScreen instanceof com.hazeclient.gui.HUDEditor) {
                    client.setScreen(null);
                }
            }

            // Zoom (hold key)
            Module zoomMod = ModuleRegistry.getModuleByName("Zoom");
            if (zoomMod instanceof ZoomModule zoom && zoomMod.isEnabled()) {
                // Handled in mixin
            }

            // Freelook (hold key)
            Module freelookMod = ModuleRegistry.getModuleByName("Freelook");
            if (freelookMod instanceof FreelookModule freelook && freelookMod.isEnabled()) {
                // Handled in mixin
            }
        });

        // ─── HUD Render Callback ────────────────────────────────────

        net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.currentScreen == null || mc.currentScreen instanceof com.hazeclient.gui.HUDEditor) {
                java.util.List<Module> hudMods = ModuleRegistry.getModulesByCategory(Module.Category.HUD);
                for (Module module : hudMods) {
                    if (module.isEnabled()) {
                        module.render(drawContext, tickDelta);
                    }
                }
            }
        });

        LOGGER.info("Haze Client Core initialized — {} modules loaded.", ModuleRegistry.getModules().size());
    }
}
