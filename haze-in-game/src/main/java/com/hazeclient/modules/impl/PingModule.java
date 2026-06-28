package com.hazeclient.modules.impl;

import com.hazeclient.modules.Module;
import com.hazeclient.render.HazeRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;

public class PingModule extends Module {

    public PingModule() {
        super("Ping", "Displays network latency to server", Category.HUD);
        setX(10);
        setY(50);
        setWidth(50);
        setHeight(18);
        setShowBg(true);
    }

    @Override
    public void render(DrawContext context, float tickDelta) {
        int ping = 0;
        if (mc.getNetworkHandler() != null && mc.player != null) {
            PlayerListEntry entry = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
            if (entry != null) {
                ping = entry.getLatency();
            }
        }
        
        String text = ping + " ms";
        
        int textW = mc.textRenderer.getWidth(text);
        int w = textW + 12;
        int h = 18;
        
        HazeRenderer.drawCenteredText(context, mc.textRenderer, text, w / 2, (h - mc.textRenderer.fontHeight) / 2, getTextColor());
        
        setWidth(w);
        setHeight(h);
    }
}
