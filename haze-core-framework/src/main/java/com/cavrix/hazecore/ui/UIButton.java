package com.cavrix.hazecore.ui;

public class UIButton extends UIComponent {
    private String text;
    private Runnable onClick;

    public UIButton(String text, float x, float y, float width, float height, Runnable onClick) {
        super(x, y, width, height);
        this.text = text;
        this.onClick = onClick;
    }

    @Override
    public void render(float partialTicks, int mouseX, int mouseY) {
        if (!visible) return;
        
        // Render button background (hovered vs normal)
        // Render centered text
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (!visible) return false;

        if (isHovered(mouseX, mouseY) && mouseButton == 0) {
            if (onClick != null) {
                onClick.run();
            }
            return true;
        }
        return false;
    }
}
