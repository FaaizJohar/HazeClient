package com.hazeclient.gui;

import com.hazeclient.modules.Module;
import com.hazeclient.modules.ModuleRegistry;
import com.hazeclient.render.HazeColors;
import com.hazeclient.render.HazeRenderer;
import com.hazeclient.render.HazeTheme;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import java.util.*;

public class HUDEditor extends Screen {

    // ─── Constants ──────────────────────────────────────────────────────
    private static final int GRID_SIZE = 20;
    private static final int SNAP_THRESHOLD = 6;
    private static final int TOOLBAR_HEIGHT = 40;
    private static final int LAYER_PANEL_WIDTH = 150;

    // ─── State ──────────────────────────────────────────────────────────
    private boolean showGrid = true;
    private boolean showSnap = true;
    
    private final Set<Module> selectedModules = new HashSet<>();
    private final Set<Module> draggingModules = new HashSet<>();
    private double dragStartX, dragStartY;
    private final Map<Module, Integer> initialDragX = new HashMap<>();
    private final Map<Module, Integer> initialDragY = new HashMap<>();

    // Snap lines
    private int snapLineX = -1;
    private int snapLineY = -1;
    
    // UI Panels
    private boolean layerPanelOpen = true;

    // Undo / Redo (State = x, y, anchor, zIndex/order)
    private static class ModState {
        int x, y;
        Module.Anchor anchor;
        ModState(int x, int y, Module.Anchor a) { this.x = x; this.y = y; this.anchor = a; }
    }
    private final Deque<Map<Module, ModState>> undoStack = new ArrayDeque<>();
    private final Deque<Map<Module, ModState>> redoStack = new ArrayDeque<>();

    public HUDEditor() {
        super(Text.literal("Advanced HUD Editor"));
    }

    @Override
    protected void init() {
        super.init();
        pushUndoState();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    // ─── Rendering ──────────────────────────────────────────────────────

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        HazeTheme theme = HazeTheme.get();

        // Dim background
        ctx.fillGradient(0, 0, this.width, this.height, HazeColors.withAlpha(theme.getSurfaceBase(), 0.7f), HazeColors.withAlpha(theme.getSurfaceBase(), 0.7f));

        // Grid overlay
        if (showGrid) {
            int gridCol = theme.getCardBorder();
            for (int gx = 0; gx < this.width; gx += GRID_SIZE) {
                ctx.fill(gx, 0, gx + 1, this.height, gridCol);
            }
            for (int gy = 0; gy < this.height; gy += GRID_SIZE) {
                ctx.fill(0, gy, this.width, gy + 1, gridCol);
            }
            HazeRenderer.drawRect(ctx, this.width / 2, 0, this.width / 2 + 1, this.height, HazeColors.withAlpha(theme.getTextPrimary(), 0.2f));
            HazeRenderer.drawRect(ctx, 0, this.height / 2, this.width, this.height / 2 + 1, HazeColors.withAlpha(theme.getTextPrimary(), 0.2f));
        }

        // Snap lines
        if (snapLineX >= 0) HazeRenderer.drawVerticalSnapLine(ctx, snapLineX, this.height, HazeColors.SNAP_LINE);
        if (snapLineY >= 0) HazeRenderer.drawHorizontalSnapLine(ctx, snapLineY, this.width, HazeColors.SNAP_LINE);

        // Render HUD Modules (in reverse z-order so top is last)
        List<Module> hudModules = new ArrayList<>(ModuleRegistry.getModulesByCategory(Module.Category.HUD));
        hudModules.sort(java.util.Comparator.comparingInt(Module::getZIndex));
        
        for (Module mod : hudModules) {
            if (!mod.isEnabled()) continue;
            
            int mw = Math.max(mod.getWidth(), 30);
            int mh = Math.max(mod.getHeight(), 14);
            
            // Push scale/rot (using the settings from the new Module base class)
            HazeRenderer.pushTransform(ctx, mod.getX(), mod.getY(), mod.getScale(), 0f /* rotation not fully in settings yet */);
            
            // Preview actual background if it has one
            if (mod.showBg()) {
                int bgc = HazeRenderer.withAlpha(mod.getBgColor(), mod.getBgOpacity());
                if (mod.getBgStyle() == Module.BackgroundStyle.ROUNDED) {
                    HazeRenderer.drawRoundedRect(ctx, 0, 0, mw, mh, 4, bgc);
                } else if (mod.getBgStyle() == Module.BackgroundStyle.SOLID) {
                    HazeRenderer.drawRect(ctx, 0, 0, mw, mh, bgc);
                }
            }

            // Draw module content
            mod.render(ctx, delta);
            
            // Draw Editor bounds and anchors
            boolean selected = selectedModules.contains(mod);
            if (selected) {
                HazeRenderer.drawBorder(ctx, -2, -2, mw + 4, mh + 4, theme.getAccentColor());
                HazeRenderer.drawGlow(ctx, -2, -2, mw + 4, mh + 4, theme.getAccentColor(), 4);
                
                // Draw anchors
                drawAnchorPoints(ctx, mw, mh, mod.getAnchor(), theme.getAccentColor());
            } else {
                HazeRenderer.drawBorder(ctx, -1, -1, mw + 2, mh + 2, HazeColors.withAlpha(theme.getTextPrimary(), 0.3f));
            }
            
            HazeRenderer.popTransform(ctx);
        }

        // UI Panels
        renderTopToolbar(ctx, mouseX, mouseY, theme);
        renderBottomToolbar(ctx, theme);
        
        if (layerPanelOpen) {
            renderLayerPanel(ctx, mouseX, mouseY, theme, hudModules);
        }

        super.render(ctx, mouseX, mouseY, delta);
    }

    private void drawAnchorPoints(DrawContext ctx, int w, int h, Module.Anchor current, int color) {
        int r = 3;
        int[][] pts = {
            {0,0}, {w/2,0}, {w,0},
            {0,h/2}, {w/2,h/2}, {w,h/2},
            {0,h}, {w/2,h}, {w,h}
        };
        Module.Anchor[] anchors = {
            Module.Anchor.TOP_LEFT, Module.Anchor.TOP_CENTER, Module.Anchor.TOP_RIGHT,
            Module.Anchor.MIDDLE_LEFT, Module.Anchor.MIDDLE_CENTER, Module.Anchor.MIDDLE_RIGHT,
            Module.Anchor.BOTTOM_LEFT, Module.Anchor.BOTTOM_CENTER, Module.Anchor.BOTTOM_RIGHT
        };
        
        for (int i=0; i<9; i++) {
            int c = (anchors[i] == current) ? color : 0xAAFFFFFF;
            HazeRenderer.drawCircle(ctx, pts[i][0], pts[i][1], (anchors[i] == current) ? r+1 : r, c);
        }
    }

    private void renderTopToolbar(DrawContext ctx, int mouseX, int mouseY, HazeTheme theme) {
        HazeRenderer.drawRect(ctx, 0, 0, width, TOOLBAR_HEIGHT, theme.getSurfaceElevated());
        HazeRenderer.drawHorizontalSeparator(ctx, 0, TOOLBAR_HEIGHT, width, theme.getCardBorder());
        
        HazeRenderer.drawText(ctx, textRenderer, "HUD Editor", 12, (TOOLBAR_HEIGHT - textRenderer.fontHeight) / 2, theme.getTextPrimary(), 1.2f);
        
        // Buttons
        int bx = 120;
        bx = drawBtn(ctx, bx, 8, "Grid: " + (showGrid ? "ON" : "OFF"), mouseX, mouseY, theme) + 8;
        bx = drawBtn(ctx, bx, 8, "Snap: " + (showSnap ? "ON" : "OFF"), mouseX, mouseY, theme) + 8;
        bx = drawBtn(ctx, bx, 8, "Layers", mouseX, mouseY, theme) + 8;
        
        if (selectedModules.size() > 1) {
            HazeRenderer.drawVerticalSeparator(ctx, bx, 8, 24, theme.getCardBorder());
            bx += 8;
            bx = drawBtn(ctx, bx, 8, "Align L", mouseX, mouseY, theme) + 4;
            bx = drawBtn(ctx, bx, 8, "Align C", mouseX, mouseY, theme) + 4;
            bx = drawBtn(ctx, bx, 8, "Align R", mouseX, mouseY, theme) + 8;
            bx = drawBtn(ctx, bx, 8, "Align T", mouseX, mouseY, theme) + 4;
            bx = drawBtn(ctx, bx, 8, "Align M", mouseX, mouseY, theme) + 4;
            bx = drawBtn(ctx, bx, 8, "Align B", mouseX, mouseY, theme) + 8;
        }
    }
    
    private int drawBtn(DrawContext ctx, int x, int y, String text, int mx, int my, HazeTheme theme) {
        int w = textRenderer.getWidth(text) + 16;
        int h = 24;
        boolean hover = mx >= x && mx <= x + w && my >= y && my <= y + h;
        HazeRenderer.drawRoundedRect(ctx, x, y, w, h, 4, hover ? theme.getSurfaceElevated() : theme.getSurfacePrimary());
        HazeRenderer.drawRoundedBorder(ctx, x, y, w, h, 4, theme.getCardBorder());
        HazeRenderer.drawCenteredText(ctx, textRenderer, text, x + w/2, y + (h - textRenderer.fontHeight)/2, theme.getTextPrimary());
        return x + w;
    }

    private void renderBottomToolbar(DrawContext ctx, HazeTheme theme) {
        int ty = this.height - TOOLBAR_HEIGHT;
        HazeRenderer.drawRect(ctx, 0, ty, this.width, TOOLBAR_HEIGHT, theme.getSurfaceElevated());
        HazeRenderer.drawHorizontalSeparator(ctx, 0, ty, width, theme.getCardBorder());

        if (selectedModules.size() == 1) {
            Module m = selectedModules.iterator().next();
            String info = String.format("%s | X:%d Y:%d | Anchor: %s | Scale: %.1f", 
                m.getName(), m.getX(), m.getY(), m.getAnchor().name(), m.getScale());
            ctx.drawText(this.textRenderer, info, 12, ty + (TOOLBAR_HEIGHT - textRenderer.fontHeight)/2, theme.getTextPrimary(), true);
        } else if (selectedModules.size() > 1) {
            ctx.drawText(this.textRenderer, selectedModules.size() + " modules selected", 12, ty + (TOOLBAR_HEIGHT - textRenderer.fontHeight)/2, theme.getAccentColor(), true);
        } else {
            ctx.drawText(this.textRenderer, "No module selected. Click to select, Ctrl+Click for multi-select.", 12, ty + (TOOLBAR_HEIGHT - textRenderer.fontHeight)/2, theme.getTextSecondary(), true);
        }
    }
    
    private void renderLayerPanel(DrawContext ctx, int mx, int my, HazeTheme theme, List<Module> modules) {
        int py = TOOLBAR_HEIGHT + 8;
        int px = width - LAYER_PANEL_WIDTH - 8;
        int ph = height - py - TOOLBAR_HEIGHT - 8;
        
        HazeRenderer.drawRoundedRect(ctx, px, py, LAYER_PANEL_WIDTH, ph, 4, theme.getSurfaceElevated());
        HazeRenderer.drawRoundedBorder(ctx, px, py, LAYER_PANEL_WIDTH, ph, 4, theme.getCardBorder());
        HazeRenderer.drawText(ctx, textRenderer, "Visibility & Layers", px + 8, py + 8, theme.getTextPrimary(), 1.0f);
        
        int ly = py + 24;
        for (int i = modules.size() - 1; i >= 0; i--) { // Top down
            Module m = modules.get(i);
            boolean sel = selectedModules.contains(m);
            boolean hover = mx >= px && mx <= px + LAYER_PANEL_WIDTH && my >= ly && my <= ly + 18;
            
            int bg = sel ? HazeColors.withAlpha(theme.getAccentColor(), 0.3f) : (hover ? theme.getSurfaceElevated() : 0);
            if (bg != 0) HazeRenderer.drawRect(ctx, px + 2, ly, px + LAYER_PANEL_WIDTH - 2, ly + 18, bg);
            
            HazeRenderer.drawText(ctx, textRenderer, m.getName(), px + 24, ly + 5, m.isEnabled() ? theme.getTextPrimary() : theme.getTextMuted(), 1.0f);
            
            // Visibility icon (eye stub)
            HazeRenderer.drawCircle(ctx, px + 12, ly + 9, 3, m.isEnabled() ? theme.getTextPrimary() : theme.getTextMuted());
            
            // Up/Down arrows if selected
            if (sel) {
                HazeRenderer.drawText(ctx, textRenderer, "^", px + LAYER_PANEL_WIDTH - 30, ly + 5, theme.getTextPrimary(), 1.0f);
                HazeRenderer.drawText(ctx, textRenderer, "v", px + LAYER_PANEL_WIDTH - 15, ly + 5, theme.getTextPrimary(), 1.0f);
            }

            ly += 18;
            if (ly > py + ph - 20) break; // basic clip
        }
    }

    // ─── Mouse Interactions ─────────────────────────────────────────────

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Toolbar clicks
        if (mouseY < TOOLBAR_HEIGHT) {
            handleToolbarClick(mouseX, mouseY);
            return true;
        }
        if (layerPanelOpen && mouseX > width - LAYER_PANEL_WIDTH - 8 && mouseY > TOOLBAR_HEIGHT + 8 && mouseY < height - TOOLBAR_HEIGHT - 8) {
            handleLayerPanelClick(mouseX, mouseY);
            return true;
        }

        // Module selection
        List<Module> hudModules = ModuleRegistry.getModulesByCategory(Module.Category.HUD);
        
        Module clicked = null;
        for (int i = hudModules.size() - 1; i >= 0; i--) {
            Module mod = hudModules.get(i);
            if (!mod.isEnabled()) continue;
            int mw = Math.max(mod.getWidth(), 30);
            int mh = Math.max(mod.getHeight(), 14);
            float scale = mod.getScale();
            
            if (mouseX >= mod.getX() && mouseX <= mod.getX() + mw * scale &&
                mouseY >= mod.getY() && mouseY <= mod.getY() + mh * scale) {
                clicked = mod;
                break;
            }
        }
        
        if (clicked != null) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                if (hasControlDown()) {
                    if (selectedModules.contains(clicked)) selectedModules.remove(clicked);
                    else selectedModules.add(clicked);
                } else {
                    if (!selectedModules.contains(clicked)) {
                        selectedModules.clear();
                        selectedModules.add(clicked);
                    }
                }
                
                // Start drag
                draggingModules.clear();
                draggingModules.addAll(selectedModules);
                dragStartX = mouseX;
                dragStartY = mouseY;
                initialDragX.clear();
                initialDragY.clear();
                for (Module m : draggingModules) {
                    initialDragX.put(m, m.getX());
                    initialDragY.put(m, m.getY());
                }
                pushUndoState();
            } else if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                // Change anchor point logic here in future, or open specific module settings
            }
            return true;
        }
        
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            selectedModules.clear();
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    private void handleToolbarClick(double mx, double my) {
        int bx = 120;
        if (btnClick(bx, 8, "Grid: ON", mx, my)) { showGrid = !showGrid; return; } bx += 100;
        if (btnClick(bx, 8, "Snap: ON", mx, my)) { showSnap = !showSnap; return; } bx += 100;
        if (btnClick(bx, 8, "Layers", mx, my)) { layerPanelOpen = !layerPanelOpen; return; } bx += 80;
        
        if (selectedModules.size() > 1) {
            bx += 8;
            if (btnClick(bx, 8, "Align L", mx, my)) { alignSelect(0); return; } bx += 60;
            if (btnClick(bx, 8, "Align C", mx, my)) { alignSelect(1); return; } bx += 60;
            if (btnClick(bx, 8, "Align R", mx, my)) { alignSelect(2); return; } bx += 60;
            if (btnClick(bx, 8, "Align T", mx, my)) { alignSelect(3); return; } bx += 60;
            if (btnClick(bx, 8, "Align M", mx, my)) { alignSelect(4); return; } bx += 60;
            if (btnClick(bx, 8, "Align B", mx, my)) { alignSelect(5); return; }
        }
    }
    
    private boolean btnClick(int x, int y, String text, double mx, double my) {
        int w = textRenderer.getWidth(text) + 16;
        int h = 24;
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }
    
    private void alignSelect(int mode) {
        pushUndoState();
        int minX = 9999, maxX = -9999, minY = 9999, maxY = -9999;
        for (Module m : selectedModules) {
            minX = Math.min(minX, m.getX());
            maxX = Math.max(maxX, (int)(m.getX() + m.getWidth() * m.getScale()));
            minY = Math.min(minY, m.getY());
            maxY = Math.max(maxY, (int)(m.getY() + m.getHeight() * m.getScale()));
        }
        for (Module m : selectedModules) {
            int w = (int)(m.getWidth() * m.getScale());
            int h = (int)(m.getHeight() * m.getScale());
            switch (mode) {
                case 0: m.setX(minX); break; // L
                case 1: m.setX(minX + (maxX - minX)/2 - w/2); break; // C
                case 2: m.setX(maxX - w); break; // R
                case 3: m.setY(minY); break; // T
                case 4: m.setY(minY + (maxY - minY)/2 - h/2); break; // M
                case 5: m.setY(maxY - h); break; // B
            }
        }
    }
    
    private void handleLayerPanelClick(double mx, double my) {
        int py = TOOLBAR_HEIGHT + 8;
        int px = width - LAYER_PANEL_WIDTH - 8;
        int ly = py + 24;
        List<Module> hudModules = new ArrayList<>(ModuleRegistry.getModulesByCategory(Module.Category.HUD));
        for (int i = hudModules.size() - 1; i >= 0; i--) {
            Module m = hudModules.get(i);
            if (my >= ly && my <= ly + 18) {
                // Clicked eye?
                if (mx >= px + 4 && mx <= px + 20) {
                    m.setEnabled(!m.isEnabled());
                } else {
                    if (hasControlDown()) {
                        if (selectedModules.contains(m)) selectedModules.remove(m);
                        else selectedModules.add(m);
                    } else {
                        boolean sel = selectedModules.contains(m);
                        if (sel && mx >= px + LAYER_PANEL_WIDTH - 30 && mx <= px + LAYER_PANEL_WIDTH - 20) {
                            m.setZIndex(m.getZIndex() + 1);
                        } else if (sel && mx >= px + LAYER_PANEL_WIDTH - 15 && mx <= px + LAYER_PANEL_WIDTH - 5) {
                            m.setZIndex(m.getZIndex() - 1);
                        } else {
                            selectedModules.clear();
                            selectedModules.add(m);
                        }
                    }
                }
                break;
            }
            ly += 18;
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!draggingModules.isEmpty() && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            double dx = mouseX - dragStartX;
            double dy = mouseY - dragStartY;
            
            snapLineX = -1;
            snapLineY = -1;
            
            // If dragging single module, do snap logic
            if (draggingModules.size() == 1 && showSnap) {
                Module m = draggingModules.iterator().next();
                int newX = (int) (initialDragX.get(m) + dx);
                int newY = (int) (initialDragY.get(m) + dy);
                
                int w = (int)(m.getWidth() * m.getScale());
                int h = (int)(m.getHeight() * m.getScale());
                
                int scX = width / 2;
                int scY = height / 2;
                
                if (Math.abs(newX + w/2 - scX) < SNAP_THRESHOLD) { newX = scX - w/2; snapLineX = scX; }
                if (Math.abs(newY + h/2 - scY) < SNAP_THRESHOLD) { newY = scY - h/2; snapLineY = scY; }
                
                if (showGrid && snapLineX < 0 && snapLineY < 0) {
                    int gx = Math.round((float) newX / GRID_SIZE) * GRID_SIZE;
                    int gy = Math.round((float) newY / GRID_SIZE) * GRID_SIZE;
                    if (Math.abs(newX - gx) < SNAP_THRESHOLD) newX = gx;
                    if (Math.abs(newY - gy) < SNAP_THRESHOLD) newY = gy;
                }
                
                m.setX(newX);
                m.setY(newY);
            } else {
                for (Module m : draggingModules) {
                    m.setX((int) (initialDragX.get(m) + dx));
                    m.setY((int) (initialDragY.get(m) + dy));
                }
            }
            
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT && !draggingModules.isEmpty()) {
            draggingModules.clear();
            snapLineX = -1;
            snapLineY = -1;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    // ─── Keyboard Interactions ──────────────────────────────────────────

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_Z && hasControlDown()) { undo(); return true; }
        if (keyCode == GLFW.GLFW_KEY_Y && hasControlDown()) { redo(); return true; }
        
        if (!selectedModules.isEmpty()) {
            int step = hasShiftDown() ? 10 : 1;
            if (keyCode == GLFW.GLFW_KEY_LEFT) { selectedModules.forEach(m -> m.setX(m.getX() - step)); return true; }
            if (keyCode == GLFW.GLFW_KEY_RIGHT) { selectedModules.forEach(m -> m.setX(m.getX() + step)); return true; }
            if (keyCode == GLFW.GLFW_KEY_UP) { selectedModules.forEach(m -> m.setY(m.getY() - step)); return true; }
            if (keyCode == GLFW.GLFW_KEY_DOWN) { selectedModules.forEach(m -> m.setY(m.getY() + step)); return true; }
            
            if (keyCode == GLFW.GLFW_KEY_DELETE) {
                selectedModules.forEach(m -> m.setEnabled(false));
                selectedModules.clear();
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    // ─── Undo / Redo ────────────────────────────────────────────────────
    private void pushUndoState() {
        Map<Module, ModState> state = new HashMap<>();
        for (Module mod : ModuleRegistry.getModulesByCategory(Module.Category.HUD)) {
            state.put(mod, new ModState(mod.getX(), mod.getY(), mod.getAnchor()));
        }
        undoStack.push(state);
        if (undoStack.size() > 64) undoStack.removeLast();
        redoStack.clear();
    }
    private void undo() {
        if (undoStack.size() <= 1) return;
        redoStack.push(undoStack.pop());
        applyState(undoStack.peek());
    }
    private void redo() {
        if (redoStack.isEmpty()) return;
        Map<Module, ModState> state = redoStack.pop();
        undoStack.push(state);
        applyState(state);
    }
    private void applyState(Map<Module, ModState> state) {
        if (state == null) return;
        for (var entry : state.entrySet()) {
            entry.getKey().setX(entry.getValue().x);
            entry.getKey().setY(entry.getValue().y);
            entry.getKey().setAnchor(entry.getValue().anchor);
        }
    }
}
