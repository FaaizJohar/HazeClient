package com.hazeclient.modules.impl;

import com.hazeclient.modules.Module;
import com.hazeclient.modules.settings.BooleanSetting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import java.util.ArrayList;
import java.util.List;

public class ArmorModule extends Module {

    private final BooleanSetting vertical;

    public ArmorModule() {
        super("Armor", "Displays equipped armor items and durability", Category.HUD);
        setX(10);
        setY(90);
        setWidth(80);
        setHeight(20);
        setShowBg(false);
        
        vertical = addSetting(new BooleanSetting("Vertical", "Stack items vertically", false));
    }

    @Override
    public void render(DrawContext context, float tickDelta) {
        if (mc.player == null) return;
        
        List<ItemStack> armor = new ArrayList<>();
        mc.player.getArmorItems().forEach(armor::add);
        // Usually renders boot -> helmet, let's reverse to helmet -> boot
        java.util.Collections.reverse(armor);
        
        int xOff = 0;
        int yOff = 0;
        boolean isVertical = vertical.get();
        
        for (ItemStack item : armor) {
            if (item.isEmpty()) continue;
            
            context.drawItem(item, xOff, yOff);
            context.drawItemInSlot(mc.textRenderer, item, xOff, yOff);
            
            if (isVertical) {
                yOff += 20;
            } else {
                xOff += 20;
            }
        }
        
        if (isVertical) {
            setWidth(20);
            setHeight(Math.max(20, yOff));
        } else {
            setWidth(Math.max(20, xOff));
            setHeight(20);
        }
    }
}
