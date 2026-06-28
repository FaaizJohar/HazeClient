package com.hazeclient.modules.impl;

import com.hazeclient.modules.Module;
import net.minecraft.client.gui.DrawContext;

public class ServerInfoModule extends Module {
    public ServerInfoModule() {
        super("ServerInfo", "Shows current server name and IP", Category.HUD);
        setX(10);
        setY(94);
        setWidth(120);
        setHeight(10);
    }

    @Override
    public void render(DrawContext context, float tickDelta) {
        String text;
        if (mc.getCurrentServerEntry() != null) {
            text = "Server: " + mc.getCurrentServerEntry().address;
        } else if (mc.isIntegratedServerRunning()) {
            text = "Singleplayer";
        } else {
            text = "Not connected";
        }
        context.drawTextWithShadow(mc.textRenderer, text, getX(), getY(), getTextColor());
        setWidth(mc.textRenderer.getWidth(text));
    }
}
