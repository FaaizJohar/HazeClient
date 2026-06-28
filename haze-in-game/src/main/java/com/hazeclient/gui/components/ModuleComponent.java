package com.hazeclient.gui.components;

import com.hazeclient.modules.Module;
import com.hazeclient.modules.settings.*;
import com.hazeclient.render.HazeAnimation;
import com.hazeclient.render.HazeRenderer;
import com.hazeclient.render.HazeTheme;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

public class ModuleComponent extends GUIComponent {
    private final Module module;
    private final List<GUIComponent> settingComponents = new ArrayList<>();
    
    private boolean expanded = false;
    private final HazeAnimation heightAnim = new HazeAnimation(0f);
    private final HazeAnimation toggleAnim = new HazeAnimation(0f);
    private final HazeAnimation hoverAnim = new HazeAnimation(0f);
    
    private final int MODULE_BTN_HEIGHT = 20;

    public ModuleComponent(Module module, int x, int y, int width) {
        super(x, y, width, 20);
        this.module = module;
        
        buildSettings();
        
        toggleAnim.set(module.isEnabled() ? 1f : 0f);
        heightAnim.set(MODULE_BTN_HEIGHT);
    }

    private void buildSettings() {
        settingComponents.clear();
        int pad = 4;
        int w = width - pad * 2;
        
        GUIKeybindButton kbBtn = new GUIKeybindButton(module, pad, 0, w, 16);
        settingComponents.add(kbBtn);
        
        for (Setting<?> s : module.getSettings()) {
            GUIComponent comp = null;
            if (s instanceof BooleanSetting bs) {
                comp = new GUIToggle(bs, pad, 0, w, 16);
            } else if (s instanceof IntSetting is) {
                comp = new GUISlider(is, pad, 0, w, 20);
            } else if (s instanceof FloatSetting fs) {
                comp = new GUISlider(fs, pad, 0, w, 20);
            } else if (s instanceof ColorSetting cs) {
                comp = new GUIColorPicker(cs, pad, 0, w, 16);
            } else if (s instanceof EnumSetting es) {
                comp = new GUIDropdown(es, pad, 0, w, 16);
            } else if (s instanceof StringSetting ss) {
                comp = new GUITextField(ss, pad, 0, w, 16);
            }
            if (comp != null) {
                settingComponents.add(comp);
            }
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        hovered = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + MODULE_BTN_HEIGHT;
        hoverAnim.springTo(hovered ? 1f : 0f, 200f, 0.8f);
        hoverAnim.update();
        float hp = hoverAnim.get();

        toggleAnim.springTo(module.isEnabled() ? 1f : 0f, 200f, 0.8f);
        toggleAnim.update();
        float tp = toggleAnim.get();

        int contentHeight = MODULE_BTN_HEIGHT;
        if (expanded) {
            for (GUIComponent c : settingComponents) {
                int ch = c.getHeight();
                if (c instanceof GUIColorPicker cp) ch = cp.getExpandedHeight();
                else if (c instanceof GUIDropdown dd) ch = dd.getExpandedHeight();
                contentHeight += ch + 4;
            }
            contentHeight += 4; // Bottom padding
        }
        
        heightAnim.springTo(expanded ? contentHeight : MODULE_BTN_HEIGHT, 250f, 0.9f);
        heightAnim.update();
        this.height = (int) heightAnim.get();

        HazeTheme theme = HazeTheme.get();

        // Module Button Bg
        int bgColor = HazeRenderer.lerpColor(theme.getSurfacePrimary(), theme.getSurfaceElevated(), hp);
        if (tp > 0.01f) {
            bgColor = HazeRenderer.lerpColor(bgColor, theme.getCardOn(), tp);
        }
        HazeRenderer.drawRect(context, x, y, x + width, y + MODULE_BTN_HEIGHT, bgColor);

        // Name
        int textColor = module.isEnabled() ? theme.getTextPrimary() : theme.getTextSecondary();
        HazeRenderer.drawText(context, net.minecraft.client.MinecraftClient.getInstance().textRenderer, module.getName(), x + 8, y + 6, textColor, 1.0f);
        
        // Indicator for settings
        if (!settingComponents.isEmpty()) {
            HazeRenderer.drawText(context, net.minecraft.client.MinecraftClient.getInstance().textRenderer, expanded ? "-" : "+", x + width - 12, y + 6, theme.getTextMuted(), 1.0f);
        }

        if (this.height > MODULE_BTN_HEIGHT + 1) {
            // Draw settings bg
            HazeRenderer.drawRect(context, x, y + MODULE_BTN_HEIGHT, x + width, y + this.height, theme.getSurfaceElevated());
            
            // Scissor contents to animating height
            context.enableScissor(x, y + MODULE_BTN_HEIGHT, x + width, y + this.height);
            
            int yOff = MODULE_BTN_HEIGHT + 4;
            for (GUIComponent c : settingComponents) {
                c.setPosition(x + 4, y + yOff);
                c.render(context, mouseX, mouseY, delta);
                
                int ch = c.getHeight();
                if (c instanceof GUIColorPicker cp) ch = cp.getExpandedHeight();
                else if (c instanceof GUIDropdown dd) ch = dd.getExpandedHeight();
                yOff += ch + 4;
            }
            
            context.disableScissor();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (hovered) {
            if (button == 0) {
                module.toggle();
                return true;
            } else if (button == 1) {
                if (!settingComponents.isEmpty()) {
                    expanded = !expanded;
                }
                return true;
            }
        }
        
        if (expanded && mouseY > y + MODULE_BTN_HEIGHT && mouseY <= y + height && mouseX >= x && mouseX <= x + width) {
            for (GUIComponent c : settingComponents) {
                if (c.mouseClicked(mouseX, mouseY, button)) return true;
            }
        }
        
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (expanded) {
            for (GUIComponent c : settingComponents) {
                c.mouseReleased(mouseX, mouseY, button);
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (expanded) {
            for (GUIComponent c : settingComponents) {
                if (c.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (expanded) {
            for (GUIComponent c : settingComponents) {
                if (c.keyPressed(keyCode, scanCode, modifiers)) return true;
            }
        }
        return false;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (expanded) {
            for (GUIComponent c : settingComponents) {
                if (c.charTyped(chr, modifiers)) return true;
            }
        }
        return false;
    }
}
